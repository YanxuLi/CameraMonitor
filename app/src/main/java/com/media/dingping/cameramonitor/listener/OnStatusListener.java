package com.media.dingping.cameramonitor.listener;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public interface OnStatusListener {

    void onSuccess();

    void onError();

    /**
     * 账户已存在
     */
    void onExists();



}
