package com.chinalooke.yuwan.interf;

/**
 * Created by Administrator on 2016/8/26.
 */
public interface UpdateGetCity {
    public static final int PROVINCE_ID=1;
    public static final int CITY_ID=2;
    public static final int COUNY_ID=3;
    void updateCityInfo(String city, int id);
}
