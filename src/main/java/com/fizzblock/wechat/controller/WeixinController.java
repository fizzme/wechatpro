package com.fizzblock.wechat.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSONObject;
import com.fizzblock.wechat.response.Article;
import com.fizzblock.wechat.response.NewsMessage;
import com.fizzblock.wechat.response.TextMessage;
import com.fizzblock.wechat.service.ShopTicketTemplateMsgService;
import com.fizzblock.wechat.service.TemplateMessageService;
import com.fizzblock.wechat.service.UserService;
import com.fizzblock.wechat.service.UserWaitQueueImpl;
import com.fizzblock.wechat.template.BDResource;
import com.fizzblock.wechat.template.ResumeFeedBack;
import com.fizzblock.wechat.template.ResumeSend;
import com.fizzblock.wechat.template.TakeTicket;
import com.fizzblock.wechat.template.TicketWait;
import com.fizzblock.wechat.util.apis.WeiXinApis;
import com.fizzblock.wechat.util.common.XStreamMessageUtil;
import com.fizzblock.wechat.util.common.impl.MessageUtil;
import com.fizzblock.wechat.util.common.impl.SignCheckUtil;
import com.fizzblock.wechat.util.redis.JedisOper;
import com.fizzblock.wechat.util.redis.JedisPoolUtil;

@Controller
public class WeixinController {
    private static final String redirect_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Ffizzblock.bceapp.com%2FgetSNSUserinfo2.do&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";  
    //以ip方式测试授权
    private static final String redirect_url2 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Ffizzblock.bceapp.com%2FgetSNSUserinfo.do&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";  

    //这个地址不对
    private static final String redirect_url_login = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Ffizzblock.bceapp.com%2FgetUserInfoBind.do&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";  
    
    //简历模板消息service
    @Autowired
    @Qualifier("templateService")
    TemplateMessageService templateService ;
    
    
    //排队取号模板消息service
    @Autowired
    @Qualifier("ticketTemplateService")
    ShopTicketTemplateMsgService ticketTemplateService;
    
    
    JedisOper jedisOper = null;
    
    UserService userService = null;
    
    //队列服务，基于redis
    UserWaitQueueImpl userWaitQueue = null;
    
    /**		
     * 接收微信服务器验证消息
     * @param request
     * @param response
     * @param model
     * @throws IOException 
     */
	@RequestMapping(value = "/wx" ,method=RequestMethod.GET)
	public void receiveServer(HttpServletRequest request,HttpServletResponse response) throws IOException {

		//日志日期的格式化处理
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒SSS毫秒");
		Date date = new Date();
		String time = sdf.format(date);

		//获取响应
		response.setContentType("text/html;charset=utf-8"); //设置输出编码格式
		PrintWriter out = response.getWriter();

		//判断请求的类型，微信验证使用的是get请求，其余post请求
		System.out.println(time+"：GET请求 [微信服务端发来消息]：开始签名校验");
		
		//获取微信校验参数
		String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        //空参数则返回，不执行校验
        if(signature==null||timestamp==null||nonce==null||"".equals(signature)||"".equals(timestamp)||"".equals(nonce)){
        	System.out.println("出现微信校验请求参数为空");
        	return ;
        }
        String msg = "获取到的相关wx请求校验信息，{signature:%s timestamp:%s nonce:%s echostr:%s}";
        System.out.println("校验消息输出"+String.format(msg, signature,timestamp,nonce,echostr));
        
		//参数非空进行校验
		if (SignCheckUtil.checkSignature(signature, timestamp, nonce)) {
			System.out.println(time+"：签名校验通过。");
			out.print(echostr);
			out.close();
			return ;
		}
		
		System.out.println(time+"：签名校验失败。");
		out.close();
		out = null;
	}
	
	
	/**
	 * 接收微信服务器转发用户操作请求
	 * @param request
	 * @param response
	 * @param model
	 * @throws IOException 
	 */
	@RequestMapping(value = "/wx" ,method=RequestMethod.POST)
	@ResponseBody
	public String wxRequestDispatcher(HttpServletRequest request) throws IOException {
		
		//xml格式响应给用户的消息数据
		String responseXml = null;
		System.out.println(">>>>>>>>>>>>>>>获取模板消息的service实例："+templateService);
		try{
	        // 从request中取得输入流
	        InputStream inputStream = request.getInputStream();
	        
	        //获取请求的xml内容
	        String requestXml = MessageUtil.getStrFromInputSteam(inputStream);
	        System.out.println(getDate()+"--->请求的xml内容："+requestXml);
	        
			//调用parseXml方法解析请求消息
			Map<String,String > requestMap = MessageUtil.parseXml(requestXml);
			
			//获取请求相关信息
			String msgType = requestMap.get("MsgType");
			
			//判断请求消息的类型，响应不同的消息回复
			if(MessageUtil.REQ_MESSAGE_TYPE_TEXT.equals(msgType)){//文本类型
				return textMessageResponse(requestMap,requestXml);
			}else if(MessageUtil.REQ_MESSAGE_TYPE_IMAGE.equals(msgType)){//图片类型
				return imageMessageResponse(requestMap,requestXml);
			}else if(MessageUtil.REQ_MESSAGE_TYPE_LINK.equals(msgType)){//链接类型
				return linkMessageResponse(requestMap,requestXml);
			}else if(MessageUtil.REQ_MESSAGE_TYPE_VOICE.equals(msgType)){//语音类型
				return voiceMessageResponse(requestMap,requestXml);
			}else if(MessageUtil.REQ_MESSAGE_TYPE_EVENT.equals(msgType)){//事件类型
				return eventMessageResponse(requestMap,requestXml);
			}

			//默认响应返回的文本消息
			TextMessage textMsg = initTextMessage(requestMap);
			textMsg.setContent("未实现的消息类型");
			//响应返回
			responseXml =  MessageUtil.messageToXml(textMsg);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return responseXml;
	}


	//返回格式化日期
	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String nowDate =df.format(new Date());
		return nowDate;
	}


	//初始化的文本消息类型
	private TextMessage initTextMessage(Map<String, String> requestMap) {
		//获取请求相关信息
		String fromUserName = requestMap.get("FromUserName");
		String toUserName =requestMap.get("ToUserName");
		
		//响应的文本消息初始化
		TextMessage textMsg = new TextMessage();
		textMsg.setFromUserName(toUserName);
		textMsg.setToUserName(fromUserName);
		textMsg.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		textMsg.setCreateTime(System.currentTimeMillis());
		
		return textMsg;
	}


	//事件消息类型，需要对事件消息的具体类型进行判断
	private String eventMessageResponse(Map<String, String> requestMap,String requestXml) {

		String eventType = requestMap.get("Event");//获取具体的事件类型
		
		if(MessageUtil.EVENT_TYPE_CLICK.equals(eventType)){//菜单点击事件
			String menuName = requestMap.get("EventKey");
			return menuClickResponse(menuName,requestMap,requestXml);
		}else if(MessageUtil.EVENT_TYPE_SUBSCRIBE.equals(eventType)){//用户关注事件，调用授权页面
			return userSubscribeResponse(requestMap,requestXml);
		}else if(MessageUtil.EVENT_TYPE_UNSUBSCRIBE.equals(eventType)){//用户取关事件
			return userUnSubscribeResponse(requestXml);
		}
		
		//默认响应返回的文本消息
		TextMessage textMsg = initTextMessage(requestMap);
		textMsg.setContent("未实现的事件类型");
		return MessageUtil.messageToXml(textMsg);
	}


	private String voiceMessageResponse(Map<String,String > requestMap,String requestXml) {
		//默认响应返回的文本消息
		TextMessage textMsg = initTextMessage(requestMap);
		textMsg.setContent("你发送的是语音消息");
		return MessageUtil.messageToXml(textMsg);
	}


	private String linkMessageResponse(Map<String,String > requestMap,String requestXml) {
		TextMessage textMsg = initTextMessage(requestMap);
		textMsg.setContent("你发送的是链接消息");
		return MessageUtil.messageToXml(textMsg);
	}


	private String imageMessageResponse(Map<String,String > requestMap,String requestXml) {
		TextMessage textMsg = initTextMessage(requestMap);
		textMsg.setContent("你发送的是图片消息");
		return MessageUtil.messageToXml(textMsg);
	}


	private String userUnSubscribeResponse(String requestXml) {
		// TODO Auto-generated method stub
		System.out.println(getDate()+">>>>>>>>>用户取关");
		return null;
	}


	/**
	 * 用户关注公众号响应
	 * @param requestMap
	 * @param requestXml
	 * @return
	 */
	private String userSubscribeResponse(Map<String,String > requestMap,String requestXml) {
		
		System.out.println(getDate()+">>>>>>>>>用户关注:"+requestXml);
		String message = "你好，欢迎关注/:circle/:circle~~\n\n"
						+"初来乍到请多多指教哟！[Hey][Hey]\n\n"
						+"回复：绑定，进行绑定操作/:@)\n\n"
						+"回复：资源，可以获取更多的资源哦[Concerned][Concerned]\n\n"
						+"回复：图文，可以查看往期文章哟[Smirk][Smirk]\n\n"
						+"另外选择我的招聘菜单，可以进行简历投递和查看[Concerned][Concerned]\n\n";
		
		TextMessage textMsg = initTextMessage(requestMap);
		textMsg.setContent(message);
		return MessageUtil.messageToXml(textMsg);
	}


	//菜单点击事件处理
	//添加投递简历菜单和点击事件处理
	private String menuClickResponse(String menuName, Map<String,String > requestMap,String requestXml) {
		String user =  requestMap.get("FromUserName");
		
		//如果选择了下载资源返回一个文本消息
		if("btn_download".equals(menuName)){
			//普通文本消息
			TextMessage textMsg = initTextMessage(requestMap);
    		String message = "回复如下信息获取相关资源： \n"+
					"1.回复 ：chanpin  可以《获取产品运营训练营—腾讯产品经理》[Hey]  \n\n "+
					"2.回复 ：linux  获取《linux命令行与shell编程大全》电子书资源  [Smirk]\n\n "+
					"3.回复 ：git  获取《完全学会GIT SERVER的24堂课》电子书资源  /:jj\n\n "+
					"4.回复 ：springboot  获取《JavaEE开发的颠覆者springboot实战》电子书资源 [Yeah!] \n\n "+
					"5.回复 ：design  获取《JAVA设计模式深入研究》电子书资源  /:hug \n\n "+
					"6.回复 ：es  获取《深入理解ElasticSearch》电子书资源 /::* \n\n "+
					"更多资源敬请期待...\n\n";
    		textMsg.setContent(message);
    		return MessageUtil.messageToXml(textMsg);
		}
		
		//如果选择了绑定，则发送一条图文
		if("bindNew".equals(menuName)){
			String fromUser = requestMap.get("ToUserName");
			String toUser =requestMap.get("FromUserName");
			
			//图文基本使用的
			NewsMessage newsMessage = new NewsMessage();
			newsMessage.setFromUserName(fromUser);
			newsMessage.setToUserName(toUser);
			newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
			newsMessage.setCreateTime(System.currentTimeMillis());
    		//构造文章内容
    		Article article1 = new Article();
    		article1.setTitle("用户绑定");
    		article1.setPicUrl("http://img.178.com/acg1/201801/310592749361/310592753623.jpg");
    		article1.setUrl("http://acg.178.com/201801/310353320606.html");
    		article1.setDescription("等你很久了，快来加入吧");
    		
    		//装入集合
    		List<Article> articles = new ArrayList<>();
    		articles.add(article1);
    		newsMessage.setArticleCount(articles.size());//设置文章数为2
    		newsMessage.setArticles(articles);
    		
    		System.out.println(">>>>>>>>>>>>>>>>>图文消息格式化结果...");
    		return MessageUtil.messageToXml(newsMessage);
		}
		
		try {
			//投递简历
			if("sendResume".equals(menuName)){
				ResumeSend resumeSend = new ResumeSend();
				resumeSend.setFirst("您好! 您的简历投递成功");
				resumeSend.setCompany("北京58同城信息技术有限公司");
				resumeSend.setUrl("http://www.baidu.com");
				resumeSend.setJob("产品经理");
				templateService.sendResumeSubmitTemplate(user, resumeSend);
				return "";
			}
				//查看简历状态
			if("resumeStatus".equals(menuName)){
				ResumeFeedBack resumeFeedBack = new ResumeFeedBack();
				resumeFeedBack.setFirst("您好! 您投递的简历有新的反馈");
				resumeFeedBack.setCompany("北京58同城信息技术有限公司");
				resumeFeedBack.setUrl("http://www.baidu.com");
				resumeFeedBack.setJob("产品经理");
				resumeFeedBack.setResult("已查看");
				templateService.sendResumeFeedbackTemplate(user, resumeFeedBack);
				return "";
			}
		} catch (IOException e) {
			e.printStackTrace();
			//其他的响应返回
			TextMessage textMsg = initTextMessage(requestMap);
			textMsg.setContent("你点击了菜单："+ menuName);
			return MessageUtil.messageToXml(textMsg);
		}
		
		//取号和排队服务需要初始化redis服务
//		String args = "192.168.248.151:6379";
//		String args = "118.25.4.250:6379";
		String args = "127.0.0.1:6379";
		JedisPool jedisPool = initJedisPool(args);
		JedisOper jedisOper= new JedisOper(jedisPool);
		
		userService = new UserService(jedisOper);
		userWaitQueue = new UserWaitQueueImpl(jedisPool);
		//添加排队相关的事件按钮操作
		//取号
		if("takeTicket".equals(menuName)){
			//获取用户信息，昵称等
			String nickname = "";
			try {
				String userStr = getUserInfoByOpenId(user);
				nickname = JSONObject.parseObject(userStr).getString("nickname");
			} catch (IOException e) {
				System.out.println("获取用户信息失败");
				e.printStackTrace();
				return "";
			}
			if(userWaitQueue.search(user)>=0){
				
				System.out.println("用户已经存在");
				long index = userWaitQueue.search(user);
				String msg = String.format("用户:%s,您已经取票，您是 是第%s个用户，前面还有%s个人。\n查看具体消息，请选择排队进度菜单",nickname, (index+1)+"",index+"");
				System.out.println(msg);
				//普通文本消息
				TextMessage textMsg = initTextMessage(requestMap);
				textMsg.setContent(msg);
			 return MessageUtil.messageToXml(textMsg);	
			}
			
			//添加用户的openid到排队队列中
			userWaitQueue.push(user);
			
			long index = userWaitQueue.search(user);
			System.out.println(String.format("c用户是第%s个用户，前面还有%s个人", (index+1)+"",index+""));
			
			//根据当前排队进度信息拼接模板消息
			System.out.println(">>>>>>>>>>>开始发送取号模板消息");
			//格式化日期
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
			String dateTime  = df.format(new Date());
			
			TakeTicket takeTicket = new TakeTicket();
			takeTicket.setFirst("您已成功排队取号!");
			takeTicket.setNickname(nickname);
			takeTicket.setShopname("豆捞坊(锦艺城店)");
			takeTicket.setNumber("小桌 3009");
			takeTicket.setBefore(index+"桌");
			takeTicket.setRemark(dateTime);
			
			//发送模板消息
			try {
				ticketTemplateService.sendTakeTicketTemplateMsg(user, takeTicket);
			} catch (IOException e) {
				System.out.println("发送模板消息失败....");
				e.printStackTrace();
			}
			
			return "";
		}
		
		//查看排队进度
		if("ticketWait".equals(menuName)){
			//普通文本消息
			long index = userWaitQueue.search(user);
			System.out.println(String.format("c用户是第%s个用户，前面还有%s个人", (index+1)+"",index+""));
			System.out.println(">>>>>>>>>>>开始发送排号模板消息");
			
			//格式化日期
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
			String dateTime  = df.format(new Date());
			
			TicketWait ticketWait = new TicketWait();
			ticketWait.setFirst("");
			ticketWait.setShopname("豆捞坊(锦艺城店)");
			ticketWait.setNumber("小桌 3009");
			ticketWait.setBefore(index+"桌");
			ticketWait.setWaitTime(">30分钟(仅供参考)");
			ticketWait.setStatus("排队中");
			ticketWait.setRemark(dateTime);
			
			try {
				ticketTemplateService.sendTicketWaitTemplateMsg(user, ticketWait);
			} catch (IOException e) {
				System.out.println("发送模板消息失败....");
				e.printStackTrace();
			}
			
			return "";
		}
		
		
		//其他的响应返回
		TextMessage textMsg = initTextMessage(requestMap);
		textMsg.setContent("你点击了菜单："+ menuName);
		return MessageUtil.messageToXml(textMsg);
	}


	
	//获取用户基本信息
	private String getUserInfoByOpenId(String openId) throws IOException {
		//先从缓存中获取
		String user = userService.getUserInfo(openId);
		System.out.println("从缓存中获取结果："+user);
		
		//缓存中没有则从服务端获取
		if(null == user||"".equals(user)){
			//获取accessToken
			String accessToken =WeiXinApis.fetcheAccessToken() ;
			//调用微信api
			user = WeiXinApis.getFansInfo(openId,accessToken);
			//加入缓存
			userService.addUserInfo(openId, user);
			System.out.println("缓存未命中，从微信服务器拉取用户信息，再次放入缓存");
			System.out.println("用户信息："+user);
			return user;
		}
			
		System.out.println("缓存命中，从缓存中取用户信息");
		System.out.println("用户信息："+user);
		return user;
	}

	public JedisPool initJedisPool(String args){
		
		String [] params =args.split(":");
		
		String host = params[0];//主机名
		int port = Integer.parseInt(params[1]);
		System.out.println(String.format("host主机：%s port端口：%d", host,port));
		JedisPool jedisPool =null;
		
		try{
			//初始化jedis连接池
			jedisPool = JedisPoolUtil.getJedisPool(host, port);
		}catch(Exception ex){
			System.out.println("初始化jedis连接池失败："+ex.getCause().toString());
			return null;
		}
		//初始化jedis操作类

		return jedisPool;
}

	//文本消息响应
	private String textMessageResponse(Map<String,String > requestMap,String requestXml) {
		//解析文本对象
		com.fizzblock.wechat.request.TextMessage textMessageReq = MessageUtil.xmlToMessage(requestXml, com.fizzblock.wechat.request.TextMessage.class);
		//获取文本内容
		String content = textMessageReq.getContent().trim();
		System.out.println("获取的用户请求的文本消息内容："+content);
		String respContent= null ;
		
		String fromUser = textMessageReq.getToUserName();
		String toUser = textMessageReq.getFromUserName();
		
		//图文基本使用的
		NewsMessage newsMessage = new NewsMessage();
		newsMessage.setFromUserName(fromUser);
		newsMessage.setToUserName(toUser);
		newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
		newsMessage.setCreateTime(System.currentTimeMillis());

		//普通文本消息
		TextMessage textMsg = initTextMessage(requestMap);
		
		BDResource baiduResource = null;
		
		switch(content){
		
    	case "绑定":
    		
    		//构造文章内容
    		Article article1 = new Article();
    		article1.setTitle("用户绑定");
    		article1.setPicUrl("http://img.178.com/acg1/201801/310592749361/310592753623.jpg");
    		article1.setUrl("http://acg.178.com/201801/310353320606.html");
    		article1.setDescription("等你很久了，快来加入吧");
    		
    		//装入集合
    		List<Article> articles = new ArrayList<>();
    		articles.add(article1);
    		newsMessage.setArticleCount(articles.size());//设置文章数为2
    		newsMessage.setArticles(articles);
    		
    		System.out.println(">>>>>>>>>>>>>>>>>图文消息格式化结果...");
    		respContent =  XStreamMessageUtil.messageToXml(newsMessage);
    		break;
    	case "资源":
    		String message = "回复如下信息获取相关资源： \n"+
					"1.回复 ：chanpin  可以《获取产品运营训练营—腾讯产品经理》[Hey]  \n\n "+
					"2.回复 ：linux  获取《linux命令行与shell编程大全》电子书资源  [Smirk]\n\n "+
					"3.回复 ：git  获取《完全学会GIT SERVER的24堂课》电子书资源  /:jj\n\n "+
					"4.回复 ：springboot  获取《JavaEE开发的颠覆者springboot实战》电子书资源 [Yeah!] \n\n "+
					"5.回复 ：design  获取《JAVA设计模式深入研究》电子书资源  /:hug \n\n "+
					"6.回复 ：es  获取《深入理解ElasticSearch》电子书资源 /::* \n\n "+
					"更多资源敬请期待...\n\n";
    		textMsg.setContent(message);
    		respContent =  MessageUtil.messageToXml(textMsg);
    		break;
    	case "图文":
	    		//装入集合
	    		List<Article> articles2 = new ArrayList<>();
	           Article article11 = new Article();  
	           article11.setTitle("我是一条多图文消息");  
	           article11.setDescription("");  
	           article11.setPicUrl("http://img.hb.aicdn.com/ead4ae131c0a7f4c1665a8c64a3fd3ea04fd7cf81681e-EMxwTu_fw658");  
	           article11.setUrl("http://tuposky.iteye.com/blog/2008583");  
	
	           Article article12 = new Article();  
	           article12.setTitle("微信公众平台开发教程Java版（二）接口配置 ");  
	           article12.setDescription("");  
	           article12.setPicUrl("https://wx2.sinaimg.cn/mw690/9ceb2c2cly1fnkvf5z5ifj20e80e8aey.jpg");  
	           article12.setUrl("http://tuposky.iteye.com/blog/2008655");  
	
	           Article article13 = new Article();  
	           article13.setTitle("微信公众平台开发教程Java版(三) 消息接收和发送");  
	           article13.setDescription("");  
	           article13.setPicUrl("https://b-ssl.duitang.com/uploads/item/201603/03/20160303091940_LKwNa.jpeg");  
	           article13.setUrl("http://tuposky.iteye.com/blog/2017429");  
	
	           articles2.add(article11);  
	           articles2.add(article12);  
	           articles2.add(article13);  
	           newsMessage.setArticleCount(articles2.size());  
	           newsMessage.setArticles(articles2);  
	           respContent = MessageUtil.messageToXml(newsMessage);
           break;
    	case "chanpin":
    		textMsg.setContent("产品运营  链接：http://pan.baidu.com/s/1o8h0Zns 密码：");
    		baiduResource = new BDResource("《产品运营训练营—腾讯产品经理》PPT[高清]", "qx9m", "http://pan.baidu.com/s/1o8h0Zns");
    		try {
				templateService.sendDownloadTemplate(textMsg.getToUserName(), baiduResource);
			} catch (IOException e) {
				System.out.println("模板消息发送异常：");
				e.printStackTrace();
				respContent =  MessageUtil.messageToXml(textMsg);
				break;
			}
    		respContent ="";
    		break;
    	case "linux":
    		textMsg.setContent("《linux命令行与shell编程大全》 链接：http://pan.baidu.com/s/1geJYFYz 密码：h11t");
    		try {
    			baiduResource = new BDResource("《linux命令行与shell编程大全》PDF扫描版 [高清]", "h11t", "http://pan.baidu.com/s/1geJYFYz");
				templateService.sendDownloadTemplate(textMsg.getToUserName(), baiduResource);
			} catch (IOException e) {
				System.out.println("模板消息发送异常：");
				e.printStackTrace();
				respContent =  MessageUtil.messageToXml(textMsg);
				break;
			}
    		respContent =  "";
    		break;
    	case "git":
    		textMsg.setContent("《完全学会GIT SERVER的24堂课》 链接：http://pan.baidu.com/s/1pLsov2N 密码：4fn0");
    		try {
    			baiduResource = new BDResource("《完全学会GIT SERVER的24堂课》PDF扫描版 [高清]", "4fn0", "http://pan.baidu.com/s/1pLsov2N");
				templateService.sendDownloadTemplate(textMsg.getToUserName(), baiduResource);
			} catch (IOException e) {
				System.out.println("模板消息发送异常：");
				e.printStackTrace();
				respContent =  MessageUtil.messageToXml(textMsg);
				break;
			}
    		respContent =  "";
    		break;
    	case "springboot":
    		textMsg.setContent("《JavaEE开发的颠覆者springboot实战》 链接：http://pan.baidu.com/s/1cIxomi 密码：eo9g");
    		try {
    			baiduResource = new BDResource("《JavaEE开发的颠覆者springboot实战》PDF扫描版 [高清]", "eo9g", "http://pan.baidu.com/s/1cIxomi");
				templateService.sendDownloadTemplate(textMsg.getToUserName(), baiduResource);
			} catch (IOException e) {
				System.out.println("模板消息发送异常：");
				e.printStackTrace();
				respContent =  MessageUtil.messageToXml(textMsg);
				break;
			}
    		respContent =  "";
    		break;
    	case "design":
    		textMsg.setContent("《JAVA设计模式深入研究》 链接：http://pan.baidu.com/s/1hrYPL3Y 密码：ofl5");
    		try {
    			baiduResource = new BDResource("《JAVA设计模式深入研究》PDF扫描版 [高清]", "ofl5", "http://pan.baidu.com/s/1hrYPL3Y");
				templateService.sendDownloadTemplate(textMsg.getToUserName(), baiduResource);
			} catch (IOException e) {
				System.out.println("模板消息发送异常：");
				e.printStackTrace();
				respContent =  MessageUtil.messageToXml(textMsg);
				break;
			}
    		respContent =  "";
    		break;
    	case "es":
    		textMsg.setContent("《深入理解ElasticSearch》 链接：http://pan.baidu.com/s/1kUPWh9d 密码：dmng");
    		try {
    			baiduResource = new BDResource("《深入理解ElasticSearch》PDF扫描版 [高清]", "dmng", "http://pan.baidu.com/s/1kUPWh9d");
				templateService.sendDownloadTemplate(textMsg.getToUserName(), baiduResource);
			} catch (IOException e) {
				System.out.println("模板消息发送异常：");
				e.printStackTrace();
				respContent =  MessageUtil.messageToXml(textMsg);
				break;
			}
    		respContent =  "";
    		break;
	    }
		
		return respContent;
	}
	
	
    
}
