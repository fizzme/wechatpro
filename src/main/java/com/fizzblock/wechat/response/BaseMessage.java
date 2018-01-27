package com.fizzblock.wechat.response;
/**
 * 基本响应消息类型，没有msgId
 * @author glen
 *
 */
public class BaseMessage {

	private String ToUserName;//给user收到的openID
	private String FromUserName;//开发者微信号
	private long CreateTime;//消息创建时间（整型）
	private String MsgType;//消息类型（text/image/location/link/voice）
	
	
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
}
