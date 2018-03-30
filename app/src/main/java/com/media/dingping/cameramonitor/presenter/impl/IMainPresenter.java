package com.media.dingping.cameramonitor.presenter.impl;

/**
 * Created by Administrator on 2017/5/21 0021.
 */
public interface IMainPresenter {
    /**
     * 获取摄像头列表
     *
     * @param userName
     */
    void getCameraList(String userName);

    /**
     * 获取直播流地址
     *
     * @param userName
     * @param camreaId
     */
    void getCameraPlayUrl(String userName, String camreaId,String verifyCode);

    /**
     * 判断是否为第一次使用
     *
     * @return
     */
    void isFirstTime();

    /**
     * 控制摄像头移动
     *
     * @param direction   控制方向0-上 1-下 2-左 3-右
     * @param startOrStop 0开始  1停止
     */
    void controlCamera(int direction, int startOrStop);

    /**
     * 断开摄像头控制
     */
    void close();

    /**
     * @return
     */
    String getLoginUser();

}
