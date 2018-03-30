package com.media.dingping.cameramonitor.view.model;

/**
 * Created by lu on 2017/5/24.
 */

public class ShowDataInfo {
    String number; //屏编号
    String name;//屏名称

    public ShowDataInfo(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ShowDataInfo{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
