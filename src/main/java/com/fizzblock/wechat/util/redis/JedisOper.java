package com.fizzblock.wechat.util.redis;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public class JedisOper {
	
	JedisPool jedisPool ;

	/**
	 * 构造方法初始化连接池
	 * @param jedisPool
	 */
	public JedisOper(JedisPool jedisPool){
		this.jedisPool = jedisPool;
	}
	
	//需要set方法，get方法可以不要
	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	//set
	public void setString(String key,String value){
		
		Jedis jedis = jedisPool.getResource();
		jedis.set(key, value);
		jedis.close();
		
	}
	
	/**
	 * 获取key对应的值
	 * @param key
	 * @return
	 */
	public String getString(String key){
		Jedis jedis = jedisPool.getResource();
		String result = jedis.get(key);
		jedis.close();
		return result;
	}
	
	//set
	public long setExpire(String key,int seconds){
		Jedis jedis = jedisPool.getResource();
		
		long result = jedis.expire(key, seconds);
		System.out.println("设置超时的结果："+result);
		jedis.close();
		
		return result;
	}
	
	
	//删除键值
	public void del(String key){
		Jedis jedis = jedisPool.getResource();
		jedis.del(key);
		jedis.close();
	}
	
	/**
	 * 查看剩余时长
	 * @param key
	 * @return
	 */
	public long ttl(String key){
		long time = -1L;
		Jedis jedis = jedisPool.getResource();
		time = jedis.ttl(key);
		jedis.close();
		return time;
	}
	
	
	/**
	 * 批量删除，批量设置超时
	 * @param keys
	 */
	public void multiExpire(List<String> keys){
		Jedis jedis = jedisPool.getResource();
		
		//生成pipeline对象
		Pipeline pipeline = jedis.pipelined();
		
		//pipeline命令追加
		for(String key :keys){
			pipeline.expire(key, 10);
		}
		
		//pipeline命令批量发送
		pipeline.sync();
		
		jedis.close();
	}
	
	
	public long hset(String key,String field,String value){
		
		Jedis jedis = jedisPool.getResource();
		long result =jedis.hset(key, field, value);
		jedis.close();
		
		return result;
	}
	
	/**
	 * 获取结果
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(String key,String field){
		
		Jedis jedis = jedisPool.getResource();
		String result =jedis.hget(key, field);
		jedis.close();
		
		return result;
	}
	
	/**
	 * 参数列表的方式
	 * @param key
	 * @param fields
	 * @return
	 */
	public long hdel(String key,String... fields){
		
		Jedis jedis = jedisPool.getResource();
		long result =jedis.hdel(key, fields);
		jedis.close();
		
		return result;
	}
	
	/**
	 * 计算field的个数
	 * @param key
	 * @param fields
	 * @return
	 */
	public long hlen(String key){
		
		Jedis jedis = jedisPool.getResource();
		long result =jedis.hlen(key);
		jedis.close();
		
		return result;
	}
	
	
	/**
	 * 返回所有的hashFiled
	 * @param key
	 * @return
	 */
	public Set<String> hkeys(String key){
		
		Jedis jedis = jedisPool.getResource();
		Set<String> result =jedis.hkeys(key);
		jedis.close();
		
		return result;
	}
}
