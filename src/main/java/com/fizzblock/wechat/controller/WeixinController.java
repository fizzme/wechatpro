package com.fizzblock.wechat.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fizzblock.wechat.response.TextMessage;
import com.fizzblock.wechat.util.common.impl.MessageUtil;
import com.fizzblock.wechat.util.common.impl.SignCheckUtil;

@Controller
public class WeixinController {
    private static final String redirect_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Ffizzblock.bceapp.com%2FgetSNSUserinfo2.do&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";  
    //以ip方式测试授权
    private static final String redirect_url2 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Ffizzblock.bceapp.com%2FgetSNSUserinfo.do&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";  

    //日了狗....这个地址不对
    private static final String redirect_url_login = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Ffizzblock.bceapp.com%2FgetUserInfoBind.do&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";  
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
        
        String msg = "获取到的相关wx请求校验信息，{signature:%s timestamp:%s nonce:%s echostr:%s}";
        System.out.println("校验消息输出"+String.format(msg, signature,timestamp,nonce,echostr));
        
		//进行校验
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
//		response.setContentType("text/html;charset=utf-8"); //设置输出编码格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String nowDate =df.format(new Date());
		
		//xml格式消息数据请求
		String responseXml = null;
		
		//默认返回的文本消息
		String respContent = "未实现的消息类型";
		
		try{
			
			//调用parseXml方法解析请求消息
			Map<String,String > requestMap = MessageUtil.parseXml(request);
			
			//获取请求相关信息
			String fromUserName = requestMap.get("FromUserName");
			String toUserName =requestMap.get("ToUserName");
			String msgType = requestMap.get("MsgType");
			
			//响应的文本消息初始化
			TextMessage textMsg = new TextMessage();
			textMsg.setFromUserName(toUserName);
			textMsg.setToUserName(fromUserName);
			textMsg.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
			textMsg.setCreateTime(System.currentTimeMillis());
			
			/*
			 * 判断请求消息的类型，响应不同的消息回复
			 * 
			 */
			if(MessageUtil.REQ_MESSAGE_TYPE_TEXT.equals(msgType)){//文本类型
				//解析文本对象
				com.fizzblock.wechat.request.TextMessage textMessage = MessageUtil.xmlToMessage(request.getInputStream().toString(), com.fizzblock.wechat.request.TextMessage.class);
				//获取文本内容
				String content = textMessage.getContent();
				System.out.println("获取的用户请求的文本消息内容："+content);
				respContent="你发送的是文本消息\n文本消息内容："+content;
				
			}else if(MessageUtil.REQ_MESSAGE_TYPE_IMAGE.equals(msgType)){//图片类型
				respContent="你发送的是图片消息";
			}else if(MessageUtil.REQ_MESSAGE_TYPE_LINK.equals(msgType)){//链接类型
				respContent="你发送的是链接消息";
			}else if(MessageUtil.REQ_MESSAGE_TYPE_VOICE.equals(msgType)){//语音类型
				respContent="你发送的是语音消息";
			}else if(MessageUtil.REQ_MESSAGE_TYPE_EVENT.equals(msgType)){//事件类型
				
				//获取具体的事件类型
				String eventType = requestMap.get("Event");
				
				if(MessageUtil.EVENT_TYPE_CLICK.equals(eventType)){//菜单点击事件
					String menuName = requestMap.get("EventKey");
					respContent="点击了菜单："+menuName;
					
				}else if(MessageUtil.EVENT_TYPE_SUBSCRIBE.equals(eventType)){//用户关注事件，调用授权页面
					System.out.println(">>>>>>>>>用户关注");
				}else if(MessageUtil.EVENT_TYPE_UNSUBSCRIBE.equals(eventType)){//用户取关事件
					System.out.println(">>>>>>>>>用户取关");
				}
			}
			
			textMsg.setContent(respContent);
			//响应返回
			responseXml =  MessageUtil.messageToXml(textMsg);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return responseXml;
	}
	
	


    
}
