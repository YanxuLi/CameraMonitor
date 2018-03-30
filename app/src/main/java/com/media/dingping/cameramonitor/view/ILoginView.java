package com.media.dingping.cameramonitor.view;

import com.media.dingping.cameramonitor.bean.UserInfo;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public interface ILoginView extends IBaseView{

    /**
     * 展示最近的登陆账号
     * @param info
     */
    void showLastRecordUser(UserInfo info);


    /**
     * 登陆成功
     */
    void loginSuccess();

    /**
     * 登陆失败
     */
    void loginError();


    /**
     * 保存最近的用户
     * @param
     */
    void saveLastUserInfo();


}
