package com.media.dingping.cameramonitor.presenter;

import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.bean.StreamInfo;
import com.media.dingping.cameramonitor.listener.OnCameraArrayCallBackListener;
import com.media.dingping.cameramonitor.listener.OnCameraPlayUrlCallBackListener;
import com.media.dingping.cameramonitor.listener.OnFirstLoginListener;
import com.media.dingping.cameramonitor.model.CameraModel;
import com.media.dingping.cameramonitor.model.impl.ICameraModel;
import com.media.dingping.cameramonitor.presenter.impl.IMainPresenter;
import com.media.dingping.cameramonitor.view.IMainAcvityView;

/**
 * Created by zsc on 2017/5/21 0021.
 */
public class MainPresenter implements IMainPresenter, OnCameraArrayCallBackListener, OnCameraPlayUrlCallBackListener, OnFirstLoginListener {
    private ICameraModel mCameraModel;
    private IMainAcvityView mMainView;

    public MainPresenter(IMainAcvityView mMainView) {
        mCameraModel = new CameraModel();
        this.mMainView = mMainView;
    }

    @Override
    public void getCameraList(String userName) {
        mCameraModel.getCameraList(userName, this);
    }

    @Override
    public void getCameraPlayUrl(String userName, String camreaId, String verifyCode) {
        mCameraModel.getCameraToken(userName, camreaId,verifyCode,this);
    }

    @Override
    public void isFirstTime() {
        mCameraModel.isFristTime(this);
    }


    @Override
    public void controlCamera(int direction, int startOrStop) {
        mCameraModel.controlCamera(direction, startOrStop, null);
    }

    @Override
    public void close() {
        mCameraModel.close();
    }


    /**
     * 获取终端列表成功
     *
     * @param cameras
     */
    @Override
    public void OnSuccess(Cameras cameras) {
        mMainView.showDataListSuccessd(cameras);
    }

    /**
     * 获取终端列表失败
     */
    @Override
    public void OnGetArrayError() {
        mMainView.showDataListFaild();
    }

    /**
     * 获取直播流地址
     *
     * @param streamInfo
     */
    @Override
    public void OnSuccess(StreamInfo streamInfo) {
        mMainView.showgPlayUrlSuccessed(streamInfo);
    }

    /**
     * 获取摄像头直播流失败
     */
    @Override
    public void OnGetURLError() {
        //缺一接口
        mMainView.showgPlayUrlFaild();
    }

    @Override
    public void onFirst() {
        mMainView.isFirstLogin();
    }

    public String getLoginUser() {
        return mCameraModel.getUsersInfo().getUserName();
    }

}
