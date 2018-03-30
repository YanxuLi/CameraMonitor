package com.media.dingping.cameramonitor.presenter;

import com.media.dingping.cameramonitor.bean.UserInfo;
import com.media.dingping.cameramonitor.listener.OnStatusListener;
import com.media.dingping.cameramonitor.model.CameraModel;
import com.media.dingping.cameramonitor.model.impl.ICameraModel;
import com.media.dingping.cameramonitor.presenter.impl.IRegisterPresenter;
import com.media.dingping.cameramonitor.view.IRegisterView;

/**
 * Created by Administrator on 2017/5/22 0022.
 */
public class RegisterPresenter implements IRegisterPresenter,OnStatusListener {
    private IRegisterView mRegisterView;
    private ICameraModel mCameraModel;

    public RegisterPresenter(IRegisterView mRegisterView) {
        this.mRegisterView = mRegisterView;
        mCameraModel = new CameraModel();
    }

    @Override
    public void register(String user, String password) {
        mRegisterView.showLoading();
        mCameraModel.register(user, password, this);
    }

    @Deprecated
    public void selectUserInfo() {
        //注册无需查询
    }

    @Override
    public void saveUserInfo(UserInfo userInfo) {
        mCameraModel.saveUserInfo(userInfo);
    }

    @Override
    public void onSuccess() {
        mRegisterView.hideLoading();
        mRegisterView.registerSuccess();
    }

    @Override
    public void onError() {
        mRegisterView.hideLoading();
        mRegisterView.registerError();
    }

    @Override
    public void onExists() {
        mRegisterView.hideLoading();
        mRegisterView.onExists();
    }
}
