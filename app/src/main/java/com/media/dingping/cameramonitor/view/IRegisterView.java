package com.media.dingping.cameramonitor.view;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public interface IRegisterView  extends IBaseView{

    /*
    用户密码确认
     */
    String getConfirmPassword();

    /**
     * 注册成功
     */
    void registerSuccess();

    /**
     * 注册失败
     */
    void registerError();


    void onExists();

    /**
     * 保存刚注册的的用户位最新登陆用户
     */
    void saveLastUserInfo();


}
