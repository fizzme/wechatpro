package com.fizzblock.wechat.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fizzblock.wechat.template.BDResource;
import com.fizzblock.wechat.template.ResumeFeedBack;
import com.fizzblock.wechat.template.ResumeSend;
import com.fizzblock.wechat.template.TemplateData;
import com.fizzblock.wechat.util.httpclient.LocalHttpClient;

@Service(value="templateService")
public class TemplateMessageService {
	//资源下载模板id
	String templateId_download = "MQ56RYkF8BJsqPhHLcp5cKg32jhW9NIbtVQ60cN1Lvg";
	//简历投递
	String template_resume_Send = "Blz_acdU505SONlViCb4Uu72PKQNb_vhQb4NQPwilPo";
	//投递反馈
	String template_resume_feedback = "B-S3XSfXl7JgcuNgeUb6I0_j-ag--XGttC_rLGF6_Tc";
	
	//2018-1-29 19:19:25 到20:19可用
	String accessToken = "6_p_je-3nIfrBUOTpEHX0xwQsSqJs9XLvi1Jz1bMFmstyOKCpTQe0QzsSvTTNnNOYj1me80vHE6B8-nhli7jDOXoYRPSdFQ8z2tCvsH9hIF-1ewaWsOEDPpu7u_ru5pDGdaRcCVJK1o6pnDL7SXXYjAIAOOP";
	
	
	public void sendDownloadTemplate(String toUser,BDResource baiduResource) throws IOException{
		String accessToken = fetcheAccessToken();
		String templateUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		String dateTime  = df.format(new Date());
		String json = TemplateData.New()
				.setTouser(toUser)
				.setTemplate_id(templateId_download)
				.setTopcolor("#743A3A")
				.setUrl(baiduResource.getShareURL())
				.add("first", baiduResource.getResourceName(), "#743A3A")
				.add("passwd", baiduResource.getSharePwd(), "#0000FF")
				.add("nowtime", dateTime, "#0000FF")
				.add("attion", "如果取消关注本公众号，再次关注将无法享受分享码福利，切记切记", "#0000FF")
				.add("end", "请点击向详情保存资源，祝生活愉快", "#008000")
				.build();
		
		System.out.println("发送时间："+dateTime);
		System.out.println("模板消息的主题内容："+json);
		
		String result = LocalHttpClient.jsonPost(templateUrl, json);
		System.out.println("发送模板消息的结果："+result);
	}
	
	
	/**
	 * 简历投递成功
	 * @param toUser
	 * @throws IOException
	 */
	public void sendResumeSubmitTemplate(String toUser,ResumeSend resumeSend)throws IOException{
		System.out.println(">>>>>>>>简历投递模板消息发送");
		String accessToken = fetcheAccessToken();
		String templateUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTime  = df.format(new Date());
		String json = TemplateData.New()
				.setTouser(toUser)
				.setTemplate_id(template_resume_Send)
				.setTopcolor("#743A3A")
				.setUrl(resumeSend.getUrl())//设置投递状态查看链接
				.add("first", resumeSend.getFirst()+"\n", "#743A3A")
				.add("company", resumeSend.getCompany(), "#0000FF")
				.add("job", resumeSend.getJob(), "#ff0000")
				.add("time", dateTime, "#0000FF")
				.add("remark", "\n 跳转链接查看详情", "#008000")
				.build();
		
		System.out.println("发送时间："+dateTime);
		System.out.println("模板消息的主题内容："+json);
		
		String result = LocalHttpClient.jsonPost(templateUrl, json);
		System.out.println("发送模板消息的结果："+result);
	}
	
	/**
	 * 简历反馈提醒
	 * @param toUser
	 * @throws IOException
	 */
	public void sendResumeFeedbackTemplate(String toUser,ResumeFeedBack resumeFeedBack)throws IOException{
		System.out.println(">>>>>>>>简历投递反馈提醒模板消息发送");
		String accessToken = fetcheAccessToken();
		String templateUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTime  = df.format(new Date());
		String json = TemplateData.New()
				.setTouser(toUser)
				.setTemplate_id(template_resume_feedback)
				.setTopcolor("#743A3A")
				.setUrl(resumeFeedBack.getUrl())//设置投递状态查看链接
				.add("first", resumeFeedBack.getFirst()+"\n", "#743A3A")
				.add("company", resumeFeedBack.getCompany(), "#0000FF")
				.add("time", dateTime, "#0000FF")
				.add("job", resumeFeedBack.getJob(), "#ff0000")
				.add("result", resumeFeedBack.getResult(), "#0000FF")
				.add("remark", "\n 跳转链接查看详情", "#008000")
				.build();
		
		System.out.println("发送时间："+dateTime);
		System.out.println("模板消息的主题内容："+json);
		
		String result = LocalHttpClient.jsonPost(templateUrl, json);
		System.out.println("发送模板消息的结果："+result);
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
