package com.media.dingping.cameramonitor.model.impl;

import com.media.dingping.cameramonitor.bean.DefenceVoiceType;
import com.media.dingping.cameramonitor.bean.IsDefence;
import com.media.dingping.cameramonitor.bean.SetDefenceVoivePatmer;
import com.media.dingping.cameramonitor.bean.StreamInfo;
import com.media.dingping.cameramonitor.bean.TokenInfo;
import com.media.dingping.cameramonitor.bean.UserInfo;
import com.media.dingping.cameramonitor.listener.OnCameraArrayCallBackListener;
import com.media.dingping.cameramonitor.listener.OnCameraPlayUrlCallBackListener;
import com.media.dingping.cameramonitor.listener.OnFirstLoginListener;
import com.media.dingping.cameramonitor.listener.OnStatusListener;

/**
 * Created by zsc on 2017/5/19 0019.
 */
public interface ICameraModel {

    /**
     * 登陆动作
     *
     * @param user     用户名
     * @param password 密码
     */
    void Login(String user, String password, OnStatusListener listener);

    /**
     * 注册动作
     *
     * @param user     用户名
     * @param password 密码
     */
    void register(String user, String password, OnStatusListener listener);


    /**
     * 修改密码
     *
     * @param user     用户名
     * @param oldPassword 密码
     */
    void modify(String user, String oldPassword,String newPassword, OnStatusListener listener);


    /**
     * 获取登录过的用户信息列表
     *
     * @return
     */
    UserInfo getUsersInfo();

    /**
     * 保存用户账户跟密码
     *
     * @param info
     */
    void saveUserInfo(UserInfo info);


    /**
     * 获取摄像头列表
     *
     * @param userName
     * @param listener
     */
    void getCameraList(String userName, OnCameraArrayCallBackListener listener);

    /**
     * 获取直播流地址
     *
     * @param tokenInfo
     * @param listner
     */
    void getCameraPlayUrl(TokenInfo tokenInfo, OnCameraPlayUrlCallBackListener listner);



    void getRealCameraInfo(StreamInfo streamInfo,OnCameraPlayUrlCallBackListener listner);
    /**
     * 获取摄像头token
     * @param userName
     * @param cameraId
     * @param listner
     */
    void getCameraToken(String userName, String cameraId,String verifyCode, OnCameraPlayUrlCallBackListener listner);

     /**
     *操控摄像头
     * @param direction
     * @param startOrStop
     * @param listener
     */
    void controlCamera(int direction,int startOrStop, OnStatusListener listener);

    /**
     * 查看是否是第一次安装打开
     *
     * @return
     */
    void isFristTime(OnFirstLoginListener listener);

    /**
     * 第一次打开后保存非第一次打开的动作
     */
    void saveNoFristTime();

    /**
     * 关闭当前控制连接
     */
    void close();


    boolean getLoginState();


    void update(OnUpdateListener listener);

    /**
     * 設置是否布放
     * */
    void setDefence(IsDefence isDefence);

    /**
     * 設置布放時間
     * */
    void setDefenceTime(SetDefenceVoivePatmer patmer);
    /**
     * 設置提醒聲音類型
     * */
    void setDefenceVoiceType(DefenceVoiceType defenceVoiceType);
}
