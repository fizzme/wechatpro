package com.fizzblock.wechat.template;

public class ResumeSend {

	private String job;
	private String company;
	private String first;
	private String remark;
	private String url;
	
	public ResumeSend(){}
	
	public ResumeSend(String job, String company, String first) {
		this.job = job;
		this.company = company;
		this.first = first;
	}
	
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUrl() {
		if(null == url){
			return "";
		}
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
