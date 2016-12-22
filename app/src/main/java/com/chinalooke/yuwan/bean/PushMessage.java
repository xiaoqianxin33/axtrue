package com.chinalooke.yuwan.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 推送消息实体类
 * Created by xiao on 2016/12/22.
 */
@DatabaseTable
public class PushMessage {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * content : 今天在您的网吧举办的守望先锋对战第1场需要判定输赢，请查看
     * temp : 战场id , 场次号数字
     * title : 雷熊
     * type : gameDesk
     */
    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String content;
    @DatabaseField
    private String temp;
    @DatabaseField
    private String title;
    @DatabaseField
    private String type;
    @DatabaseField
    private boolean isDone;
    @DatabaseField
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
