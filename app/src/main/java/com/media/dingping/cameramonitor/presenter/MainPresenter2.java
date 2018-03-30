package com.media.dingping.cameramonitor.presenter;

import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.bean.StreamInfo;
import com.media.dingping.cameramonitor.bean.UpdateInfo;
import com.media.dingping.cameramonitor.listener.OnCameraArrayCallBackListener;
import com.media.dingping.cameramonitor.listener.OnCameraPlayUrlCallBackListener;
import com.media.dingping.cameramonitor.listener.OnFirstLoginListener;
import com.media.dingping.cameramonitor.model.CameraModel;
import com.media.dingping.cameramonitor.model.impl.ICameraModel;
import com.media.dingping.cameramonitor.model.impl.OnUpdateListener;
import com.media.dingping.cameramonitor.presenter.impl.IMainPresenter;
import com.media.dingping.cameramonitor.main.IMainView;

/**
 * Created by Administrator on 2017/6/6 0006.
 */
public class MainPresenter2 implements IMainPresenter, OnCameraArrayCallBackListener, OnCameraPlayUrlCallBackListener, OnFirstLoginListener,OnUpdateListener {
    private ICameraModel mCameraModel;
    private IMainView mMainView;


    public MainPresenter2(IMainView mMainView) {
        this.mMainView = mMainView;
        this.mCameraModel = new CameraModel();
//        mCameraModel.update(this);
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

    @Override
    public String getLoginUser() {
        return mCameraModel.getUsersInfo().getUserName();
    }


    @Override
    public void OnSuccess(Cameras cameras) {
        mMainView.onSuccessCameras(cameras);
    }

    @Override
    public void OnGetArrayError() {
        mMainView.onErrorCameras();
    }

    @Override
    public void OnSuccess(StreamInfo streamInfo) {
        mMainView.onSuccessUrl(streamInfo);
    }

    @Override
    public void OnGetURLError() {
        mMainView.onErrorUrl();
    }

    @Override
    public void onFirst() {
        mMainView.isFirstLogin();
    }

    @Override
    public void onUpdateUrl(UpdateInfo info) {
        mMainView.onUpdate(info);
    }
}
