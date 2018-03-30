package com.media.dingping.cameramonitor.bean;

/**
 * Created by Administrator on 2017/9/7 0007.
 */

public class IsDefence {
    String accessToken; //授权过程获取的access_token
    String deviceSerial;//设备序列号
    int isDefence;//是否布防

    public IsDefence() {
    }

    public IsDefence(String accessToken, String deviceSerial, int isDefence) {
        this.accessToken = accessToken;
        this.deviceSerial = deviceSerial;
        this.isDefence = isDefence;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public int getIsDefence() {
        return isDefence;
    }

    public void setIsDefence(int isDefence) {
        this.isDefence = isDefence;
    }

    @Override
    public String toString() {
        return "IsDefence{" +
                "accessToken='" + accessToken + '\'' +
                ", deviceSerial='" + deviceSerial + '\'' +
                ", isDefence=" + isDefence +
                '}';
    }
}
