package com.fizzblock.wechat.request;
/**
 * 基本消息类型
 * @author glen
 *
 */
public class BaseMessage {

	private String ToUserName;//开发者公众号
	private String FromUserName;//发送方账号（openId）
	private long CreateTime;//消息创建时间（整型）
	private String MsgType;//消息类型（text/image/location/link/voice）
	private long MsgId;//消息ID,64位整型
	
	
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
	public long getMsgId() {
		return MsgId;
	}
	public void setMsgId(long msgId) {
		MsgId = msgId;
	}
	
}
