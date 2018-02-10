package com.fizzblock.wechat.util.apis;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fizzblock.wechat.util.httpclient.LocalHttpClient;

public class WeiXinApis {

	/**
	 * 获取调用接口用到的accessToken
	 * @return
	 * @throws IOException
	 */
	public static String fetcheAccessToken() throws IOException {
	    //获取accessToken的方式
		String jsonResult = LocalHttpClient.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx416c5da1eef311e6&secret=30d7fdede88444ff927934f50b367669");
		JSONObject jsonObj = JSON.parseObject(jsonResult);
		String accessToken = jsonObj.getString("access_token"); 
		System.out.println("accessToken获取："+accessToken);
		
		return accessToken;
	}

	/**
	 * 获取关注粉丝的信息
	 * 
	 */
	public static String  getFansInfo(String openId,String accessToken) throws IOException {
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		//构造请求用户基本信息的url
		String requestUrl = url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
//		System.out.println("用户请求地址"+requestUrl);
		
		//执行请求方法
		String jsonResult = LocalHttpClient.get(requestUrl);
		return jsonResult;
	}

}
