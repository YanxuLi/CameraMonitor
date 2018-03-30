package com.media.dingping.cameramonitor.bean;

/**
 * Created by Administrator on 2017/9/7 0007.
 */

public class SetDefenceVoivePatmer {
    String accessToken; //授权过程获取的access_token
    String deviceSerial;//设备序列号
    int channelNo;//通道号，不传表示设备本身
    String startTime;//开始时间，如16:00，默认为00:00
    String stopTime;//结束时间，如16:00，n00:00表示第二天的0点，结束时间必须大于开始时间，默认为n00:00
    String period;//周一~周日，用0~6表示，英文逗号分隔，默认为0,1,2,3,4,5,6
    String enable;//是否启用：1-启用，0-不启用，默认为1

    public SetDefenceVoivePatmer(){}

    public SetDefenceVoivePatmer(String accessToken, String deviceSerial, int channelNo, String startTime, String stopTime, String period, String enable) {
        this.accessToken = accessToken;
        this.deviceSerial = deviceSerial;
        this.channelNo = channelNo;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.period = period;
        this.enable = enable;
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

    public int getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(int channelNo) {
        this.channelNo = channelNo;
    }

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

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "SetDefenceVoivePatmer{" +
                "accessToken='" + accessToken + '\'' +
                ", deviceSerial='" + deviceSerial + '\'' +
                ", channelNo=" + channelNo +
                ", startTime='" + startTime + '\'' +
                ", stopTime='" + stopTime + '\'' +
                ", period='" + period + '\'' +
                ", enable='" + enable + '\'' +
                '}';
    }
}
