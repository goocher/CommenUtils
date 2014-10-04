package com.gooch.commenutils.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
/**
 * Created by goocher on 2014/10/4.
 */
public class MySQLiteDataBaseHelper {
	// 数据库的路径
	private final static String BASE_PATH = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
			.getAbsolutePath();
	// 数据库的名字，即文件名
	private final static String DB_NAME = "my.db";
	// 完全路径名
	private final static String DB_PATH_NAME = BASE_PATH + File.separator
			+ DB_NAME;

	private SQLiteDatabase dbConn = null;

	public MySQLiteDataBaseHelper() {

		File file = new File(DB_PATH_NAME);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 第一个参数是数据库文件的完全路径名，第二个参数是一个可缺省的Cursor工厂，第三个参数是数据库的打开的方式读写
		// cursor它是一个数据库的查询结果，cursor可以被遍历，就能拿到所有的查询结果，可以理解cursor就是一个二维的表结构
		dbConn = SQLiteDatabase.openDatabase(DB_PATH_NAME, null,
				SQLiteDatabase.OPEN_READWRITE);
		// execSQL可以执行任意的sql语句，包括ddl，dml
		dbConn.execSQL("create table if not exists tb_words (_id integer primary key autoincrement,english,chinese)");

	}

	// 这是查询的方法，返回值为cursor，它是带替换占位符的
	public Cursor selectCursor(String sql, String[] selectionArgs) {

		// 在数据查询的时候，我们经常使用替换占位符
		// String str=select * from tb_words where id=? and sex=? and name=?;
		// xxxx(str,new String [] {"1","1","zhangsan"});
		return dbConn.rawQuery(sql, selectionArgs);
	}

	// 这是查询方法，直接返回list；
	public List<Map<String, Object>> selectList(String sql,
			String[] selectionArgs) {
		return cursorToList(selectCursor(sql, selectionArgs));
	}

	// 把一个cursor转化为list<map>的方法，一个二维结构的等价替换。
	public List<Map<String, Object>> cursorToList(Cursor cursor) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 需要知道这一个查询结果都有什么列名
		//new String []{"username","password"};
		String[] arrColumnName = cursor.getColumnNames();

		// cursor.moveToNext()是有一个指向行的游标
		// 在cursor最开始产生的时候，游标指向第一个的前一个
		// 当存在下一个的时候，这个方法会指向下一个，并且返回true
		// 当不存在下一个的时候，就直接返回false；

		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < arrColumnName.length; i++) {
				
				//cursor.getString(columnIndex);
				Object cols_value = cursor.getString(i);
				//username---0
				//password---1
				//一个map对应一行
				map.put(arrColumnName[i], cols_value);
			}
			list.add(map);

		}

		// 需要注意的是：cursor使用完毕后，要关闭
		if (cursor != null) {
			cursor.close();
		}

		return list;
	}

	// 执行任意语句，主要是为了insert、delete、update
	public boolean execData(String sql, Object[] bindArgs) {
		try {
			dbConn.execSQL(sql, bindArgs);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	// 关闭数据库的链接
	public void destroy() {
		if (dbConn != null) {
			dbConn.close();
		}
	}

}
