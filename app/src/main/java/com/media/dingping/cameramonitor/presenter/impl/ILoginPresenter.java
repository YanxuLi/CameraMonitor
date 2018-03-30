package com.media.dingping.cameramonitor.presenter.impl;

/**
 * Created by Administrator on 2017/5/19 0019.
 */
public interface ILoginPresenter extends IBasePresenter{

    /**
     * 登陆动作
     *
     * @param user     用户名
     * @param password 密码
     */
    void login(String user, String password);

    boolean getLoginState();

}
