package com.fizzblock.wechat.util.httpclient;

import com.alibaba.fastjson.JSON;
/**
 * json字符的解析和构建的工具类<br>
 * 基于fastjson实现
 * @author glen
 *
 */
public class JSONUtil {

	/**
	 * 泛型方法，将指定的json串，解析为提供的泛型类
	 * 
	 * @param json 传入要解析的json串
	 * @param cls Class类需要<T>，T类型的
	 * @return 返回是T，方法中传入的泛型类型
	 */
	public static<T> T parseObject(String json ,Class<T> cls){
		return JSON.parseObject(json, cls);
	}
	
	/**
	 * 普通方法，将对象转换为Json格式的字符串
	 * @param object Object对象
	 * @return String 返回json格式的字符串
	 */
	public static String toJSONString(Object object){
		return JSON.toJSONString(object);
	}
	
}
