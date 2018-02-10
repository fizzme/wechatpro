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

import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fizzblock.wechat.util.httpclient.LocalHttpClient;
import com.fizzblock.wechat.util.redis.JedisOper;
import com.fizzblock.wechat.util.redis.JedisPoolUtil;
/**
 * 微信api接口调用测试类
 * @author glen
 *
 */
public class WxApisRedis {

//	String openId= "";
	
	//初始化用户信息
	Map<String,String > users = null;
	JedisOper jedisOper = null;
	public static final String HASH_KEY_USER_TABLE ="users";
	/**
	 * 初始化用户列表信息和redis操作类
	 */
	@Before
	public void init(){
		initUsers();
		initRedisOper();
	}
	
	
	
	/**
	 * 获取单个粉丝信息
	 * @throws IOException
	 */
	@Test
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
		
		//将用户信息添加到缓存中
		System.out.println("将用户信息添加到缓存中");
		addUserInfo(openId, jsonResult);
		
	}
	
	/**
	 * 添加用户信息
	 * @param openId
	 * @param infoStr
	 */
	public void addUserInfo(String openId,String infoStr){
		long result = jedisOper.hset(HASH_KEY_USER_TABLE, openId, infoStr);
		System.out.println("设置openid为-->"+openId+"--用户的信息,响应结果"+result);
	}
	
	/**
	 * 获取用户信息
	 * @param openId
	 * @return
	 */
	public String getUserInfo(String openId){
		String  result = jedisOper.hget(HASH_KEY_USER_TABLE, openId);
		return result; 
	}
	

	
	
	public static final String ACESSTOKEN_KEY ="accessToken";
	
	private String fetcheAccessToken() throws IOException{
	    
		//先从缓存中获取
		String accessToken = jedisOper.getString(ACESSTOKEN_KEY);
		
		//直接从微信服务器去取accessToken
		if(null == accessToken||"".equals(accessToken)){
			System.out.println("缓存中未获取到accessToken信息，从微信服务去取");
			//获取accessToken的方式
			String jsonResult = LocalHttpClient.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx416c5da1eef311e6&secret=30d7fdede88444ff927934f50b367669");
			JSONObject jsonObj = JSON.parseObject(jsonResult);
			accessToken = jsonObj.getString("access_token");
			
			//设置进入redis缓存
			jedisOper.setString(ACESSTOKEN_KEY, accessToken);
			jedisOper.setExpire(ACESSTOKEN_KEY, 7000);//设置超时时间为7000秒
			System.out.println("从微信服务去取得的accessToken为："+accessToken);
			return accessToken; 
		}
		
		System.out.println("从redis缓存中取得的accessToken为："+accessToken);
		
		return accessToken;
	}

	
	public void initRedisOper(){
		
			String args = "192.168.248.151:6379";
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
				return ;
			}
			//初始化jedis操作类
			jedisOper = new JedisOper(jedisPool);
	}
	
	public void initUsers(){
		System.out.println("初始化用户的openId");
		users = new HashMap<String, String>();
		users.put("lhj", "o3Wh70XAXDg21vAuSRWDzXFS5Ryo");
		users.put("zhx", "o3Wh70ecg_5RPOBHlPJCJ9xxUuVU");
		users.put("lrh", "o3Wh70eOFEEiC12A6bTuzZkBv_8o");
		users.put("zkq", "o3Wh70TjTIZk9VpOtcw5vbIQ1dN4");
	}
}
