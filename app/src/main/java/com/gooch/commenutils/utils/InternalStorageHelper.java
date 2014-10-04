package com.gooch.commenutils.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
/**
 * Created by goocher on 2014/10/4.
 */
public class InternalStorageHelper {
	public static final int CACHE = 0;
	public static final int FILES = 1;

	/**
	 * 内部存储数据 1、内部存储总是可用的 2、内部存储的文件默认只能被当前应用程序访问，它是私有的
	 * （如果有root权限就可以访问，如果数据被ContentProvider暴露，也可以被访问， 如果数据通过AIDL传输出去，也可以被访问）
	 * 3、如果应用程序卸载，那么内部存储的文件也会随着消失 （一）内部存储的写入： 方式一：通过上下文对象的openFileOutput(String
	 * name,int mode)拿到输出流。 方式二：File file=new
	 * File(getFilesDir(),"mytxt.txt");拿到文件对象。进而拿到输出流 （二）内部存储的读取：
	 * 方式一：通过上下文对象的openFileIntput(String name);拿到输入流。 方式二：File file=new
	 * File(getFilesDir(),"mytxt.txt");拿到文件对象。进而拿到输入流 （三）删除文件：通过上下文对象的boolean
	 * deleteFile("文件名");
	 * 
	 */
	// 保存文件到内部存储
	public static void saveFile(String fileName, String content, Context context) {
		if (fileName == null || "".equals(fileName)) {
			return;
		}
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(content.getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// 打开指定的内部存储文件
	public static byte[] loadFile(String fileName, Context context) {
		// 判断文件名
		if (fileName == null || "".equals(fileName)) {
			return null;
		}
		try {
			FileInputStream fis = context.openFileInput(fileName);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			fis.close();
			return bytes;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	// 删除文件操作
	public static boolean deleteFile(String fileName, Context context) {
		// 判断文件名
		if (fileName == null || "".equals(fileName)) {
			return false;
		}
		boolean flag = context.deleteFile(fileName);
		return flag;
	}

	// 第二种文件保存方式
	public static void otherWriter(String fileName, String content,
			Context context, int type) {
		// 判断文件名
		if (fileName == null || "".equals(fileName)) {
			return;
		}
		FileOutputStream outputStream = null;
		File file = null;
		switch (type) {
		case CACHE:
			file = new File(context.getCacheDir(), fileName);
			break;
		case FILES:
			file = new File(context.getFilesDir(), fileName);
			break;

		default:
			break;
		}
		// 对文件进行操作
		try {
			outputStream = new FileOutputStream(file);
			outputStream.write(content.getBytes());
			outputStream.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 第二种文件保存方式
	public static void otherWriter(String fileName, byte[] content,
			Context context, int type) {
		// 判断文件名
		if (fileName == null || "".equals(fileName)) {
			return;
		}
		FileOutputStream outputStream = null;
		File file = null;
		switch (type) {
		case CACHE:
			file = new File(context.getCacheDir(), fileName);
			break;
		case FILES:
			file = new File(context.getFilesDir(), fileName);
			break;

		default:
			break;
		}
		// 对文件进行操作
		try {
			outputStream = new FileOutputStream(file);
			outputStream.write(content);
			outputStream.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 第二种读取内部存储中的文件的方法
	public static byte[] otherReader(String fileName, Context context, int type) {
		// 判断文件名
		if (fileName == null || "".equals(fileName)) {
			return null;
		}
		FileInputStream inputStream = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		File file = null;
		switch (type) {
		case CACHE:
			file = new File(context.getCacheDir(), fileName);
			break;
		case FILES:
			file = new File(context.getFilesDir(), fileName);
			break;

		default:
			break;
		}
		// 操作文件
		try {
			inputStream = new FileInputStream(file);
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
				baos.flush();
			}
			return baos.toByteArray();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

}
