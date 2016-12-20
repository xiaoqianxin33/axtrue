package com.chinalooke.yuwan.engine;

import android.app.Activity;
import android.text.TextUtils;

import com.bigkoo.pickerview.OptionsPickerView;
import com.chinalooke.yuwan.bean.LevelList;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 弹出选项框engine
 * Created by xiao on 2016/12/20.
 */

public class PickerEngine {

    //弹出积分级别选择框
    public static void alertLevelPicker(Activity context, OptionsPickerView.OnOptionsSelectListener listener) {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<ArrayList<String>> list2 = new ArrayList<>();
        String level = PreferenceUtils.getPrefString(context, "level", "");
        if (!TextUtils.isEmpty(level)) {
            Gson gson = new Gson();
            LevelList levelList = gson.fromJson(level, LevelList.class);
            List<LevelList.ResultBean> result = levelList.getResult();
            if (result != null) {
                for (LevelList.ResultBean resultBean : result) {
                    list.add(resultBean.getLevelName() + "(最低" + resultBean.getLeast() + ")");
                }
                for (int i = 1; i < list.size() + 1; i++) {
                    ArrayList<String> list1 = new ArrayList<>();
                    for (int j = i; j <= list.size(); j++) {
                        list1.add(j + "");
                    }
                    list2.add(list1);
                }
                OptionsPickerView<String> optionsPickerView = new OptionsPickerView<>(context);
                optionsPickerView.setPicker(list, list2, true);
                optionsPickerView.setTitle("积分级别选择");
                optionsPickerView.setOnoptionsSelectListener(listener);
                optionsPickerView.setCyclic(false);
                optionsPickerView.show();
            }
        }
    }
}


