package com.fizzblock.util;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.fizzblock.wechat.response.TextMessage;
import com.fizzblock.wechat.util.common.impl.MessageUtil;

public class MessageUtilTest {

	@Test
	public void test() {
		
		try {
			Map<String,String > requestMap = MessageUtil.parseXml("<xml><ToUserName><![CDATA[gh_e35ca6ca94c3]]></ToUserName><FromUserName><![CDATA[o3Wh70TjTIZk9VpOtcw5vbIQ1dN4]]></FromUserName><CreateTime>1517051942</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[你好]]></Content><MsgId>6515688477642552261</MsgId></xml>");
	
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
			String content = requestMap.get("Content");
			System.out.println("获取的用户请求的文本消息内容："+content);
			String respContent ="你发送的是文本消息\n文本消息内容："+content;
			
			String responseXml =  MessageUtil.messageToXml(textMsg);
			
			System.out.println(responseXml);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
