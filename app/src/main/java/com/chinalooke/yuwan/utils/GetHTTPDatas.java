package com.chinalooke.yuwan.utils;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.model.ResultDatas;

/**
 * Created by Administrator on 2016/8/25.
 */
public class GetHTTPDatas {
    private RequestQueue mQueue;
    private ResultDatas result;

    public ResultDatas getResult() {
            return result;
    }

    public void setResult(ResultDatas result) {
        this.result = result;
    }

    public RequestQueue getmQueue() {
        return mQueue;
    }

    public void setmQueue(RequestQueue mQueue) {
        this.mQueue = mQueue;
    }

    //网络获取  验证手机号是否注册
    public void getHTTPIsPhoneExists(String URLPhone){

        Log.d("TAG","电话网址----------"+URLPhone);
        StringRequest stringRequest = new StringRequest(URLPhone,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        //解析数据

                        if(response!=null){
                            result=new ResultDatas();
                           // result=new ResultDatas();
                            result = AnalysisJSON.getAnalysisJSON().AnalysisJSONResult(response);

                            System.out.println("----result----"+result.getResult());
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(stringRequest);


    }

}
