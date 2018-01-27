package com.fizzblock.util;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

public class Dom4jTest {

	@Test
	public void testParseXmlString() throws DocumentException{
		//构造xml 
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<person>");
		sb.append("<name>张三</name>");
		sb.append("<sex>男</sex>");
		sb.append("<address>河南郑州金水区</address>");
		sb.append("</person>");
		String personXml = sb.toString();
		System.out.println(personXml);
		
		//解析获取Document文档树，使用DocumentHelper
		Document document = DocumentHelper.parseText(personXml);
		//得到xml的根元素
		Element root = document.getRootElement();
		//得到根元素person的所有子节点
		List<Element> elementList = root.elements();
		//遍历子节点
		for(Element e :elementList){
			System.out.println(e.getName()+"=>"+e.getText());
		}
		
	}
	
	
}
