package com.media.dingping.cameramonitor.bean;

/**
 * Created by Administrator on 2017/9/7 0007.
 */

public class DefenceVoiceType {
    String accessToken; //授权过程获取的access_token
    String deviceSerial;//设备序列号
    int defenceVoiceType;//提醒類型

    public DefenceVoiceType(){}
    public DefenceVoiceType(String accessToken, String deviceSerial, int defenceVoiceType) {
        this.accessToken = accessToken;
        this.deviceSerial = deviceSerial;
        this.defenceVoiceType = defenceVoiceType;
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

    public int getDefenceVoiceType() {
        return defenceVoiceType;
    }

    public void setDefenceVoiceType(int defenceVoiceType) {
        this.defenceVoiceType = defenceVoiceType;
    }

    @Override
    public String toString() {
        return "IsDefence{" +
                "accessToken='" + accessToken + '\'' +
                ", deviceSerial='" + deviceSerial + '\'' +
                ", isDefence=" + defenceVoiceType +
                '}';
    }
}
