package com.media.dingping.cameramonitor.bean;


/**
 * Created by Administrator on 2017/6/19 0019.
 */

public class UpdateInfo {
    private String version;
    private String url;


    public UpdateInfo(String version, String url) {
        this.version = version;
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "version='" + version + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
