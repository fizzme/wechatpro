package com.fizzblock.wechat.menu;

/**
 * click类型的按钮
 * @author glen
 *
 */
public class ClickButton extends Button {

	private String type = "click" ;//类型
	private String key;//key
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
}
