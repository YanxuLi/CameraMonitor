package com.media.dingping.cameramonitor.presenter;

import com.media.dingping.cameramonitor.bean.UserInfo;
import com.media.dingping.cameramonitor.listener.OnStatusListener;
import com.media.dingping.cameramonitor.model.CameraModel;
import com.media.dingping.cameramonitor.model.impl.ICameraModel;
import com.media.dingping.cameramonitor.presenter.impl.ILoginPresenter;
import com.media.dingping.cameramonitor.view.ILoginView;

/**
 * Created by zsc on 2017/5/19 0019.
 */
public class LoginPresenter implements ILoginPresenter,OnStatusListener{
    private ILoginView mLoginView;
    private ICameraModel mCameraModel;


    public LoginPresenter(ILoginView mLoginView) {
        this.mLoginView = mLoginView;
        this.mCameraModel = new CameraModel();
    }

    @Override
    public void login(String user, String password) {
        mLoginView.showLoading();
        mCameraModel.Login(user,password,this);
    }

    @Override
    public boolean getLoginState() {
        return mCameraModel.getLoginState();
    }

    @Override
    public void selectUserInfo() {
        UserInfo usersInfo = mCameraModel.getUsersInfo();
        mLoginView.showLastRecordUser(usersInfo);
    }


    @Override
    public void saveUserInfo(UserInfo userInfo) {
        mCameraModel.saveUserInfo(userInfo);
    }


    @Override
    public void onSuccess() {
        mLoginView.hideLoading();
        mLoginView.loginSuccess();
    }

    @Override
    public void onError() {
        mLoginView.hideLoading();
        mLoginView.loginError();
    }


    @Deprecated
    public void onExists() {
        //登陆不需要此接口
    }
}
