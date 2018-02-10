package com.fizzblock.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.client.HttpClient;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fizzblock.wechat.menu.Button;
import com.fizzblock.wechat.menu.ClickButton;
import com.fizzblock.wechat.menu.ComplexButton;
import com.fizzblock.wechat.menu.Menu;
import com.fizzblock.wechat.menu.ViewButton;
import com.fizzblock.wechat.util.httpclient.JSONUtil;
import com.fizzblock.wechat.util.httpclient.LocalHttpClient;
/**
 * 创建菜单测试类
 * @author glen
 *
 */
public class MenuTest {

	
	/**
	 * 新创建menu的测试
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	@Test
	public void wxMenuCreate1() throws IOException, KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException{
		//create menu创建菜单
//		String jsonMenu  = createMenu();
		String jsonMenu  = createMenuNew();
		
		
		//	http请求
//		String accessToken = "5_I23VKk4j3ptENhstAaJGFuOFFSxb6qtvN5s8hzPOlI0BFtQlgku_ZrURcbcaxzqUozm76RsNDVtjfF8Y7pDXTs8LgGnD6CWSj63UxA_i_Zfl-2qt8qVmUShFgbKrviI__7OdAY6Y6saAQzR4IHVeAIAUTH";
		String accessToken = fetcheAccessToken();
		
		String createMenuUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+accessToken;
		try {
			String result = LocalHttpClient.jsonPost(createMenuUrl, jsonMenu);
			System.out.println("菜单创建完成");
			System.out.println("result:"+result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 传统menu的创建测试
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
//	@Test
	public void wxMenuCreate2() throws IOException, KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException{

		//create menu创建菜单
		String jsonMenu  = createMenu();
		
		//	http请求
//		String accessToken = "5_I23VKk4j3ptENhstAaJGFuOFFSxb6qtvN5s8hzPOlI0BFtQlgku_ZrURcbcaxzqUozm76RsNDVtjfF8Y7pDXTs8LgGnD6CWSj63UxA_i_Zfl-2qt8qVmUShFgbKrviI__7OdAY6Y6saAQzR4IHVeAIAUTH";
		String accessToken = fetcheAccessToken();
		String createMenuUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+accessToken;

		URL url  = new URL(createMenuUrl);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		//使用自定义的信任管理器
		TrustManager [] tm ={new MyX509TrustManager()};
		
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
		
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		conn.setSSLSocketFactory(ssf);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		//设置请求方式
		conn.setRequestMethod("POST");
		//向输出流写菜单结构
		OutputStream outputStream  = conn.getOutputStream();
		outputStream.write(jsonMenu.getBytes("UTF-8"));
		outputStream.close();
		
		//取得输入流
		InputStream inputStream  = conn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		//读取响应信息
		StringBuffer sb = new StringBuffer();
		String str =null;
		while((str = bufferedReader.readLine())!= null){
			sb.append(str);
		}
		//关闭释放资源
		bufferedReader.close();
		inputStreamReader.close();
		inputStream.close();
		conn.disconnect();
		//输出菜单创建结果
		System.out.println(sb);
		
		
	}
	
	
	private String fetcheAccessToken() throws IOException{
	    //获取accessToken的方式
//	    https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx416c5da1eef311e6&secret=30d7fdede88444ff927934f50b367669
		//获取的accessToken 5_rJ0lyh3jarA1NDeK_UICzIEhgG-IdcBIHblSxn2n-qEzpBN_eYnvhZnNRKtlsquXucjmuxlUBK-KJEcZ0ClrtveDCCFR6ozj0UeYVhBfa9ocOEBRJ8Wr7p2r_yl9y0WfxUa7vMJSEYPSTFdUOXFcAIARAI
		String result = LocalHttpClient.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx416c5da1eef311e6&secret=30d7fdede88444ff927934f50b367669");
		JSONObject jsonObj = JSON.parseObject(result);
		String accessToken = jsonObj.getString("access_token"); 
		System.out.println("accessToken获取："+accessToken);
		
		return accessToken;
	}

	//创建菜单
	private String createMenu() {
		
		//请求地址，微信会进行回调
		String bindUser = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Ffizzblock.bceapp.com%2FgetSNSUserinfo2.do&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
//		String loginUser ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Ffizzblock.bceapp.com%2FgetUserInfoBind.do&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

		//卧槽，这个地址不对...
//		String loginUser ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Ffizzblock.bceapp.com%2FgetSNSUserinfo.php&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		String loginUser ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Ffizzblock.bceapp.com%2FgetUserInfoBind.do&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
		
		//新增的链接按钮
				//链接按钮一
				ViewButton linkbtn1 = new ViewButton();
				linkbtn1.setName("用户绑定");
//				btn2.setType("view");
				linkbtn1.setUrl(bindUser);
				
				//链接按钮二
				ViewButton linkbtn2 = new ViewButton();
				linkbtn2.setName("用户登陆");
//				btn2.setType("view");
				linkbtn2.setUrl(loginUser);
				
				
		//--------------一级菜单两个btn1/btn2------------------------		
				//带点击事件的按钮
				ClickButton btn1 = new ClickButton();
				btn1.setName("按钮测试");
//				btn1.setType("click");
				btn1.setKey("V1001_TODAY_MUSIC");
				
				//链接按钮
				ViewButton btn2 = new ViewButton();
				btn2.setName("我的微站");
//				btn2.setType("view");
				btn2.setUrl("http://fizzblock.bceapp.com/home.do");

		//----------------一个复合菜单btn3有两个子菜单btn31/32----------------------		
				ClickButton btn31 = new ClickButton();
				btn31.setName("hello word");
//				btn31.setType("click");
				btn31.setKey("V1001_HELLO_WORLD");
				
				ClickButton btn32 = new ClickButton();
				btn32.setName("赞我们一下");
				btn32.setKey("V1001_GOOD");
				//链接按钮
				ViewButton btn33 = new ViewButton();
				btn33.setName("访问百度");
				btn33.setUrl("http://www.baidu.com");
				
				//复合按钮包含2个click类型的按钮
				ComplexButton btn3 = new ComplexButton();
				btn3.setName("自助服务");
				btn3.setSub_button(new Button[]{btn2,btn31,btn32,btn33});

		//------------------------------------		
				
				//创建菜单对象,以数组的形式包裹btn1/btn2/btn3
				Menu menu = new Menu();
//				menu.setButton(new Button[]{linkbtn1,linkbtn2,btn1,btn2,btn3});
//				menu.setButton(new Button[]{linkbtn1,linkbtn2,btn2,btn3});
				menu.setButton(new Button[]{linkbtn1,linkbtn2,btn3});
				
				//将菜单对象转换成Json字符串
				String jsonMenu = JSONUtil.toJSONString(menu);
				System.out.println(jsonMenu);
		return jsonMenu;
	}
	
	
	private String createMenuNew() {
		
		//请求地址，微信会进行回调
		String bindUser = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Fwww.fizzblock.cn%2Fwechat-demo%2FgetSNSUserinfo2.do&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		
		//这个地址不对...
		String loginUser ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx416c5da1eef311e6&redirect_uri=http%3A%2F%2Fwww.fizzblock.cn%2Fwechat-demo%2FgetUserInfoBind.do&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
		
		//复合按钮一-----------------------------------------
		//注意复制的时候，看看对象的名是进行了修改
		//新增的链接按钮
		//链接按钮一
		ViewButton linkbtn1 = new ViewButton();
		linkbtn1.setName("用户绑定");
		linkbtn1.setUrl(bindUser);
		
		//链接按钮二
		ViewButton linkbtn2 = new ViewButton();
		linkbtn2.setName("用户登陆");
		linkbtn2.setUrl(loginUser);
		
		ClickButton clickBtn = new ClickButton();
		clickBtn.setName("绑定new");
		clickBtn.setKey("bindNew");
		
		ClickButton ticketClickBtn1 = new ClickButton();
		ticketClickBtn1.setName("排队取号");
		ticketClickBtn1.setKey("takeTicket");
		
		ClickButton ticketClickBtn2 = new ClickButton();
		ticketClickBtn2.setName("排队进度");
		ticketClickBtn2.setKey("ticketWait");
		
		//复合按钮包含2个click类型的按钮
		ComplexButton cmbtn1 = new ComplexButton();
		cmbtn1.setName("用户服务");
		cmbtn1.setSub_button(new Button[]{linkbtn1,clickBtn,linkbtn2,ticketClickBtn1,ticketClickBtn2});
		
		//复合按钮二-----------------------------------------
		
		ClickButton btn11 = new ClickButton();
		btn11.setName("简历投递");
		btn11.setKey("sendResume");
		
		ClickButton btn12 = new ClickButton();
		btn12.setName("投递状态");
		btn12.setKey("resumeStatus");
		
		//复合按钮包含2个click类型的按钮
		ComplexButton cmbtn2 = new ComplexButton();
		cmbtn2.setName("我的招聘");
		cmbtn2.setSub_button(new Button[]{btn11,btn12});
		
		
		//--------------复合按钮三------------------------		
		//带点击事件的按钮
		ClickButton btn1 = new ClickButton();
		btn1.setName("按钮测试");
		btn1.setKey("V1001_TODAY_MUSIC");
		
		//链接按钮
		ViewButton btn2 = new ViewButton();
		btn2.setName("我的微站");
		btn2.setUrl("http://www.fizzblock.cn/wechat-demo/");
		
		ClickButton btn31 = new ClickButton();
		btn31.setName("资源下载");
		btn31.setKey("btn_download");
		
		ClickButton btn32 = new ClickButton();
		btn32.setName("赞我们一下");
		btn32.setKey("V1001_GOOD");
		//链接按钮
		ViewButton btn33 = new ViewButton();
		btn33.setName("访问百度");
		btn33.setUrl("http://www.baidu.com");
		
		//复合按钮包含2个click类型的按钮
		ComplexButton cmbtn3 = new ComplexButton();
		cmbtn3.setName("自助服务");
		cmbtn3.setSub_button(new Button[]{btn2,btn31,btn32,btn33});
		
		//------------------------------------		
		
		//创建菜单对象,以数组的形式包裹btn1/btn2/btn3
		Menu menu = new Menu();
		menu.setButton(new Button[]{cmbtn1,cmbtn2,cmbtn3});
		
		//将菜单对象转换成Json字符串
		String jsonMenu = JSONUtil.toJSONString(menu);
		System.out.println(jsonMenu);
		return jsonMenu;
	}
	
	
}
