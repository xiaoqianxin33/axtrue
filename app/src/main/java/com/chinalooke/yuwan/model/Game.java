package com.chinalooke.yuwan.model;

/**
 * 游戏实体类
 * Created by xiao on 2016/11/9.
 */

public class Game {
    public String gameId;
    public String thumb;
    public String name;
    public int _id;

    public Game(String name, String gameId, String thumb) {
        this.name = name;
        this.gameId = gameId;
        this.thumb = thumb;
    }

    public Game() {
    }
}
