package com.fizzblock.wechat.util.common.impl;

import java.util.Arrays;

import com.fizzblock.wechat.util.common.DecriptUtil;

public class SignCheckUtil {
	
	private static String token = "oFWRoUnETDW4XpORscqN";
    /**
	 * 校验签名
	 */
	public static boolean checkSignature(String signature, String timestamp, String nonce) {
		System.out.println("signature:" + signature + "timestamp:" + timestamp + "nonc:" + nonce);
		
		// 将token、timestamp、nonce三个参数进行字典序排序
		String[] arr = new String[] { token, timestamp, nonce };
		Arrays.sort(arr);

		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		
		//自己生成的签名摘要
		String genSignature = DecriptUtil.SHA1(content.toString());
		
		//比较签名
		System.out.println("签名校验成功与否："+genSignature.equals(signature));
		return genSignature != null ? genSignature.equals(signature) : false;
	}

}
