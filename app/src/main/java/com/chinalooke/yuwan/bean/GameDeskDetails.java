package com.chinalooke.yuwan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 游戏桌详情实体类
 * Created by xiao on 2016/8/24.
 */
public class GameDeskDetails implements Serializable {


    /**
     * Msg :
     * Result : {"details":"请填写游戏规则","gameCount":"3","gameName":"魔兽世界","gamePay":"310.00","judgeLoser":"77","netBarAddress":"中国河南省洛阳市涧西区郑州路街道青滇路11号","netBarName":"摩卡","netbarId":"5","peopleNumber":"4","players":{"left":[],"right":[{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","isLoser":false,"status":"pedding","userId":"77"}]},"roomId":"1472187301148","startTime":"2016-08-26 12:54:00","status":"done","winer":"","winerForOfficial":[{"gameCount":"1","rating":"3","userId":"77"},{"gameCount":"3","rating":"2","userId":"77"}]}
     * Success : true
     */

    private String Msg;
    private ResultBean Result;
    private boolean Success;

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

    public static class ResultBean {
        /**
         * details : 请填写游戏规则
         * gameCount : 3
         * gameName : 魔兽世界
         * gamePay : 310.00
         * judgeLoser : 77
         * netBarAddress : 中国河南省洛阳市涧西区郑州路街道青滇路11号
         * netBarName : 摩卡
         * netbarId : 5
         * peopleNumber : 4
         * players : {"left":[],"right":[{"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","isLoser":false,"status":"pedding","userId":"77"}]}
         * roomId : 1472187301148
         * startTime : 2016-08-26 12:54:00
         * status : done
         * winer :
         * winerForOfficial : [{"gameCount":"1","rating":"3","userId":"77"},{"gameCount":"3","rating":"2","userId":"77"}]
         */

        private String details;
        private String gameCount;
        private String gameName;
        private String deskId;
        private String playerLevel;

        public String getPlayerLevel() {
            return playerLevel;
        }

        public void setPlayerLevel(String playerLevel) {
            this.playerLevel = playerLevel;
        }

        public String getDeskId() {
            return deskId;
        }

        public void setDeskId(String deskId) {
            this.deskId = deskId;
        }

        private String gamePay;
        private String judgeLoser;
        private String netBarAddress;
        private String netBarName;
        private String netbarId;
        private String peopleNumber;
        private PlayersBean players;
        private String roomId;
        private String startTime;
        private String status;
        private String winer;
        private List<WinerForOfficialBean> winerForOfficial;

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getGameCount() {
            return gameCount;
        }

        public void setGameCount(String gameCount) {
            this.gameCount = gameCount;
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

        public String getJudgeLoser() {
            return judgeLoser;
        }

        public void setJudgeLoser(String judgeLoser) {
            this.judgeLoser = judgeLoser;
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

        public String getNetbarId() {
            return netbarId;
        }

        public void setNetbarId(String netbarId) {
            this.netbarId = netbarId;
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

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
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

        public List<WinerForOfficialBean> getWinerForOfficial() {
            return winerForOfficial;
        }

        public void setWinerForOfficial(List<WinerForOfficialBean> winerForOfficial) {
            this.winerForOfficial = winerForOfficial;
        }

        public static class PlayersBean {
            private List<LeftBean> left;
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

            public static class RightBean {
                /**
                 * headImg : http://121.42.172.61/public/images/defaultAvatar.png
                 * isLoser : false
                 * status : pedding
                 * userId : 77
                 */

                private String headImg;
                private boolean isLoser;
                private String status;
                private String userId;
                private String nickName;

                public boolean isLoser() {
                    return isLoser;
                }

                public void setLoser(boolean loser) {
                    isLoser = loser;
                }

                public String getNickName() {
                    return nickName;
                }

                public void setNickName(String nickName) {
                    this.nickName = nickName;
                }

                public String getHeadImg() {
                    return headImg;
                }

                public void setHeadImg(String headImg) {
                    this.headImg = headImg;
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

            public static class LeftBean {
                /**
                 * headImg : http://121.42.172.61/public/images/defaultAvatar.png
                 * isLoser : false
                 * status : pedding
                 * userId : 77
                 */

                private String headImg;
                private boolean isLoser;
                private String status;
                private String userId;
                private String nickName;

                public boolean isLoser() {
                    return isLoser;
                }

                public void setLoser(boolean loser) {
                    isLoser = loser;
                }

                public String getNickName() {
                    return nickName;
                }

                public void setNickName(String nickName) {
                    this.nickName = nickName;
                }

                public String getHeadImg() {
                    return headImg;
                }

                public void setHeadImg(String headImg) {
                    this.headImg = headImg;
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


        static class WinerForOfficialBean {
            /**
             * gameCount : 1
             * rating : 3
             * userId : 77
             */

            private String gameCount;
            private String rating;
            private String userId;

            public String getGameCount() {
                return gameCount;
            }

            public void setGameCount(String gameCount) {
                this.gameCount = gameCount;
            }

            public String getRating() {
                return rating;
            }

            public void setRating(String rating) {
                this.rating = rating;
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
