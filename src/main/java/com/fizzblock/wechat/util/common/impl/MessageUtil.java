package com.fizzblock.wechat.util.common.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.fizzblock.wechat.util.common.XStreamMessageUtil;

public class MessageUtil {

	/**
	 * 获取请求中的xml，解析请求中的xml文件流<br>
	 * 微信xml消息层次都是1层没有过多的嵌套
	 * @param request
	 * @return
	 * @throws Exception
	 */
    public static Map<String,String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String,String> map = new HashMap<>();

        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        System.out.println("parseXml-解析消息：获取输入流");
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
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
}
