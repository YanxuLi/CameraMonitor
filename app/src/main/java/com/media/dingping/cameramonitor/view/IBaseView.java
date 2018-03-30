package com.media.dingping.cameramonitor.view;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public interface IBaseView{

    /**
     * 获取输入用户名
     *
     * @return
     *
     */
    String getUserName();

    /**
     * 获取输入用户密码
     *
     * @return
     */
    String getPassWord();

    void showLoading();

    void hideLoading();
}
