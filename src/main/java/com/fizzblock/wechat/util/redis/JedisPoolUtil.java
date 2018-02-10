package com.fizzblock.wechat.util.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * jedis连接池的工具
 * 使用单例来管理jedisPool
 * @author glen
 *
 */
public class JedisPoolUtil {

	private static volatile JedisPool jedisPool = null;
	
	//构造方法私有化
	private JedisPoolUtil(){}
	
	/**
	 * 
	 * @param host 主机ip地址
	 * @param port 端口号
	 * @return
	 */
	public static JedisPool getJedisPool(String host,int port)throws Exception{
		//没有实例则创建实例
		if(null == jedisPool){
			synchronized(JedisPoolUtil.class){
				
				if(null == jedisPool){
					  JedisPoolConfig poolConfig = new JedisPoolConfig();
					  
					  poolConfig.setMaxTotal(1500);
					  poolConfig.setMaxIdle(32);
					  poolConfig.setMaxWaitMillis(300*1000); 
					  poolConfig.setTestOnBorrow(true);
					  poolConfig.setTestOnReturn(true);
//					  jedisPool = new JedisPool(poolConfig, "127.0.0.1", 6379);
					  jedisPool = new JedisPool(poolConfig, host, port);
				}
			}
		}
		return jedisPool;
	}
	

	/**
	 * 释放资源
	 * @param pool
	 * @param jedis
	 */
	public static void release(JedisPool pool ,Jedis jedis){
		if(null != jedis){
			pool.returnResourceObject(jedis);
		}
		
	}
	
}
