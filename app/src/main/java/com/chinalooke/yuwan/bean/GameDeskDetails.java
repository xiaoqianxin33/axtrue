package com.chinalooke.yuwan.bean;

import android.os.Parcel;

import java.io.Serializable;
import java.util.List;

/**
 * 游戏桌详情实体类
 * Created by xiao on 2016/8/24.
 */
public class GameDeskDetails implements Serializable {


    /**
     * Msg :
     * Result : {"details":"填写游戏规则请填写游戏规则请填写游戏规则","gameName":"游戏002","gamePay":"100.00","netBarAddress":"123456","netBarName":"黑豹网咖1","peopleNumber":"50","players":{"left":[{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"123","status":"pedding","userId":"3"},{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","status":"pedding","userId":"75"},{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"123","status":"pedding","userId":"46"},{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","status":"done","userId":"78"}],"right":[{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"霸天虎","status":"pedding","userId":"67"},{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"123","status":"pedding","userId":"3"}]},"startTime":"2016-09-06 00:00:00","status":"pedding","winer":""}
     * Success : true
     */

    private String Msg;
    /**
     * details : 填写游戏规则请填写游戏规则请填写游戏规则
     * gameName : 游戏002
     * gamePay : 100.00
     * netBarAddress : 123456
     * netBarName : 黑豹网咖1
     * peopleNumber : 50
     * players : {"left":[{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"123","status":"pedding","userId":"3"},{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","status":"pedding","userId":"75"},{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"123","status":"pedding","userId":"46"},{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","status":"done","userId":"78"}],"right":[{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"霸天虎","status":"pedding","userId":"67"},{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"123","status":"pedding","userId":"3"}]}
     * startTime : 2016-09-06 00:00:00
     * status : pedding
     * winer :
     */

    private ResultBean Result;
    private boolean Success;


    protected GameDeskDetails(Parcel in) {
        Msg = in.readString();
        Success = in.readByte() != 0;
    }


    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public ResultBean getResult() {
        return Result;
    }

    public void setResult(ResultBean Result) {
        this.Result = Result;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public static class ResultBean implements Serializable {
        private String details;
        private String gameName;
        private String gamePay;
        private String netBarAddress;
        private String netBarName;
        private String peopleNumber;
        private PlayersBean players;
        private String startTime;
        private String status;
        private String winer;
        private String bgImage;
        private String roomId;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getBgImage() {
            return bgImage;
        }

        public void setBgImage(String bgImage) {
            this.bgImage = bgImage;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getGameName() {
            return gameName;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public String getGamePay() {
            return gamePay;
        }

        public void setGamePay(String gamePay) {
            this.gamePay = gamePay;
        }

        public String getNetBarAddress() {
            return netBarAddress;
        }

        public void setNetBarAddress(String netBarAddress) {
            this.netBarAddress = netBarAddress;
        }

        public String getNetBarName() {
            return netBarName;
        }

        public void setNetBarName(String netBarName) {
            this.netBarName = netBarName;
        }

        public String getPeopleNumber() {
            return peopleNumber;
        }

        public void setPeopleNumber(String peopleNumber) {
            this.peopleNumber = peopleNumber;
        }

        public PlayersBean getPlayers() {
            return players;
        }

        public void setPlayers(PlayersBean players) {
            this.players = players;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getWiner() {
            return winer;
        }

        public void setWiner(String winer) {
            this.winer = winer;
        }

        public static class PlayersBean implements Serializable {
            /**
             * headImg : http://121.42.172.61/public/images/defaultAvatar.png
             * nickName : 123
             * status : pedding
             * userId : 3
             */

            private List<LeftBean> left;
            /**
             * headImg : http://121.42.172.61/public/images/defaultAvatar.png
             * nickName : 霸天虎
             * status : pedding
             * userId : 67
             */

            private List<RightBean> right;

            public List<LeftBean> getLeft() {
                return left;
            }

            public void setLeft(List<LeftBean> left) {
                this.left = left;
            }

            public List<RightBean> getRight() {
                return right;
            }

            public void setRight(List<RightBean> right) {
                this.right = right;
            }

            public static class LeftBean implements Serializable {
                private String headImg;
                private String nickName;
                private String status;
                private String userId;

                public String getHeadImg() {
                    return headImg;
                }

                public void setHeadImg(String headImg) {
                    this.headImg = headImg;
                }

                public String getNickName() {
                    return nickName;
                }

                public void setNickName(String nickName) {
                    this.nickName = nickName;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getUserId() {
                    return userId;
                }

                public void setUserId(String userId) {
                    this.userId = userId;
                }
            }

            public static class RightBean implements Serializable {
                private String headImg;
                private String nickName;
                private String status;
                private String userId;

                public String getHeadImg() {
                    return headImg;
                }

                public void setHeadImg(String headImg) {
                    this.headImg = headImg;
                }

                public String getNickName() {
                    return nickName;
                }

                public void setNickName(String nickName) {
                    this.nickName = nickName;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getUserId() {
                    return userId;
                }

                public void setUserId(String userId) {
                    this.userId = userId;
                }
            }
        }
    }
}
