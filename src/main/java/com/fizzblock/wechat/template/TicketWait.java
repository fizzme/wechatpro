package com.fizzblock.wechat.template;

/***
 * 排号实体类
 * @author glen
 *
 */
public class TicketWait {
	
	private String first ;
	private String shopname;
	private String number;
	private String before;
	private String waitTime;
	private String status;
	private String remark;
	
	
	public TicketWait() {}
	
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
	public String getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(String waitTime) {
		this.waitTime = waitTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}
	
	
	
}
