package com.fizzblock.template.message;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fizzblock.wechat.service.TemplateMessageService;
import com.fizzblock.wechat.template.BDResource;
import com.fizzblock.wechat.template.ResumeFeedBack;
import com.fizzblock.wechat.template.ResumeSend;
import com.fizzblock.wechat.template.TemplateData;
import com.fizzblock.wechat.util.httpclient.JSONUtil;
import com.fizzblock.wechat.util.httpclient.LocalHttpClient;

/**
 * 简历消息模板发送测试类
 * @author glen
 *
 */
public class ResumeTemplateMsgSendTest {

	TemplateMessageService templateService = new TemplateMessageService();
	
	Map<String,String > users = null;
	
	@Before
	public void initUsers(){
		System.out.println("初始化用户的openId");
		users = new HashMap<String, String>();
		users.put("lhj", "o3Wh70XAXDg21vAuSRWDzXFS5Ryo");
		users.put("zhx", "o3Wh70ecg_5RPOBHlPJCJ9xxUuVU");
		users.put("lrh", "o3Wh70eOFEEiC12A6bTuzZkBv_8o");
		users.put("zkq", "o3Wh70TjTIZk9VpOtcw5vbIQ1dN4");
	}
	

	/**
	 * 
	 * 
		标题：简历投递提醒
		{{first.DATA}}
		公司名：{{company.DATA}}
		职位名称：{{job.DATA}}
		投递时间：{{time.DATA}}    
		{{remark.DATA}}
	 * 
		您好!您的简历投递成功
		公司名：北京58同城信息技术有限公司
		职位名称：产品经理
		投递时间：2014-06-24

	 */
//	@Test
	public void SendResumeSendTemplate() throws IOException{
		
		String user = users.get("zkq");
		ResumeSend resumeSend = new ResumeSend();
		resumeSend.setFirst("您好!您的简历投递成功");
		resumeSend.setCompany("北京58同城信息技术有限公司");
		resumeSend.setJob("产品经理");

		templateService.sendResumeSubmitTemplate(user, resumeSend);
		
	}

	/**
	 * 
	 * 
		标题：简历投递反馈提醒
		{{first.DATA}}
		公司名：{{company.DATA}}
		投递时间：{{time.DATA}}
		职位名称：{{job.DATA}}
		反馈结果：{{result.DATA}}     
		{{remark.DATA}}
	 * 
		您好!您投递的简历有新的反馈
		公司名：北京58同城信息技术有限公司
		投递时间：2014-06-24
		职位名称：产品经理
		反馈结果：已查看
	 */
	@Test
	public void testSendresumeFeedBackTemplate() throws IOException {
		String user = users.get("zkq");
		ResumeFeedBack resumeFeedBack = new ResumeFeedBack();
		resumeFeedBack.setFirst("您好!您的简历投递成功");
		resumeFeedBack.setCompany("北京58同城信息技术有限公司");
		resumeFeedBack.setJob("产品经理");
		resumeFeedBack.setResult("已查看");
		templateService.sendResumeFeedbackTemplate(user, resumeFeedBack);
	}

	

	private String fetcheAccessToken() throws IOException{
	    //获取accessToken的方式
		String jsonResult = LocalHttpClient.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx416c5da1eef311e6&secret=30d7fdede88444ff927934f50b367669");
		JSONObject jsonObj = JSON.parseObject(jsonResult);
		String accessToken = jsonObj.getString("access_token"); 
		System.out.println("accessToken获取："+accessToken);
		
		return accessToken;
	}
	

	
}
