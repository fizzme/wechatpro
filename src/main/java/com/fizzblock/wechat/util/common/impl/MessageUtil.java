package com.fizzblock.wechat.util.common.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.fizzblock.wechat.util.common.XStreamMessageUtil;

public class MessageUtil {

	//定义类型请求消息类型
	//文本类型
	public static final String REQ_MESSAGE_TYPE_TEXT ="text";
	//image
	public static final String REQ_MESSAGE_TYPE_IMAGE ="image";
	//voice
	public static final String REQ_MESSAGE_TYPE_VOICE ="voice";
	//链接
	public static final String REQ_MESSAGE_TYPE_LINK ="link";
	
	
	//事件消息类型
	public static final String REQ_MESSAGE_TYPE_EVENT ="event";
	//事件类型：订阅subscribe
	public static final String EVENT_TYPE_SUBSCRIBE ="subscribe";
	//事件类型：取消订阅
	public static final String EVENT_TYPE_UNSUBSCRIBE ="unsubscribe";
	//事件类型：click自定义菜单
	public static final String EVENT_TYPE_CLICK ="CLICK";
	
	//响应消息类型
	//文本
	public static final String RESP_MESSAGE_TYPE_TEXT ="text";
	//图片
	public static final String RESP_MESSAGE_TYPE_IMAGE ="image";
	//图文消息
	public static final String RESP_MESSAGE_TYPE_NEWS ="news";
	
	
	
	/**
	 * 获取请求中的xml，解析请求中的xml文件流<br>
	 * 微信xml消息层次都是1层没有过多的嵌套
	 * @param request
	 * @return
	 * @throws Exception
	 */
    public static Map<String,String> parseXml(String  xmlString) throws Exception {
    	Map<String,String> map = null;
    	if(null!= xmlString&&!"".equals(xmlString)){
	    		
	    	System.out.println("parseXml-解析消息：获取输入流");
	    	
	        // 将解析结果存储在HashMap中
	        map = new HashMap<>();
	
	        // 读取输入流
	        SAXReader reader = new SAXReader();
//	        Document document = reader.read(inputStream);
	        InputStream inputStream =  new ByteArrayInputStream(xmlString.getBytes());
	        Document document = reader.read(inputStream);
//	        Document document = reader.read(xmlString);
	        // 得到xml根元素
	        Element root = document.getRootElement();
	        // 得到根元素的所有子节点
	        List<Element> elementList = root.elements();
	
	        // 遍历所有子节点
	        for (Element e : elementList) {
	            System.out.print(e.getName() + "|" + e.getText());
	            map.put(e.getName(), e.getText());
	        }
	        // 释放资源
	        inputStream.close();
	        inputStream = null;
    	}else{
    		throw new Exception("parseXml-解析消息异常，参数："+xmlString);
    	}

        return map;
    }
    
	/**
	 * 消息对象转换为xml,泛型方法
	 * @param person
	 * @return
	 */
	public static <T> String messageToXml(T t){
		
		return XStreamMessageUtil.messageToXml(t);
	}
    
	
	/**
	 * 序列化xml字符串为实体类，泛型方式
	 * @param 待解析的xml字符串
	 * @param 待解析的实体类型的class
	 * @return 返回实体类型的class对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlToMessage(String xml,Class<T> cls ){
		
		return XStreamMessageUtil.xmlToMessage(xml, cls);
	}
	
	
	/**
	 * 将输入流转换为字符串
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String getStrFromInputSteam(InputStream in) throws IOException{
		StringBuffer buffer = null;
		if(null != in){
			System.out.println("输入流的内容："+in.toString());
		     BufferedReader bf=new BufferedReader(new InputStreamReader(in,"UTF-8"));
		     //最好在将字节流转换为字符流的时候 进行转码
		     buffer=new StringBuffer();
		     String line="";
		     while((line=bf.readLine())!=null){
		         buffer.append(line);
		     }
		}
	     
	    return buffer.toString();
	}
}
