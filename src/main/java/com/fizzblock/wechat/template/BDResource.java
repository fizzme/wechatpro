package com.fizzblock.wechat.template;

/**
 * 百度资源实体类
 * @author glen
 *
 */

public class BDResource {

	String resourceName;
	String sharePwd;
	String shareURL;

	public BDResource() {};

	public BDResource(String resourceName, String sharePwd, String shareURL) {
		this.resourceName = resourceName;
		this.sharePwd = sharePwd;
		this.shareURL = shareURL;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getSharePwd() {
		return sharePwd;
	}

	public void setSharePwd(String sharePwd) {
		this.sharePwd = sharePwd;
	}

	public String getShareURL() {
		return shareURL;
	}

	public void setShareURL(String shareURL) {
		this.shareURL = shareURL;
	}

}
