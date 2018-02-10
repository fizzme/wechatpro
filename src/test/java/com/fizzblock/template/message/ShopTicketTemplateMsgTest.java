package com.fizzblock.template.message;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fizzblock.wechat.template.BDResource;
import com.fizzblock.wechat.template.TakeTicket;
import com.fizzblock.wechat.template.TemplateData;
import com.fizzblock.wechat.template.TicketWait;
import com.fizzblock.wechat.util.httpclient.JSONUtil;
import com.fizzblock.wechat.util.httpclient.LocalHttpClient;

/**
 * 商店排号模板消息测试
 * @author glen
 *
 */
public class ShopTicketTemplateMsgTest {

	//申请的模板消息
	//取号模板消息
	String templateId_takeTicket = "6ulZrCoN3eItQuoJvuBhPTAt_7H8_o7rqs4BC5jQoR4";
	//排号模板消息
	String templateId_ticketWait = "d7g2vEtr1XM4AbPeusFe_zDaQP29dud6bkAEPG5Pmdg";
	
	
	Map<String,String > users = null;
	
	/**
	 * 初始化关注用户的列表，key用户备注，value是openId
	 */
	@Before
	public void initUsers(){
		System.out.println("初始化用户的openId");
		users = new HashMap<String, String>();
		users.put("lhj", "o3Wh70XAXDg21vAuSRWDzXFS5Ryo");
		users.put("zhx", "o3Wh70ecg_5RPOBHlPJCJ9xxUuVU");
		users.put("lrh", "o3Wh70eOFEEiC12A6bTuzZkBv_8o");
		users.put("zkq", "o3Wh70TjTIZk9VpOtcw5vbIQ1dN4");
	}
	

	/**
	 * 发送模板消息方法的测试方法
	 * @throws IOException
	 */
	@Test
	public void testSendTakeTicketTemplate() throws IOException {
		
		System.out.println(">>>>>>>>>>>开始发送取号模板消息");
		//格式化日期
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String dateTime  = df.format(new Date());
		
		TakeTicket takeTicket = new TakeTicket();
		takeTicket.setFirst("您已成功排队取号");
//		takeTicket.setNickname("后海大章鱼");
//		takeTicket.setNickname("阿糖");
		takeTicket.setNickname("ZHX ");
		takeTicket.setShopname("豆捞坊(锦艺城店)");
		takeTicket.setNumber("小桌 3009");
		takeTicket.setBefore("8桌");
		takeTicket.setRemark(dateTime);
		
		sendTakeTicketTemplateMsg(users.get("zhx"), takeTicket);
		
	}
	
	
	/**
	 * 发送模板消息方法的测试方法，排号模板消息
	 * @throws IOException
	 */
	@Test
	public void testSendTicketWaitTemplate() throws IOException {

		System.out.println(">>>>>>>>>>>开始发送排号模板消息");
		
		//格式化日期
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String dateTime  = df.format(new Date());
		
		TicketWait ticketWait = new TicketWait();
		ticketWait.setFirst("");
		ticketWait.setShopname("豆捞坊(锦艺城店)");
		ticketWait.setNumber("小桌 3009");
		ticketWait.setBefore("8桌");
		ticketWait.setWaitTime(">30分钟(仅供参考)");
		ticketWait.setStatus("排队中");
		ticketWait.setRemark(dateTime);
		
		sendTicketWaitTemplateMsg(users.get("zhx"), ticketWait);
		
	}
	
	
	
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
