package com.fizzblock.util;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * XStream的使用示例
 * @author glen
 *
 */
public class XStreamTest {
	
	/**
	 * 对象解析为xml
	 * @param person
	 * @return
	 */
	public String javaObject2xml(Person person){
		XStream xs = new XStream(new DomDriver());
		//给person类定义别名？
		xs.alias("person", person.getClass());
		return xs.toXML(person);
	}
	/**
	 * 对象解析为xml,泛型方法
	 * @param person
	 * @return
	 */
	public <T> String javaObject2xmlGenic(T t){
		XStream xs = new XStream(new DomDriver());
		//给person类定义别名？
		xs.alias(t.getClass().getSimpleName(), t.getClass());
		
		return xs.toXML(t);
	}
	
	
	/**
	 * xml解析为java对象
	 * @param xml
	 * @return
	 */
	public Object xml2JavaObject(String xml){
		XStream xs = new XStream(new DomDriver());
		xs.alias("person", Person.class);
		Person person = (Person) xs.fromXML(xml);
		return person;
	}
	
	/**
	 * xml解析为java对象,定义泛型方法
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T xml2JavaObject(String xml,T t){
		XStream xs = new XStream(new DomDriver());
		xs.alias(t.getClass().getSimpleName(), t.getClass());
		T obj = (T) xs.fromXML(xml);
		return obj;
	}
	//
	
	/**
	 * 序列化实体类为xml字符串
	 * @param xml
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T xml2JavaObject2(String xml,Class<T> cls ){
		XStream xs = new XStream(new DomDriver());
		String alias = cls.getSimpleName().toLowerCase();//获取类名改成小写作为别名，举例Person类的simpleName是Person，alias使用toLowerCase变成person
		xs.alias(alias, cls);
		T obj = (T) xs.fromXML(xml);
		return obj;
	}
	

	/**
	 * 序列化xml字符串为实体类，泛型方式
	 * @param 待解析的xml字符串
	 * @param 待解析的实体类型的class
	 * @return 返回实体类型的class对象
	 */
	@SuppressWarnings("unchecked")
	public <T> T xml2JavaObject3(String xml,Class<T> cls ){
		XStream xs = new XStream(new DomDriver());
		T obj = null;
		if(Person.class.equals(cls)){//class反射类型比较
			System.out.println("cls 是person类型");
			String alias = cls.getSimpleName().toLowerCase();
			xs.alias(alias, cls);
			obj = (T) xs.fromXML(xml);
			
		}else{
			String alias = cls.getSimpleName().toLowerCase();
			xs.alias(alias, cls);
			obj = (T) xs.fromXML(xml);
		}
		
		return obj;
	}

//	@Test
	public void testObj2xml(){
		//构建person对象
		Person p1 = new Person();
		p1.setName("柳峰");
		p1.setSex("男");
		p1.setAddress("新疆乌鲁木齐");
	//非泛型方法，泛型方法	
		System.out.println(javaObject2xml(p1));
//		System.out.println(javaObject2xmlGenic(p1));
		
		
	}
	
	/**
	 * 解析xml为对象
	 */
	@Test
	public void testXml2obj(){
		//构造xml 
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<person>");
		sb.append("<name>张三</name>");
		sb.append("<sex>男</sex>");
		sb.append("<address>河南郑州金水区</address>");
		sb.append("</person>");
		String personXml = sb.toString();
		
//		Person p2 = xml2JavaObject2(personXml, Person.class);
//		Person p2 = (Person) xml2JavaObject(personXml);
		Person p2 = xml2JavaObject3(personXml, Person.class);
		
		System.out.println(String.format("{name:%s sex:%s address:%s}", p2.getName(),p2.getSex(),p2.getAddress()));
		
	}
}
