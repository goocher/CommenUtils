package com.gooch.commenutils.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
/**
 * Created by goocher on 2014/10/4.
 */
public class SDCardHelper {
	// 检测sd卡是否挂载
	public static boolean isSDCardMounted() {
		// 如果拓展的存储器处于MEDIA_MOUNTED状态，我们即认为sd卡挂载了
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	// 获取SD卡的根目录
	public static String getSDCardBaseDir() {
		if (isSDCardMounted()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			return null;
		}
	}

	// 获取sd卡的公有目录,参数为Environment类里的常量
	public static String getSDCardPublicDir(String type) {
		return Environment.getExternalStoragePublicDirectory(type).toString();
	}

	// 获取sd卡的私有的cache目录
	public static String getSDCardPrivateCacheDir(Context context) {
		return context.getExternalCacheDir().getAbsolutePath();
	}

	// 获取sd卡的私有目录的files目录的方法
	public static String getSDCardPrivateFilesDir(Context context, String type) {
		return context.getExternalFilesDir(type).getAbsolutePath();
	}

	// 获取sd卡的大小,单位是M
	public static long getSDCardSize() {
		if (isSDCardMounted()) {
			// 最小数据块
			StatFs fs = new StatFs(getSDCardBaseDir());
			long count = fs.getBlockCount();
			long size = fs.getBlockSize();
			return count * size / 1024 / 1024;
		} else {
			return 0;
		}
	}

	// 获取sd卡的剩余空间,单位是M
	public static long getSDCardFreeSize() {
		if (isSDCardMounted()) {
			StatFs fs = new StatFs(getSDCardBaseDir());
			long count = fs.getFreeBlocks();
			long size = fs.getBlockSize();
			return count * size / 1024 / 1024;
		} else {
			return 0;
		}
	}

	// 向sd卡的公有目录存数据
	public static boolean saveFileToSDCardPublicDir(byte[] data, String type,
			String fileName) {
		if (isSDCardMounted()) {
			BufferedOutputStream bos = null;
			File file = Environment.getExternalStoragePublicDirectory(type);
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						file, fileName)));
				bos.write(data);
				bos.flush();
				return true;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (bos != null) {
						bos.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		} else {
			return false;
		}
	}

	// 向自己指定的sd卡的目录中村数据
	public static boolean saveFileToSDCardCustomDir(byte[] data, String dir,
			String fileName) {
		if (isSDCardMounted()) {
			BufferedOutputStream bos = null;
			File file = new File(getSDCardBaseDir() + File.separator + dir);
			if (!file.exists()) {
				// 多级目录的创建
				file.mkdirs();
			}
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						file, fileName)));
				bos.write(data);
				bos.flush();
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (bos != null) {
						bos.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;

		} else {
			return false;
		}
	}

	// 向sd卡的私有目录的file目录存内容
	public static boolean saveFileToSDCardPrivateFilesDir(byte[] data,
			String type, String fileName, Context context) {
		if (isSDCardMounted()) {
			BufferedOutputStream bos = null;
			// File file=new File(getSDCardPrivateFilesDir(context, type));
			File file = context.getExternalFilesDir(type);
			if (!file.exists()) {
				file.mkdirs();
			}
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						file, fileName)));
				bos.write(data);
				bos.flush();
				return true;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (bos != null) {
						bos.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;

		} else {
			return false;
		}

	}

	public static boolean saveFileToSDCardPrivateCacheDir(byte[] data,
			String fileName, Context context) {
		if (isSDCardMounted()) {
			BufferedOutputStream bos = null;
			File file = context.getExternalCacheDir();
			if (!file.exists()) {
				file.mkdirs();
			}
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						file, fileName)));
				bos.write(data);
				bos.flush();
				return true;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (bos != null) {
						bos.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;

		} else {
			return false;
		}
	}

	public static byte[] loadFileFromSDCard(String fileDir) {
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			bis = new BufferedInputStream(
					new FileInputStream(new File(fileDir)));
			byte[] buffer = new byte[1024 * 8];
			int len = 0;
			while ((len = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
				baos.flush();
			}
			return baos.toByteArray();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
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
