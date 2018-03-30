package com.media.dingping.cameramonitor.main;

import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.bean.StreamInfo;
import com.media.dingping.cameramonitor.bean.UpdateInfo;

/**
 * Created by Administrator on 2017/6/6 0006.
 */
public interface IMainView {

    /**
     * 是否第一次登陆
     */

    void isFirstLogin();


    void onSuccessUrl(StreamInfo streamInfo);


    void onErrorUrl();

    void onSuccessCameras(Cameras cameras);


    void onErrorCameras();


    String getLoginUserName();

    void onUpdate(UpdateInfo url);
}
