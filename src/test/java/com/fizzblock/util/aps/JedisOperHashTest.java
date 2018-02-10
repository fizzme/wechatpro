package com.fizzblock.util.aps;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.JedisPool;

import com.fizzblock.wechat.util.redis.JedisOper;
import com.fizzblock.wechat.util.redis.JedisPoolUtil;

public class JedisOperHashTest {

	
	JedisOper jedisOper = null;
	
	@Before
	public void testOper() throws InterruptedException{
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
	
	@Test
	public void setStringTest(){
		jedisOper.setString("jedisOper", "test jedisOper");
		
		System.out.println("获取设置的值："+jedisOper.getString("jedisOper"));
		
	}
	
	public static final String HASH_KEY_USER_TABLE ="users";
	@Test
	public void setUserInfoTest(){
		
		
		
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
