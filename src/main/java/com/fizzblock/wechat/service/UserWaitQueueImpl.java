package com.fizzblock.wechat.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Queable;

/**
 * 用户排队队列的设计实现
 * @author glen
 *
 */
public class UserWaitQueueImpl implements IUserWaitQueue {

	private final String QUEUE_NAME ="userQueue";
	private JedisPool jedisPool ;
	
	
	public UserWaitQueueImpl(JedisPool jedisPool){
		this.jedisPool = jedisPool;
		
	}
	
	public long push(String userId) {
		Jedis jedis = jedisPool.getResource();
		long result = jedis.rpush(QUEUE_NAME, userId);
		jedis.close();
		return result;
	}

	public String pop(String userId) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.lpop(QUEUE_NAME);
		jedis.close();
		
		return result;
	}

	//等于-1标识用户没有排队
	public long search(String userId) {
		
		Jedis jedis = jedisPool.getResource();
		long len = jedis.llen(QUEUE_NAME);
		long index = -1;
		//遍历获取队列中的值
		for(long i =0;i<len;i++){
			
			String value = jedis.lindex(QUEUE_NAME, i);
			//比较得出响应的元素
			if(value.equals(userId)){
				//记录遍历的索引下标返回
				index = i;
				return index;
			}
		}
		jedis.close();
		
		return index;
	}

	//获取用户的值
	public String get(int index) {
		String value = null;
		Jedis jedis = jedisPool.getResource();
		value = jedis.lindex(QUEUE_NAME, index);
		jedis.close();
		
		return value;
	}

}
