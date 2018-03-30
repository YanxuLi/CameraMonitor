package com.media.dingping.cameramonitor.view;

import com.media.dingping.cameramonitor.customview.StateButton;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public interface IModifyView extends IBaseView {


    /**
     * 修改密码成功
     */
    void modifySuccess();

    /**
     * 修改密码失败
     */
    void modifyError();

    void currUserName(String userName);

    String getNewPassword();
    String getNewPassword2();
}
