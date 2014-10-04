package com.gooch.commenutils.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * Created by goocher on 2014/10/4.
 */
public class HttpURLConnHelper {

	public static byte [] loadByteFromURL(String url){
		HttpURLConnection httpConn=null;
		BufferedInputStream bis=null;
		try {
			URL urlObj=new URL(url);
			httpConn=(HttpURLConnection) urlObj.openConnection();
			httpConn.setRequestMethod("GET");
			
			httpConn.setDoInput(true);
			httpConn.setConnectTimeout(5000);
			httpConn.connect();
			if(httpConn.getResponseCode()==200){
				bis=new BufferedInputStream(httpConn.getInputStream());
				return streamToByte(bis);
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return null;
	}
	
	public static byte[] streamToByte(InputStream is){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		int c=0;
		byte [] buffer=new byte[8*1024];
		try {
			while ((c=is.read(buffer))!=-1) {
				baos.write(buffer,0,c);
				baos.flush();
				
			}
			
			return baos.toByteArray();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try {
				if(baos!=null){
					baos.close();
				}
				
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			
			
		}
		
		
		return null;
	}
	
}
