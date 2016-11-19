package com.chinalooke.yuwan.utils;

import android.util.Log;

import com.chinalooke.yuwan.model.ResultDatas;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * json工具类
 * Created by Administrator on 2016/8/23.
 */

/**
 * 解析数据
 */
public class AnalysisJSON {

    private static AnalysisJSON analysisJSON;

    public static AnalysisJSON getAnalysisJSON() {
        if (analysisJSON == null) {
            analysisJSON = new AnalysisJSON();
        } else {
            return analysisJSON;
        }
        return analysisJSON;
    }

    //解析手机号码是否注册
    public ResultDatas AnalysisJSONResult(String datas) {
        JSONObject jsonObject;
        ResultDatas result = null;
        Log.d("TAG", "++++" + datas);
        try {
            if (datas != null) {
                jsonObject = new JSONObject(datas);
                result = new ResultDatas();
                result.setSuccess(jsonObject.getString("Success"));
                result.setMsg(jsonObject.getString("Msg"));
                result.setResult(jsonObject.getString("Result"));
                Log.d("TAG", "++++" + result.getResult());


            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TAG", "解析错误");

        }
        return result;
    }


    public void getSubStringAddress(String address) {
        String[] city = new String[3];
        String province = address.substring(0, address.indexOf("省"));
        if ("".equals(province)) {
            province = address.substring(0, address.indexOf("市"));
        }
    }


    public static boolean analysisJson(String response) {
        if (response != null) {
            String substring = response.substring(11, 15);
            return "true".equals(substring);
        } else {
            return false;
        }
    }

}
