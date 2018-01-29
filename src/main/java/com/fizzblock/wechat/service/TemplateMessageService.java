package com.fizzblock.wechat.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fizzblock.template.message.TemplateData;
import com.fizzblock.wechat.template.BDResource;
import com.fizzblock.wechat.util.httpclient.LocalHttpClient;

@Service(value="templateService")
public class TemplateMessageService {
	//资源下载模板id
	String templateId_download = "NvEiHqNvFJaQTjH7PmE6-f-JgXz2AhCTa_k6gdCj3X8";
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
	
	private String fetcheAccessToken() throws IOException{
	    //获取accessToken的方式
		String jsonResult = LocalHttpClient.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx416c5da1eef311e6&secret=30d7fdede88444ff927934f50b367669");
		JSONObject jsonObj = JSON.parseObject(jsonResult);
		String accessToken = jsonObj.getString("access_token"); 
		System.out.println("accessToken获取："+accessToken);
		
		return accessToken;
	}
	
}
