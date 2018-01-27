package com.fizzblock.wechat.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	public void wxRequestDispatcher(HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8"); //设置输出编码格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String nowDate =df.format(new Date());
		
		//xml格式消息数据请求
		String reqXml = null;
		
		//默认返回的文本消息
		String respContent = "未实现的消息类型";
		
		
	}
	
	


    
}
