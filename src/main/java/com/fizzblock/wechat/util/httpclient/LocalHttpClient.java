package com.fizzblock.wechat.util.httpclient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;


public class LocalHttpClient {

	public static Header jsonHeader;
	protected static HttpClient httpClient;
	private static Map<String, HttpClient> httpClient_keyStore;

	/**
	 * 初始化执行内容内容
	 */
	static {
		//请求头json格式
		jsonHeader = new BasicHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
		httpClient = HttpClientFactory.createHttpClient(500, 10);//连接池配置
		httpClient_keyStore = new HashMap();
	}
	
	public static void init(int maxTotal, int maxPerRoute) {
		httpClient = HttpClientFactory.createHttpClient(maxTotal, maxPerRoute);
	}

	
	/**
	 * 单纯无参的Get请求
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public static String get(String uri) throws IOException {
		HttpUriRequest request = RequestBuilder.get().setUri(uri).build();
		HttpResponse response = execute(request);
		return response != null ? EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8")) : "";
	}

	/**
	 * Post进行json格式请求，响应返回，取出响应的Entity实体并中文格式处理
	 * @param String uri 请求地址
	 * @param String body 请求的json格式参数
	 * @return
	 * @throws IOException
	 */
	public static String jsonPost(String uri, String body) throws IOException {
		HttpUriRequest request = RequestBuilder.post().setHeader(jsonHeader).setUri(uri)
				.setEntity(new StringEntity(body, Charset.forName("UTF-8"))).build();
		HttpResponse response = execute(request);
		return EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
	}

	/**
	 * 执行请求并对返回的json结果进行解析，解析成执行的Class对象；json中的键值需要与class的属性一致
	 * @param request
	 * @param clazz
	 * @return
	 */
	public static <T> T executeJsonResult(HttpUriRequest request, Class<T> clazz) {
		return execute(request, JSONResponseHandlerManager.createResponseHandler(clazz));
	}
	
	/**
	 * -------------------------------------下面是通用方法--------------------------------------------
	 */
	
	
	/**
	 * 基本请求，返回HttpResponse对象，可以执行get/post请求
	 * @param HttpUriRequest request 可以设置Header/uri/Entity请求实体
	 * @return HttpResponse
	 */
	private  static HttpResponse execute(HttpUriRequest request) {
		try {
			return httpClient.execute(request);
		} catch (ClientProtocolException arg1) {
//			logger.error("", arg1);
			arg1.printStackTrace();
		} catch (IOException arg2) {
//			logger.error("", arg2);
			arg2.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 根据请求HttpUriRequest和ResponseHandler处理器，返回处理的泛型结果对象
	 * @param HttpUriRequest request get或是post请求，可以带参数
	 * @param ResponseHandler<T> responseHandler 需要扩展实现的ResponseHandler 
	 * @return
	 */
	private  static <T> T execute(HttpUriRequest request, ResponseHandler<T> responseHandler) {
		try {
			return httpClient.execute(request, responseHandler);
		} catch (ClientProtocolException arg2) {
//			logger.error("", arg2);
			arg2.printStackTrace();
		} catch (IOException arg3) {
//			logger.error("", arg3);
			arg3.printStackTrace();
		}
		return null;
	}

	
}
