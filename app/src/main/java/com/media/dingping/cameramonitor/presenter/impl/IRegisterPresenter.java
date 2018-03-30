package com.media.dingping.cameramonitor.presenter.impl;

/**
 * Created by Administrator on 2017/5/19 0019.
 */
public interface IRegisterPresenter  extends IBasePresenter{

    /**
     * 注册动作
     *
     * @param user     用户名
     * @param password 密码
     */
    void register(String user, String password);
}
