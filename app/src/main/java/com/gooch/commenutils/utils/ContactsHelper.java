package com.gooch.commenutils.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
/**
 * Created by goocher on 2014/10/4.
 */
public class ContactsHelper {
	// ContentResolver是通过uri来获取数据的，
	// 也就是说，电话号有电话号的uri
	// 邮箱有邮箱的uri
	// data有data的uri
	// 这一系列uri是contentprovider自己定义的，我们只是调用
	private static String uri_rawcontacts = "content://com.android.contacts/raw_contacts";
	private static String uri_contacts_phones = "content://com.android.contacts/data/phones";
	private static String uri_contacts_emails = "content://com.android.contacts/data/emails";
	private static String uri_contacts_data = "content://com.android.contacts/data";

	// 查询联系人的信息
	public static List<Map<String, Object>> selectContactsInfo(
			ContentResolver resolver) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 第一个参数是要获取的内容是什么
		// 第二个参数获取这个uri对应的表的那些字段
		// 第三个参数是你的查询条件，
		// 第四个参数是你的查询条件中的？的替换占位符
		// 第五个参数是你的查询结果的排序条件
		Cursor contactCursor = resolver.query(Uri.parse(uri_rawcontacts),
				new String[] { "_id", "display_name" }, null, null, null);
		while (contactCursor.moveToNext()) {
			// 这个就是我们这一条联系人信息
			Map<String, Object> map = new HashMap<String, Object>();
			// contactCursor.getColumnIndex("_id")意思是，在cursor里找名字为"_id"这个字段的列，返回它是第几列。
			// getInt(第几列),
			int contactsId = contactCursor.getInt(contactCursor
					.getColumnIndex("_id"));
			String displayName = contactCursor.getString(contactCursor
					.getColumnIndex("display_name"));
			// 保存联系人的id和名字
			map.put("_id", contactsId);
			map.put("display_name", displayName);
			// 根据联系人的id去data表中获取电话号码的信息
			// 获取contactsid对应的电话号码
			Cursor phoneCursor = resolver.query(Uri.parse(uri_contacts_phones),
					new String[] { "raw_contact_id", "data1" },
					"raw_contact_id=?", new String[] { contactsId + "" }, null);
			// StringBuilder stringBuilder=new StringBuilder();
			ArrayList<String> phoneList = new ArrayList<String>();
			while (phoneCursor.moveToNext()) {
				phoneList.add(phoneCursor.getString(phoneCursor
						.getColumnIndex("data1")));

			}
			// 把这个人的电话号放入map中
			map.put("phones", phoneList);
			if (phoneCursor != null) {
				phoneCursor.close();
			}

			// 根据联系人的id去data表中获取email的信息
			Cursor emailCursor = resolver.query(Uri.parse(uri_contacts_emails),
					new String[] { "raw_contact_id", "data1" },
					"raw_contact_id=?", new String[] { contactsId + "" }, null);
			// StringBuilder stringBuilder2=new StringBuilder();
			ArrayList<String> emailList = new ArrayList<String>();
			while (emailCursor.moveToNext()) {
				emailList.add(emailCursor.getString(emailCursor
						.getColumnIndex("data1")));
				// stringBuilder2.append("|");
			}
			// 把这个人的邮箱放入map中
			map.put("emails", emailList);
			if (emailCursor != null) {
				emailCursor.close();
			}
			list.add(map);

		}// end while
		if (contactCursor != null) {
			contactCursor.close();
		}

		return list;

	}

	// 修改联系人
	public static boolean updateContacts(ContentResolver resolver,
			Map<String, Object> map, String id) {
		// 键值对，和map类似，多用于contentprovider中。
		ContentValues values = new ContentValues();
		// 把名字放入contentValues里
		values.put("display_name", map.get("display_name").toString());
		values.put("display_name_alt", map.get("display_name").toString());
		values.put("sort_key", map.get("display_name").toString());
		values.put("sort_key_alt", map.get("display_name").toString());
		// 更新raw_contacts表中的数据
		int result1 = resolver.update(Uri.parse(uri_rawcontacts), values,
				"_id=?", new String[] { id });

		// 更改data表中的姓名
		values.clear();
		values.put("data1", map.get("display_name").toString());
		values.put("data2", map.get("display_name").toString());
		int result2 = resolver.update(Uri.parse(uri_contacts_data), values,
				"raw_contact_id=? and mimetype_id=?", new String[] { id, "7" });
		// 更改data表中的phone
		values.clear();
		values.put("data1", map.get("phone").toString());
		values.put("data2", 2);
		int result3 = resolver.update(Uri.parse(uri_contacts_data), values,
				"raw_contact_id=? and mimetype_id=?", new String[] { id, "5" });

		// 更改data表中的email
		values.clear();
		values.put("data1", map.get("email").toString());
		values.put("data2", 1);
		int result4 = resolver.update(Uri.parse(uri_contacts_data), values,
				"raw_contact_id=? and mimetype_id=?", new String[] { id, "1" });

		if (result1 > 0 && result2 > 0 && result3 > 0 && result4 > 0) {
			return true;
		} else {
			return false;
		}

	}

	// 根据联系人姓名删除联系人信息
	public static boolean deleteContacts(ContentResolver resolver, String displayName) {
		int data = resolver.delete(Uri.parse(uri_rawcontacts),

		"display_name=?", new String[] { displayName });
		if (data > 0) {
			return true;
		} else {
			return false;
		}
	}

	// 新增数据
	public static void insertContact(ContentResolver resolver, Map<String, Object> map) {
		ContentValues values = new ContentValues();
		// 往raw_contacts表中插入一条空的数据，目的是获取联系人的id
		Uri newUri = resolver.insert(Uri.parse(uri_rawcontacts), values);
		long id = ContentUris.parseId(newUri);

		// 往data表中插入联系人的姓名数据
		values.put("raw_contact_id", id);
		// values.put("mimetype_id", 7);必须要插入mimetype字段，而不是直接写mimetype_id
		values.put("mimetype", "vnd.android.cursor.item/name");
		values.put("data1", map.get("display_name").toString());
		values.put("data2", map.get("display_name").toString());
		resolver.insert(Uri.parse(uri_contacts_data), values);

		// 往data表中插入电话信息
		values.clear();
		values.put("raw_contact_id", id);
		values.put("mimetype", "vnd.android.cursor.item/phone_v2");
		values.put("data1", map.get("phone").toString());
		values.put("data2", 2);
		resolver.insert(Uri.parse(uri_contacts_data), values);
		// 往data表中插入联系人的email
		values.clear();
		values.put("raw_contact_id", id);
		values.put("mimetype", "vnd.android.cursor.item/email_v2");
		values.put("data1", map.get("email").toString());
		values.put("data2", 1);
		resolver.insert(Uri.parse(uri_contacts_data), values);
	}

}
