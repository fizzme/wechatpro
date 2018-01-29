package com.fizzblock.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fizzblock.wechat.request.ImageMessage;
import com.fizzblock.wechat.response.Article;
import com.fizzblock.wechat.response.Image;
import com.fizzblock.wechat.response.NewsMessage;
import com.fizzblock.wechat.response.TextMessage;
import com.fizzblock.wechat.util.common.XStreamMessageUtil;

public class XmStreamUtilTest {

	//定义类型请求消息类型
	//文本类型
	public static final String REQ_MESSAGE_TYPE_TEXT ="text";
	//image
	public static final String REQ_MESSAGE_TYPE_IMAGE ="image";
	//voice
	public static final String REQ_MESSAGE_TYPE_VOICE ="voice";
	//链接
	public static final String REQ_MESSAGE_TYPE_LINK ="link";
	
	
	//事件消息类型
	public static final String REQ_MESSAGE_TYPE_EVENT ="event";
	
	//事件类型：订阅subscribe
	public static final String EVENT_TYPE_SUBSCRIBE ="subscribe";
	
	//事件类型：取消订阅
	public static final String EVENT_TYPE_UNSUBSCRIBE ="unsubscribe";
	
	//事件类型：click自定义菜单
	public static final String EVENT_TYPE_CLICK ="CLICK";
	
	
	//响应消息类型
	//文本
	public static final String RESP_MESSAGE_TYPE_TEXT ="text";
	//图片
	public static final String RESP_MESSAGE_TYPE_IMAGE ="image";
	//图文消息
	public static final String RESP_MESSAGE_TYPE_NEWS ="news";
	
	
	
	//测试文本消息序列化,基本响应报文
	@Test
	public void testTextMessageResponse() {
		TextMessage textMsg = new TextMessage();
		textMsg.setFromUserName("微信公众号");
		textMsg.setToUserName("用户zhangsan");
		textMsg.setMsgType(RESP_MESSAGE_TYPE_TEXT);
		textMsg.setCreateTime(System.currentTimeMillis());
		textMsg.setContent("你好这是一条测试~~！");
		System.out.println(">>>>>>>>>>>>>>>>>文本消息响应格式化结果...");
		String textMessageStr = XStreamMessageUtil.messageToXml(textMsg);
		System.out.println(textMessageStr);
	}
	
	/**
	 * 请求消息的处理
	 */
	@Test
	public void testTextMessageReq() {
		com.fizzblock.wechat.request.TextMessage textMsg = new com.fizzblock.wechat.request.TextMessage();
		textMsg.setFromUserName("李四");
		textMsg.setToUserName("");
		textMsg.setMsgType(REQ_MESSAGE_TYPE_TEXT);
		textMsg.setCreateTime(System.currentTimeMillis());
		textMsg.setContent("你好这是一条测试~~！");
		textMsg.setMsgId(12313);
		System.out.println(">>>>>>>>>>>>>>>>>文本请求消息格式化结果...");		
		String textMessageStr = XStreamMessageUtil.messageToXml(textMsg);
		System.out.println(textMessageStr);
	}
	
	/**
	 * 图片请求消息的处理
	 */
	@Test
	public void testImageMessageReq() {
		ImageMessage imageMsg = new ImageMessage();
		imageMsg.setFromUserName("李四");
		imageMsg.setToUserName("公众号");
		imageMsg.setMsgType(REQ_MESSAGE_TYPE_IMAGE);
		imageMsg.setCreateTime(System.currentTimeMillis());
		
		imageMsg.setMsgId(12313);
		imageMsg.setPicUrl("picUrl.png");
		imageMsg.setMediaId("mediaId");
		System.out.println(">>>>>>>>>>>>>>>>>图片请求消息格式化结果...");		
		String textMessageStr = XStreamMessageUtil.messageToXml(imageMsg);
		System.out.println(textMessageStr);
	}

	
	
	
	//测试图片消息序列化,基本响应报文
	@Test
	public void testImageMessageResponse() {
		ImageMessage imageMessage = new ImageMessage();
		imageMessage.setFromUserName("公众号");
		imageMessage.setToUserName("fansOpenid");
		imageMessage.setMsgType(RESP_MESSAGE_TYPE_IMAGE);
		imageMessage.setCreateTime(System.currentTimeMillis());
		Image image = new Image();
		image.setMediaId("123123");
		System.out.println(">>>>>>>>>>>>>>>>>图片消息回复格式化结果...");
		String textMessageStr = XStreamMessageUtil.messageToXml(imageMessage);
		System.out.println(textMessageStr);
	}
	
	//测试文本消息序列化,基本响应报文
	@Test
	public void testNewsMessageResponse() {
		NewsMessage newsMessage = new NewsMessage();
		newsMessage.setFromUserName("微信公众号");
		newsMessage.setToUserName("用户zhangsan");
		newsMessage.setMsgType(RESP_MESSAGE_TYPE_NEWS);
		newsMessage.setCreateTime(System.currentTimeMillis());
		
		//设置文章数为2
		newsMessage.setArticleCount(2);
		
		//构造文章内容
		Article article1 = new Article();
		article1.setTitle("第一篇文章");
		article1.setPicUrl("article1.png");
		article1.setUrl("文章一的链接地址.html");
		article1.setDescription("文章一的描述");
		
		Article article2 = new Article();
		article2.setTitle("文章2");
		article2.setPicUrl("a2.png");
		article2.setUrl("text2.html");
		article2.setDescription("你好");
		//装入集合
		List<Article> articles = new ArrayList<>();
		articles.add(article1);
		articles.add(article2);
		
		newsMessage.setArticles(articles);
		
		System.out.println(">>>>>>>>>>>>>>>>>图文消息格式化结果...");
		String newsMessageStr = XStreamMessageUtil.messageToXml(newsMessage);
		System.out.println(newsMessageStr);
	}
	
}
