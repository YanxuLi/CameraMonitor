package com.media.dingping.cameramonitor.bean;

/**
 * Created by zsc on 2017/5/18 0018.
 * 用户信息实体类
 */
public class UserInfo {
    private String userName;
    private String passWord;

    public UserInfo(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
