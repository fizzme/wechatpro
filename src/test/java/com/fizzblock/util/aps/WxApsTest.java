package com.fizzblock.util.aps;

import static org.junit.Assert.*;

import java.awt.List;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fizzblock.wechat.util.httpclient.LocalHttpClient;
/**
 * 微信api接口调用测试类
 * @author glen
 *
 */
public class WxApsTest {

//	String openId= "";
	
	//初始化用户信息
	Map<String,String > users = null;
	
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
	 * 获取单个粉丝信息
	 * @throws IOException
	 */
//	@Test
	public void test() throws IOException {
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		//构造请求用户基本信息的url
		String accessToken = fetcheAccessToken();
		String openId = users.get("zhx");
		String requestUrl = url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
		System.out.println("用户请求地址"+requestUrl);
		
		//执行请求方法
		String jsonResult = LocalHttpClient.get(requestUrl);
		System.out.println("接口返回结果："+jsonResult);
		
		//获取字段
		JSONObject jsonObj = JSON.parseObject(jsonResult);
		
		String nickname = jsonObj.getString("nickname"); 
		String city = jsonObj.getString("city"); 
		String sex = jsonObj.getIntValue("sex")== 1? "男":"女" ; 
		String province = jsonObj.getString("province"); 
		String country = jsonObj.getString("country"); 
		
		System.out.println(String.format("解析结果\n"+"nickname:%s sex:%s city:%s country:%s province:%s  ", nickname,sex,city,country,province));
//		String accessToken = jsonObj.getString("access_token"); 
//		System.out.println("accessToken获取："+accessToken);
		
	}
	
	
	/**
	 * 批量获取粉丝信息
	 * @throws IOException
	 */
	@Test
	public void mutiFetchFasnInfoTest() throws IOException {
		String url = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=ACCESS_TOKEN";
		//构造请求用户基本信息的url
		String accessToken = fetcheAccessToken();
		String requestUrl = url.replace("ACCESS_TOKEN", accessToken);
		System.out.println("用户请求地址"+requestUrl);
		
		//构造请求对象
		JSONObject request = new JSONObject();
		
		//构造请求体
		ArrayList<Object> userlist = new ArrayList<Object>();  
		
		String zhx = users.get("zhx");
		String lrh = users.get("lrh");
		String zkq = users.get("zkq");
		
		userlist.add(new Item(zhx));
		userlist.add(new Item(lrh));
		userlist.add(new Item(zkq));
		
		JSONArray userArr = new JSONArray(userlist);
		
		request.put("user_list", userArr);
		
		String requestJson = request.toString();
		//执行请求方法
		String jsonResult = LocalHttpClient.jsonPost(requestUrl, requestJson);
		System.out.println("接口返回结果："+jsonResult);
		
		 String userList = JSONObject.parseObject(jsonResult).get("user_info_list").toString();
		JSONArray userinfos = JSONArray.parseArray(userList);
		
		//获取的用户数量
		int userNum = userinfos.size();
		
		for(int i =0; i<userNum ; i++){
			Object userObj = userinfos.get(i);
			
			//获取字段
			JSONObject jsonObj = JSON.parseObject(userObj.toString());
			
			String nickname = jsonObj.getString("nickname"); 
			String city = jsonObj.getString("city"); 
			String sex = jsonObj.getIntValue("sex")== 1? "男":"女" ; 
			String province = jsonObj.getString("province"); 
			String country = jsonObj.getString("country"); 
			
			System.out.println(String.format("解析结果\n"+"nickname:%s sex:%s city:%s country:%s province:%s  ", nickname,sex,city,country,province));
			
		}
		

	}
	
	class Item{
		String openid;
		String lang;
		
		public Item(String openid) {
			this.openid = openid;
			this.lang ="zh_CN";
		}
		
		public String getOpenid() {
			return openid;
		}
		public void setOpenid(String openid) {
			this.openid = openid;
		}
		public String getLang() {
			return lang;
		}
		public void setLang(String lang) {
			this.lang = lang;
		}
		
	}
	
	private String fetcheAccessToken() throws IOException{
	    //获取accessToken的方式
		String jsonResult = LocalHttpClient.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx416c5da1eef311e6&secret=30d7fdede88444ff927934f50b367669");
		JSONObject jsonObj = JSON.parseObject(jsonResult);
		String accessToken = jsonObj.getString("access_token"); 
		System.out.println("accessToken获取："+accessToken);
		
		return accessToken;
	}

}
