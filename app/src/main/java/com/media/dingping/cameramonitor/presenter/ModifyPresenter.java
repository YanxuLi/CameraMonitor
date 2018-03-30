package com.media.dingping.cameramonitor.presenter;

import com.media.dingping.cameramonitor.bean.UserInfo;
import com.media.dingping.cameramonitor.listener.OnStatusListener;
import com.media.dingping.cameramonitor.model.CameraModel;
import com.media.dingping.cameramonitor.model.impl.ICameraModel;
import com.media.dingping.cameramonitor.presenter.impl.IModifyPresenter;
import com.media.dingping.cameramonitor.view.IModifyView;

/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class ModifyPresenter implements IModifyPresenter, OnStatusListener {
    private ICameraModel mCameraModel;
    private IModifyView mModifyView;

    public ModifyPresenter(IModifyView modifyView) {
        mCameraModel = new CameraModel();
        this.mModifyView = modifyView;
    }

    @Override
    public void selectUserInfo() {
        UserInfo usersInfo = mCameraModel.getUsersInfo();
        mModifyView.currUserName(usersInfo.getUserName());
    }

    @Override
    public void saveUserInfo(UserInfo userInfo) {
        //修改完让其重新登录，不走这里了
    }

    @Override
    public void modify(String user, String oldPassword, String newPassword) {
        mModifyView.showLoading();
        mCameraModel.modify(user, oldPassword, newPassword, this);
    }


    @Override
    public void onSuccess() {
        mModifyView.hideLoading();
        mModifyView.modifySuccess();
    }

    @Override
    public void onError() {
        mModifyView.hideLoading();
        mModifyView.modifyError();
    }

    @Override
    public void onExists() {
        //不需要用到
    }
}
