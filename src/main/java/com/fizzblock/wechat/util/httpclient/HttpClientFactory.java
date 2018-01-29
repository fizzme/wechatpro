package com.fizzblock.wechat.util.httpclient;

import java.net.ProxySelector;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

/**
 * 不同的方式构建HttpClient
 * @author glen
 *
 */
public class HttpClientFactory {

	
	/**
	 * 构建无连接池管理的HttpClient
	 * @return HttpClient
	 */
	public static HttpClient createHttpClient() {
		try {
			SSLContext e = SSLContexts.custom().useSSL().build();
			SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(e,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
			return HttpClientBuilder.create().setRoutePlanner(routePlanner).setSSLSocketFactory(sf).build();
		} catch (KeyManagementException ex1) {
			ex1.printStackTrace();
//			logger.error("", arg2);
		} catch (NoSuchAlgorithmException ex2) {
//			logger.error("", arg3);
			ex2.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 创建基于SSL协议的HttpClient,从连接池中
	 * @param int maxTotal 连接池的连接总数
	 * @param int maxPerRoute 每个路由的最大并发数
	 * @return
	 */
	public static HttpClient createHttpClient(int maxTotal, int maxPerRoute) {
		SSLContext ctx = null;
		try {
			ctx = SSLContexts.custom().useSSL().build();
			//构建上下文
			//构建安全套接字工厂
			SSLConnectionSocketFactory sf= new SSLConnectionSocketFactory(ctx, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			PoolingHttpClientConnectionManager poolingHttpClientConnetionManger = new PoolingHttpClientConnectionManager();
			poolingHttpClientConnetionManger.setMaxTotal(maxTotal);//最大连接数
			poolingHttpClientConnetionManger.setDefaultMaxPerRoute(maxPerRoute);//每个路由的并发数
			SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());//路由代理
			//构造返回HttpClient
			return HttpClientBuilder.create().setConnectionManager(poolingHttpClientConnetionManger)
					.setRoutePlanner(routePlanner).setSSLSocketFactory(sf).build();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 根据密钥库等信息建立ssl的HttpClient
	 * @param keystore
	 * @param keyPassword
	 * @param supportedProtocols
	 * @return
	 */
	public static HttpClient createKeyMaterialHttpClient(KeyStore keystore, String keyPassword,
			String[] supportedProtocols) {
		try {
			SSLContext e = SSLContexts.custom().useSSL().loadKeyMaterial(keystore, keyPassword.toCharArray()).build();
			SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(e, supportedProtocols, (String[]) null,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
			return HttpClientBuilder.create().setRoutePlanner(routePlanner).setSSLSocketFactory(sf).build();
		} catch (KeyManagementException arg5) {
//			logger.error("", arg5);
		} catch (NoSuchAlgorithmException arg6) {
//			logger.error("", arg6);
		} catch (UnrecoverableKeyException arg7) {
//			logger.error("", arg7);
		} catch (KeyStoreException arg8) {
//			logger.error("", arg8);
		}

		return null;
	}
	
	
}
