package com.gooch.commenutils.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
/**
 * Created by goocher on 2014/10/4.
 */
public class AssetsHelper {
	public static byte[] getDataFromAssets(Context context, String fileName) {

		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			return streamToByte(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static void saveDataFromAssetsToSDCard(Context context,
			String fileName, String pathName) {
		FileOutputStream os = null;
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			File file = new File(pathName);
			if (!file.exists()) {
				file.createNewFile();
			}
			// 这个boolean为true是续写的意思，如果它为false，是覆盖的意思
			os = new FileOutputStream(file, true);
			int c = 0;
			byte[] buffer = new byte[1024];
			while ((c = in.read(buffer)) != -1) {
				os.write(buffer, 0, c);
				os.flush();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static byte[] streamToByte(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c = 0;
		byte[] buffer = new byte[8 * 1024];
		try {

			while ((c = is.read(buffer)) != -1) {
				baos.write(buffer, 0, c);
				baos.flush();

			}
			return baos.toByteArray();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
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
