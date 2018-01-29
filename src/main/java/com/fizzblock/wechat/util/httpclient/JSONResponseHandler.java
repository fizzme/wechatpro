package com.fizzblock.wechat.util.httpclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;


/**
 * 定义JsonResponseHandler泛型类及其中的泛型方法<br>
 * 内部使用基于fastjson的解析工具，将Entity实体转换为对应的泛型类对象返回
 * @author glen
 *
 * @param <T> 构造方法中需要传递的类型，
 */
public class  JSONResponseHandler<T> implements ResponseHandler<T>{
	/**
	 * Class<T> responseCls 全局变量，从构造方法中初识测数据响应类型，T类型的class对象
	 */
	public Class<T> responseCls = null; 
	
/**
 * 构造方法
 * @param Class<T> arg0 类T的class对象，可以传入T.class
 */
	public JSONResponseHandler(Class<T> arg0) {
		this.responseCls = arg0;
	}
	
	/**
	 * 实现ResponseHandler接口方法，返回的是一个T类型的对象<br>
	 * 实现从HttpResponse对象中，获取响应实体HttpEntity，对响应内容使用解析工具进行转换<br>
	 * 报错将抛出异常
	 */
	public T handleResponse(HttpResponse response)throws ClientProtocolException, IOException {
		//获取Response状态码
		int status  = response.getStatusLine().getStatusCode(); 
		if(status >=200 && status <300){
			//获取Reponse响应实体
			HttpEntity entity  = response.getEntity();
			//字符类型的返回则使用EntityUtils进行转换
			String str = EntityUtils.toString(entity);
			return JSONUtil.parseObject(str, this.responseCls);
		}else{
			//抛出响应的状态码异常
			throw new ClientProtocolException("Unexpected response status: " + status);
		}
	}
	

}
