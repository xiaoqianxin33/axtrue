package com.chinalooke.yuwan.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 手机信息工具类
 * Created by xiao on 2016/11/29.
 */

public class PhoneInfoUtil {
    private TelephonyManager telephonyManager;
    private Context cxt;

    public PhoneInfoUtil(Context context) {
        cxt = context;
        telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取电话号码
     */
    public String getNativePhoneNumber() {
        String NativePhoneNumber;
        NativePhoneNumber = telephonyManager.getLine1Number();
        return NativePhoneNumber;
    }

    /**
     * 获取手机服务商信息
     */
    public String getProvidersName() {
        String ProvidersName = "N/A";
        try {
            /*
      国际移动用户识别码
     */
            String IMSI = telephonyManager.getSubscriberId();
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
            System.out.println(IMSI);
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProvidersName;
    }

    public String getPhoneInfo() {
        TelephonyManager tm = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);

        return "\nDeviceId(IMEI) = " + tm.getDeviceId() +
                "\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() +
                "\nLine1Number = " + tm.getLine1Number() +
                "\nNetworkCountryIso = " + tm.getNetworkCountryIso() +
                "\nNetworkOperator = " + tm.getNetworkOperator() +
                "\nNetworkOperatorName = " + tm.getNetworkOperatorName() +
                "\nNetworkType = " + tm.getNetworkType() +
                "\nPhoneType = " + tm.getPhoneType() +
                "\nSimCountryIso = " + tm.getSimCountryIso() +
                "\nSimOperator = " + tm.getSimOperator() +
                "\nSimOperatorName = " + tm.getSimOperatorName() +
                "\nSimSerialNumber = " + tm.getSimSerialNumber() +
                "\nSimState = " + tm.getSimState() +
                "\nSubscriberId(IMSI) = " + tm.getSubscriberId() +
                "\nVoiceMailNumber = " + tm.getVoiceMailNumber();
    }
}
