package com.gooch.commenutils.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Created by goocher on 2014/10/4.
 */
public class MD5Helper {

	private static final char hexDigsits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	//封装的，获取md5的方法
	public static String getMD5(String inStr){
		//加密需要的字节数组，于是首先拿到内容的字节数组
		byte [] inStrBytes=inStr.getBytes();
		
		//消息摘要对象,指定为MD5方式
		try {
			MessageDigest messageDigest=MessageDigest.getInstance("MD5");
			//把摘要对象中的数据设置为我们的内容
			messageDigest.update(inStrBytes);
			//拿到MD5算法的结果
			byte [] messageDbytes=messageDigest.digest();
			//我们把结果转化为16进制的格式
			char [] str=new char[messageDbytes.length*2];
			//循环是使用的角标
			int k=0;
			for (int i = 0; i < messageDbytes.length; i++) {
				byte temp=messageDbytes[i];
				//把一个字节分裂成两个部分，分别转化为十六进制的字符
				str[k++]=hexDigsits[temp>>>4&0xf];
				str[k++]=hexDigsits[temp&0xf];
			}
			return new String(str);
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return null;
	}
}
