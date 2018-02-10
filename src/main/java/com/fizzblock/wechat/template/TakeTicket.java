package com.fizzblock.wechat.template;

/**
 * 取票模板消息实体类
 * @author glen
 *
 */
public class TakeTicket {

	private String first ;//开头内容
	private String nickname;//用户昵称
	private String shopname;//商店名称
	private String number;//排队号
	private String before;//前面等待
	private String remark;//结束语
	
	
	
	public TakeTicket() {}
	
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getShopname() {
		return shopname;
	}
	public void setShopname(String shopname) {
		this.shopname = shopname;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getBefore() {
		return before;
	}
	public void setBefore(String before) {
		this.before = before;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
