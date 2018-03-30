package com.media.dingping.cameramonitor.bean;

/**
 * Created by Administrator on 2017/5/25 0025.
 */
public class StatusBean {

    //json数据:
//    {
//        "status": 200,
//            "msg": "登录成功"
//    }

    private int status;
    private String msg;

    public StatusBean(int status, String msg) {
        this.status = status;
        this.msg = msg;

    }

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

    @Override
    public String toString() {
        return "StatusBean{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }
}
