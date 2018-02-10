package com.fizzblock.wechat.service;

/**
 * 用户排队队列接口定义
 * @author glen
 *
 */
public interface IUserWaitQueue {

	/**
	 * 添加排队用户到队列中
	 * @param userId
	 */
	public long push(String userId);
	
	/***
	 * 用户排到队就餐，出队
	 * @param userId
	 */
	public String pop(String userId);
	
	/**
	 * 根据userId查找用的索引下标
	 * @param userId
	 */
	public long search(String userId);
	
	/**
	 * 根据索引获取用户的openId
	 * @param index
	 */
	public String get(int index);
}
