package com.fizzblock.service;

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
import com.fizzblock.wechat.service.UserService;
import com.fizzblock.wechat.util.apis.WeiXinApis;
import com.fizzblock.wechat.util.httpclient.LocalHttpClient;
import com.fizzblock.wechat.util.redis.JedisOper;
import com.fizzblock.wechat.util.redis.JedisPoolUtil;
/**
 * 微信api接口调用测试类
 * @author glen
 *
 */
public class UserServiceTest {

//	String openId= "";
	
	//初始化用户信息
	Map<String,String > users = null;
	JedisOper jedisOper = null;
	
	UserService userService = null;
	
	
	
	
	/**
	 * 初始化用户列表信息和redis操作类
	 */
	@Before
	public void init(){
		initUsers();
		initRedisOper();
		userService = new UserService(jedisOper);
	}
	
	
	

	@Test
	public void testGetFansInfo() throws IOException {
		
		String openId = users.get("lrh");
		System.out.println("openID："+openId);
		//先从缓存中获取
		String user = userService.getUserInfo(openId);
		System.out.println("从缓存中获取结果："+user);
		
		//缓存中没有则从服务端获取
		if(null == user||"".equals(user)){
			//获取accessToken
			String accessToken =fetcheAccessToken() ;
			//调用微信api
			user = WeiXinApis.getFansInfo(openId,accessToken);
			//加入缓存
			userService.addUserInfo(openId, user);
			System.out.println("缓存未命中，从微信服务器拉取用户信息，再次放入缓存");
			System.out.println("用户信息："+user);
			return ;
		}
			
		System.out.println("缓存命中，从缓存中取用户信息");
		System.out.println("用户信息："+user);
		
	}

	
	
	public static final String ACESSTOKEN_KEY ="accessToken";
	
	private String fetcheAccessToken() throws IOException{
	    
		//先从缓存中获取
		String accessToken = jedisOper.getString(ACESSTOKEN_KEY);
		
		//直接从微信服务器去取accessToken
		if(null == accessToken||"".equals(accessToken)){
			System.out.println("缓存中未获取到accessToken信息，从微信服务去取");
			//获取accessToken的方式
			accessToken = WeiXinApis.fetcheAccessToken();
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
