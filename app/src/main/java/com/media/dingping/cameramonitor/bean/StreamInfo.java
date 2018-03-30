package com.media.dingping.cameramonitor.bean;

import com.videogo.openapi.bean.EZCameraInfo;

/**
 * Created by Administrator on 2017/5/27 0027.
 */
public class StreamInfo {
    /**
     * 控制端口
     */
    private int ControlPort;
    /**
     * 直播流端口
     */
    private int StreamPort;
    /**
     * 中间件ip
     */
    private String ip = "";
    /**
     * 正在的tonken
     */
    private String AccessToken = "";

    private String deviceSerial = "";

    private String playVerifyCode="";

    public String getPlayVerifyCode() {
        return playVerifyCode;
    }

    public void setPlayVerifyCode(String playVerifyCode) {
        this.playVerifyCode = playVerifyCode;
    }

    public int getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    private int encrypt;

    private EZCameraInfo ezCameraInfo;

    public EZCameraInfo getEzCameraInfo() {
        return ezCameraInfo;
    }

    public void setEzCameraInfo(EZCameraInfo ezCameraInfo) {
        this.ezCameraInfo = ezCameraInfo;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    /**
     * @param controlPort
     * @param streamPort
     */

    public StreamInfo(int controlPort, int streamPort) {
        ControlPort = controlPort;
        StreamPort = streamPort;
    }




    public StreamInfo() {

    }

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public int getControlPort() {
        return ControlPort;
    }

    public void setControlPort(int controlPort) {
        ControlPort = controlPort;
    }

    public int getStreamPort() {
        return StreamPort;
    }

    public void setStreamPort(int streamPort) {
        StreamPort = streamPort;


    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "StreamInfo{" +
                "ControlPort=" + ControlPort +
                ", StreamPort=" + StreamPort +
                ", ip='" + ip + '\'' +
                ", AccessToken='" + AccessToken + '\'' +
                ", deviceSerial='" + deviceSerial + '\'' +
                ", ezCameraInfo=" + ezCameraInfo +
                '}';
    }


}
