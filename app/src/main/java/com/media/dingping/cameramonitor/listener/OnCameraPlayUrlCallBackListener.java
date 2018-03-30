package com.media.dingping.cameramonitor.listener;

import com.media.dingping.cameramonitor.bean.StreamInfo;

/**
 * Created by zsc on 2017/5/21 0021.
 */
public interface OnCameraPlayUrlCallBackListener{
    void OnSuccess(StreamInfo streamInfo);
    void OnGetURLError();
}
