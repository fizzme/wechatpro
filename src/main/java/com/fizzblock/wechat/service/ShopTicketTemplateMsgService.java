package com.fizzblock.wechat.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fizzblock.wechat.template.TakeTicket;
import com.fizzblock.wechat.template.TemplateData;
import com.fizzblock.wechat.template.TicketWait;
import com.fizzblock.wechat.util.httpclient.LocalHttpClient;

@Service(value="ticketTemplateService")
public class ShopTicketTemplateMsgService {

	//申请的模板消息
	//取号模板消息
	String templateId_takeTicket = "6ulZrCoN3eItQuoJvuBhPTAt_7H8_o7rqs4BC5jQoR4";
	//排号模板消息
	String templateId_ticketWait = "d7g2vEtr1XM4AbPeusFe_zDaQP29dud6bkAEPG5Pmdg";
	
	/**
	 * 取号模板消息发送方法
	 * @param toUser
	 * @param takeTicket
	 * @throws IOException
	 */
	public void sendTakeTicketTemplateMsg(String toUser,TakeTicket takeTicket) throws IOException{
		String accessToken = fetcheAccessToken();
		String templateUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		String dateTime  = df.format(new Date());
		String json = TemplateData.New()
				.setTouser(toUser)
				.setTemplate_id(templateId_takeTicket)
				.setTopcolor("#743A3A")
				.setUrl("")
				.add("first", takeTicket.getFirst()+"\n", "#743A3A")
				.add("nickname", takeTicket.getNickname(), "#0000FF")
				.add("shopname", takeTicket.getShopname(), "#0000FF")
				.add("number", takeTicket.getNumber(), "#0000FF")
				.add("before", takeTicket.getBefore(), "#0000FF")
				.add("remark", "\n"+takeTicket.getRemark(), "#008000")
				.build();
		
		System.out.println("发送时间："+dateTime);
		System.out.println("模板消息的主题内容："+json);
		
		String result = LocalHttpClient.jsonPost(templateUrl, json);
		System.out.println("发送模板消息的结果："+result);
	}
	
	
	/**
	 * 排号模板消息发送方法
	 * @param toUser
	 * @param ticketWait
	 * @throws IOException
	 */
	public void sendTicketWaitTemplateMsg(String toUser,TicketWait ticketWait) throws IOException{
		String accessToken = fetcheAccessToken();
		String templateUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		String dateTime  = df.format(new Date());

		String json = TemplateData.New()
				.setTouser(toUser)
				.setTemplate_id(templateId_ticketWait)
				.setTopcolor("#743A3A")
				.setUrl("")
				.add("first", ticketWait.getFirst(), "#743A3A")
				.add("shopname", ticketWait.getShopname(), "#0000FF")
				.add("number", ticketWait.getNumber(), "#0000FF")
				.add("before", ticketWait.getBefore(), "#0000FF")
				.add("waitTime", ticketWait.getWaitTime(), "#0000FF")
				.add("status", ticketWait.getStatus(), "#0000FF")
				.add("remark", "\n"+ticketWait.getRemark(), "#008000")
				.build();
		
		System.out.println("发送时间："+dateTime);
		System.out.println("模板消息的主题内容："+json);
		
		String result = LocalHttpClient.jsonPost(templateUrl, json);
		System.out.println("发送模板消息的结果："+result);
	}
	
	

	/**
	 * 获取accessToken用以发送模板消息
	 * @return
	 * @throws IOException
	 */
	private String fetcheAccessToken() throws IOException{
	    //获取accessToken的方式
		String jsonResult = LocalHttpClient.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx416c5da1eef311e6&secret=30d7fdede88444ff927934f50b367669");
		JSONObject jsonObj = JSON.parseObject(jsonResult);
		String accessToken = jsonObj.getString("access_token"); 
		System.out.println("accessToken获取："+accessToken);
		
		return accessToken;
	}
	
	
}
