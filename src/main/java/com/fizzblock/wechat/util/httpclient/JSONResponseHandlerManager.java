package com.fizzblock.wechat.util.httpclient;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ResponseHandler;

/**
 * 
 * JsonResponseHandler的管理器
 * 其实现是使用map管理不同类型的T的JsonResponseHandler实例
 * @author glen
 *
 */
public class JSONResponseHandlerManager {

	private static Map<String,JSONResponseHandler<?>> map = new HashMap<>();
	
	public static <T> JSONResponseHandler<T> createResponseHandler(Class<T> cls){
		if(map.containsKey(cls.getName())){
			return (JSONResponseHandler)map.get(cls.getName());
		}else{
			JSONResponseHandler<T> responseHandler = new JSONResponseHandler<T>(cls);
			map.put(cls.getName(), responseHandler);
			return responseHandler;
		}
		
	}
	
}
