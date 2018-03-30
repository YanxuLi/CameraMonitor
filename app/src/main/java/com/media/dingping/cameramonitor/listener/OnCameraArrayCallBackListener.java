package com.media.dingping.cameramonitor.listener;

import com.media.dingping.cameramonitor.bean.Cameras;

/**
 * Created by Administrator on 2017/5/21 0021.
 */
public interface OnCameraArrayCallBackListener{

    void OnSuccess(Cameras cameras);

    void OnGetArrayError();

}
