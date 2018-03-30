package com.media.dingping.cameramonitor.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zsc on 2017/5/21 0021.
 * <p>
 * 获取摄像头列表返回的实体类，暂无数据，先空
 */
public class Cameras {


    /**
     * msg : 获取成功
     * status : 200
     * datas : [{"code":"ttttt","address":"dianpung","name":"4下","caremaID":"322324234","terminal_ID":"39f8fcf798d9e89"},{"code":"eeee","address":"dianpiung","name":"peng","caremaID":"242342424","terminal_ID":"41NA9BHH5BG5EL8"}]
     */

    private String msg;
    private int status;
    private List<DatasBean> datas;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DatasBean> getDatas() {
        return datas;
    }

    public void setDatas(List<DatasBean> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "Cameras{" +
                "msg='" + msg + '\'' +
                ", status=" + status +
                ", datas=" + datas +
                '}';
    }

    public static class DatasBean implements Serializable{
        /**
         * code : ttttt
         * address : dianpung
         * name : 4下
         * caremaID : 3223242A34
         * terminal_ID : 39f8fcf798d9e89
         */

        private String code;
        private String address;
        private String name;
        private String caremaID;
        private String terminal_ID;
        private String playVerifyCode="";

        public String getPlayVerifyCode() {
            if (caremaID.contains("/")){
                return caremaID.split("/")[1];
            }
            return playVerifyCode;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCaremaID() {
            if (caremaID.contains("/")){
                return caremaID.split("/")[0];
            }
            return caremaID;
        }

        public void setCaremaID(String caremaID) {
            this.caremaID = caremaID;
        }

        public String getTerminal_ID() {
            return terminal_ID;
        }

        public void setTerminal_ID(String terminal_ID) {
            this.terminal_ID = terminal_ID;
        }

        @Override
        public String toString() {
            return "DatasBean{" +
                    "code='" + code + '\'' +
                    ", address='" + address + '\'' +
                    ", name='" + name + '\'' +
                    ", caremaID='" + caremaID + '\'' +
                    ", terminal_ID='" + terminal_ID + '\'' +
                    ", playVerifyCode='" + playVerifyCode + '\'' +
                    '}';
        }
    }
}
