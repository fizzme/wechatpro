package com.fizzblock.wechat.menu;

/**
 * 带连接的按钮
 * @author glen
 *
 */
public class ViewButton extends Button {

	/**
	 * 类型属性
	 */
	private String type ="view";
	
	/**
	 * 超链接地址
	 */
	private String url;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
