package com.fizzblock.wechat.request.event;

/**
 * EventKey的值表名哪个菜单被点击
 * @author glen
 *
 */
public class MenuEvent extends BaseEvent {

	private String EventKey;

	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}
	
	
}
