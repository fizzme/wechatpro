package com.fizzblock.wechat.service;

import com.fizzblock.wechat.util.redis.JedisOper;

public class UserService {

	JedisOper jedisOper = null;
	public static final String HASH_KEY_USER_TABLE ="users";
	
	public UserService(JedisOper jedisOper){
		this.jedisOper = jedisOper;
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
	
}
