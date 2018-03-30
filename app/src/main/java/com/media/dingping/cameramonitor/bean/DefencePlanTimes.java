package com.media.dingping.cameramonitor.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/26 0026.
 * 获取布防计划的设置
 */

public class DefencePlanTimes implements Serializable {


    /**
     * data : {"startTime":"23:20","stopTime":"23:21","period":"0,1,6","enable":0}
     * code : 200
     * msg : 操作成功!
     */

    private DataBean data;
    private String code;
    private String msg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean implements Serializable{
        /**
         * startTime : 23:20
         * stopTime : 23:21
         * period : 0,1,6
         * enable : 0
         */

        private String startTime;
        private String stopTime;
        private String period;
        private int enable;

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getStopTime() {
            return stopTime;
        }

        public void setStopTime(String stopTime) {
            this.stopTime = stopTime;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public int getEnable() {
            return enable;
        }

        public void setEnable(int enable) {
            this.enable = enable;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "startTime='" + startTime + '\'' +
                    ", stopTime='" + stopTime + '\'' +
                    ", period='" + period + '\'' +
                    ", enable=" + enable +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DefencePlanTimes{" +
                "data=" + data +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
