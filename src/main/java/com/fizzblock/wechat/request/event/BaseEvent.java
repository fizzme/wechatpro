package com.fizzblock.wechat.request.event;

/**
 * 5个参数
 * @author glen
 *
 */
public class BaseEvent {

	private String ToUserName;//开发者微信号
	private String FromUserName;//给user收到的openID
	private long CreateTime;//消息创建时间（整型）
	private String MsgType;//消息类型（text/image/location/link/voice/event）
	private String Event ;//事件类型，subscribe(订阅)、unsubscribe(取消订阅)
	
	
	public String getToUserName() {
		return ToUserName;
	}
	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}
	public String getFromUserName() {
		return FromUserName;
	}
	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}
	public long getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(long createTime) {
		CreateTime = createTime;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	public String getEvent() {
		return Event;
	}
	public void setEvent(String event) {
		Event = event;
	}
	
	
}
