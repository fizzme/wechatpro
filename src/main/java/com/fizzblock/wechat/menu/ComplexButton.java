package com.fizzblock.wechat.menu;

/**
 * 符合类型的按钮
 * @author glen
 *	符合按钮是指含有子按钮，也就是含有子菜单的以及菜单
 */
public class ComplexButton extends Button {

	private Button [] sub_button;

	public Button[] getSub_button() {
		return sub_button;
	}

	public void setSub_button(Button[] sub_button) {
		this.sub_button = sub_button;
	}
	
	
}
