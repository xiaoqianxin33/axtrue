package com.chinalooke.yuwan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.chinalooke.yuwan.model.LoginUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * 用户登录信息工具
 * Created by Administrator on 2016/8/31.
 */
public class LoginUserInfoUtils {


    public static final String FILENAME = "loginUserInfo";
    public static final String KEY = "UserInfo";
    private SharedPreferences sharedPreferences;

    private LoginUserInfoUtils() {
    }


    private static class UserInfoUtils {
        private static LoginUserInfoUtils loginUserInfoUtils = new LoginUserInfoUtils();
    }

    public static LoginUserInfoUtils loginUserInfoUtils;
    private static LoginUser.ResultBean userInfo;

    public LoginUser.ResultBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(LoginUser.ResultBean userInfo) {
        this.userInfo = userInfo;
    }


    public static LoginUserInfoUtils getLoginUserInfoUtils() {
        return UserInfoUtils.loginUserInfoUtils;
       /* if (loginUserInfoUtils==null){
            loginUserInfoUtils=new LoginUserInfoUtils();
            return loginUserInfoUtils;
        }else{
            return loginUserInfoUtils;
        }*/
    }

    /*
    //同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
        SharedPreferences sharedPreferences= getSharedPreferences("loginUserInfo",
                                                                 Activity.MODE_PRIVATE);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        String name =sharedPreferences.getString("name", "");
        String habit =sharedPreferences.getString("habit", "");
    //使用toast信息提示框显示信息
        Toast.makeText(this, "读取数据如下："+"\n"+"name：" + name + "\n" + "habit：" + habit,
        Toast.LENGTH_LONG).show();
        SharedPreferences
    */
    public static void saveLoginUserInfo(Context context, String key, LoginUser.ResultBean userInfo) throws IOException {
        saveObject(context, key, userInfo);
    }

    /**
     * 清除用户登录信息
     *
     * @param context 上下文对象
     */
    public void clearData(Context context) {
        SharedPreferences.Editor sharedata = context.getSharedPreferences(FILENAME, 0).edit();
        sharedata.clear().apply();
    }

    public LoginUser.ResultBean getLoginUserInfo(Context context, String key) throws IOException {
        this.userInfo = (LoginUser.ResultBean) readObject(context, key);
        return userInfo;
    }

    /**
     * @param context 上下文对象
     * @param key     键值
     * @param obj     对象
     */
    public static void saveObject(Context context, String key, Object obj) {
        try {
            // 保存对象
            SharedPreferences.Editor sharedata = context.getSharedPreferences(FILENAME, 0).edit();
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(Base64.encode(bos.toByteArray(), Base64.DEFAULT));
            //保存该16进制数组
            sharedata.putString(key, bytesToHexString);
            sharedata.apply();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("", "保存obj失败");
        }

    }

    /**
     * desc:将数组转为16进制
     *
     * @param bArray
     * @return modified:
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * desc:获取保存的Object对象
     *
     * @param context 上下文对象
     * @param key     存储的键值
     * @return modified:
     */
    public static Object readObject(Context context, String key) {
        try {
            SharedPreferences sharedata = context.getSharedPreferences(FILENAME, 0);
            if (sharedata.contains(key)) {
                String string = sharedata.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //所有异常返回null
        return null;

    }

    /**
     * desc:将16进制的数据转为数组
     * <p>创建人：聂旭阳 , 2014-5-25 上午11:08:33</p>
     *
     * @param data
     * @return modified:
     */
    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   //// 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); //// 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }


}
