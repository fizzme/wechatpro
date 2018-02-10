package com.fizzblock.service;

import org.junit.Test;

import redis.clients.jedis.JedisPool;

import com.fizzblock.wechat.service.IUserWaitQueue;
import com.fizzblock.wechat.service.UserWaitQueueImpl;
import com.fizzblock.wechat.util.redis.JedisPoolUtil;

public class UserQueueTest {

	IUserWaitQueue userQueue = null;
	
	public void initQueueService() {
		String args = "192.168.248.151:6379";
		String [] params =args.split(":");
		
		String host = params[0];//主机名
		int port = Integer.parseInt(params[1]);
		System.out.println(String.format("host主机：%s port端口：%d", host,port));
		
		JedisPool jedisPool =null;
		try{
			//初始化jedis连接池
			System.out.println(">>>>>>>>>>>>>>>>>>>初始化数据库连接池------>");
			jedisPool = JedisPoolUtil.getJedisPool(host, port);
		}catch(Exception ex){
			System.out.println("初始化jedis连接池失败："+ex.getCause().toString());
			return ;
		}
		
		userQueue = new UserWaitQueueImpl(jedisPool);
		
		
	}

	@Test
	public void test() {
		//初始化队列服务
		initQueueService();
		//入队abcd
		userQueue.push("a");
		userQueue.push("b");
		userQueue.push("c");
		userQueue.push("d");
		
		//查看用户c的index和排队位置index+1,第index+1个用户，前面还有index人
		
		long index = userQueue.search("c");
		System.out.println(String.format("c用户是第%s个用户，前面还有%s个人", (index+1)+"",index+""));
		
		
		
	}
	
	
	@Test
	public void pop() {
		//初始化队列服务
		initQueueService();
	
		userQueue.pop("a");
		userQueue.pop("b");
		
		long index = userQueue.search("c");
		System.out.println(String.format("c用户是第%s个用户，前面还有%s个人", (index+1)+"",index+""));
	}
	
	
	//排队校验的设计实现，已经排队的用户将不再入队
	@Test
	public void checkUser() {
		String str = "d";
		//初始化队列服务
		initQueueService();
		
		if(userQueue.search(str)>0){
		
			System.out.println("用户已经存在");
			long index = userQueue.search("c");
			System.out.println(String.format("c用户是第%s个用户，前面还有%s个人", (index+1)+"",index+""));
		 return ;	
		}
		userQueue.push(str);
		
	}
	
}
