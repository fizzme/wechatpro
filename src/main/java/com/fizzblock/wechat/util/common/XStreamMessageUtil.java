package com.fizzblock.wechat.util.common;

import java.io.Writer;

import com.fizzblock.wechat.response.Article;
import com.fizzblock.wechat.response.NewsMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * 使用xStream进行消息与xml互转的工具类
 * @author glen
 *
 */
public class XStreamMessageUtil {

	/**
	 * xStream扩展支持CDATA
	 * 作为全局变量使用
	 */
	private static XStream xstream = new XStream(new XppDriver(){
		public HierarchicalStreamWriter createWriter(Writer out){
			
			PrettyPrintWriter prettyPrintWriter = new PrettyPrintWriter(out){
				//对所有xml节点的转换都增加CDATA标记
				boolean cdata = true;
				@SuppressWarnings("unchechked")
				public void startNode(String name, Class clazz){
					super.startNode(name, clazz);
				}
				
				//文本处理
				protected void writeText(QuickWriter writer,String text){
					if(cdata){
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					}else{
						writer.write(text);
					}
				}
			};
			
			return prettyPrintWriter;
			
		}
	});
	
	/**
	 * 消息对象转换为xml,泛型方法
	 * @param person
	 * @return
	 */
	public static <T> String messageToXml(T t){
		
		String className = t.getClass().getSimpleName();
		//图文消息的别名需要额外处理
		if("NewsMessage".equals(className)){
			xstream.alias("item", new Article().getClass());
		}
		
		//一般统一处理
		xstream.alias("xml", t.getClass());
		
		return xstream.toXML(t);
	}
	
	/**
	 * 序列化xml字符串为实体类，泛型方式
	 * @param 待解析的xml字符串
	 * @param 待解析的实体类型的class
	 * @return 返回实体类型的class对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlToMessage(String xml,Class<T> cls ){
		T obj = null;
		if(NewsMessage.class.equals(cls)){//class反射类型比较
			System.out.println("cls 是NewsMessage图文消息类型");
			//获取小写类名
			String alias = cls.getSimpleName().toLowerCase();
			xstream.alias("xml", cls);
			xstream.alias("item", new Article().getClass());
			obj = (T) xstream.fromXML(xml);
			
		}else{
			//类名转为小写
			String alias = cls.getSimpleName().toLowerCase();
			xstream.alias("xml", cls);
			obj = (T) xstream.fromXML(xml);
		}
		
		return obj;
	}
	
}
