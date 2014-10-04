package com.gooch.commenutils.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by goocher on 2014/10/4.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	
	
	private static List<DataOBServer> list=null;
	static {
		list=new ArrayList<DataOBServer>();
	}
	public void registerOBServer(DataOBServer dataOBServer){
		list.add(dataOBServer);
	}
	
	public void unregisterOBServer(DataOBServer dataOBServer){
		list.remove(dataOBServer);
	}
	
	
	private static final String DB_NAME = "db_openhelper";
	private static final int VERSION = 1;

	// 内部实际操作数据库的对象
	private SQLiteDatabase dbConn = null;

	// 构造方法，要求一定要调用父类的构造方法，而父类的构造方法有四个参数，
	// 这里为了方便使用，我们对于父类的构造方法进行封装
	public MySQLiteOpenHelper(Context context) {
		// 第一个参数是上下文
		// 第二个参数是数据库的名字
		// 第三个参数是cursor工厂，指定null就有默认的工厂
		// 第四个参数是数据库的版本要求版本 >=1
		super(context, DB_NAME, null, VERSION);
		// getWritableDatabase();
		// 它是指打开数据库无论何时，都是以可写的方式打开。
		// getReadableDatabase();
		// 它在大多情况下打开数据库也是可写的，但是当，磁盘空间满了，这是只能得到一个只读的数据库。
		dbConn = this.getReadableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// 这个db就是它内部封装的SQLiteDatabase
		db.execSQL("create table if not exists tb_words(_id integer primary key autoincrement,english,chinese)");
	}

	// 版本更新
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (newVersion > oldVersion) {
			// 保不保存之前的数据，结合需求。
			db.execSQL("drop table if exists tb_words");
			onCreate(db);
		}
	}

	// 返回结果为cursor的查询
	public Cursor selectCursor(String sql, String[] selectionArgs) {
		return dbConn.rawQuery(sql, selectionArgs);
	}

	// 返回结果为List<Map<String,Object>>的查询
	public List<Map<String, Object>> selectList(String sql,
			String[] selectionArgs) {
		return cursorToList(selectCursor(sql, selectionArgs));
	}

	// 把一个cursor转化为list<map>的方法，一个二维结构的等价替换。
	public static List<Map<String, Object>> cursorToList(Cursor cursor) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 需要知道这一个查询结果都有什么列名
		// new String []{"username","password"};
		String[] arrColumnName = cursor.getColumnNames();

		// cursor.moveToNext()是有一个指向行的游标
		// 在cursor最开始产生的时候，游标指向第一个的前一个
		// 当存在下一个的时候，这个方法会指向下一个，并且返回true
		// 当不存在下一个的时候，就直接返回false；

		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < arrColumnName.length; i++) {

				// cursor.getString(columnIndex);
				Object cols_value = cursor.getString(i);
				// username---0
				// password---1
				// 一个map对应一行
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

	// 针对于update、insert、delete的自定义语句的封装
	public boolean execData(String sql, Object[] bindArgs) {
		try {
			dbConn.execSQL(sql, bindArgs);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	// 针对于update、insert、delete的自定义语句的封装
	public boolean execData(String sql) {
		try {
			dbConn.execSQL(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	// 下面三个方法是 用于配合contentprovider来使用的方法
	// 关于这个方法，第一个参数是表名。
	// 第二个参数是说，当你的插入语句一个值都没有，谁来作为null，插入进表。
	public int insert(String table, String nullColumnHack, ContentValues values) {
		// insert into tb_my(nullColumnHack) values(null);
		return (int) dbConn.insert(table, nullColumnHack, values);
	}

	// 关于这个方法，第一参数是表名
	// 第二个参数是删除的条件，第三个就是删除条件的替换占位符
	public int delete(String table, String selection, String[] selectionArgs) {
		return dbConn.delete(table, selection, selectionArgs);
	}

	// 这是更新的方法，第一个参数是表名，第二个参数是你要更新的键值对，第三个参数是更新条件，第四个是更新条件替换占位符
	public int update(String table, ContentValues values, String selection,
			String[] selectionArgs) {
		return dbConn.update(table, values, selection, selectionArgs);
	}

	public Cursor query(String table, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return dbConn.query(table, projection, selection, selectionArgs, null,
				null, sortOrder);
	}

	public void destroy() {
		if (dbConn != null) {
			dbConn.close();
		}
	}

}
interface DataOBServer{
	public void onDataChanged();
}
