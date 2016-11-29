package com.chinalooke.yuwan.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 读取通讯录工具类
 * Created by xiao on 2016/11/29.
 */

public class ContactsEngine {

    public static List<HashMap<String, String>> getAllContacts(Context context) {
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        // 1.获取内容解析者
        ContentResolver resolver = context.getContentResolver();
        // 2.内容提供者地址 com.android.contacts  例如百度搜索jdk：www.baidu.com/jdk
        // raw_contacts表地址：raw_contacts  veiw_data表的地址：data
        // 3.生成查询地址
        Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");// http://
        Uri data_uri = Uri.parse("content://com.android.contacts/data");
        // 4.查询数据，先查询raw_contacts的contact_id
        // projection:查询的字段
        Cursor cursor = resolver.query(raw_uri, new String[]{"contact_id"},
                null, null, null);
        // 5.解析cursor
        while (cursor.moveToNext()) {
            // columnIndex : 表示字段的索引
            String contact_id = cursor.getString(0);
            // cursor.getString(cursor.getColumnIndex("contact_id"));//getColumnIndex
            // : 获取字段在cursor的索引，一般用在查询字段比较多的情况
            if (contact_id != null) {
                // 6.根据contact_id去查询veiw_data表, 报空指针异常的2种情况：null.方法、参数为空
                Cursor c = resolver.query(data_uri, new String[]{"data1",
                                "mimetype"}, "raw_contact_id=?",
                        new String[]{contact_id}, null);
                HashMap<String, String> map = new HashMap<>();
                // 7.解析c
                while (c.moveToNext()) {
                    // 获取数据
                    String data1 = c.getString(0);
                    String mimetype = c.getString(1);
                    // 8.根据mimetype判断data1的类型
                    if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                        // 电话
                        // 9.保存数据
                        map.put("phone", data1);
                    } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                        // 姓名
                        map.put("name", data1);
                    }
                }
                // 10.添加到集合中
                list.add(map);
                // 11.关闭cursor
                c.close();
            }
        }
        cursor.close();
        return list;
    }
}
