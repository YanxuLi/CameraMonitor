package com.media.dingping.cameramonitor.view;

import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.bean.StreamInfo;

/**
 * Created by lu on 2017/5/25.
 */

public interface IMainAcvityView {
    /*
    是否是第一次进入
     */
    void isFirstLogin();

    /*
    获取数据列表成功
     */
    void showDataListSuccessd(Cameras cameras);

    /*
    获取数据列表失败
     */
    void showDataListFaild();

    /*
    获取直播流url成功
     */
    void showgPlayUrlSuccessed(StreamInfo streamInfo);

    /*
   获取直播流url失败
    */
    void showgPlayUrlFaild();

    /*
    播放视频的界面更新
     */
    public void refreshview(int number);

    String getLoginUser();
}
