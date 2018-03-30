package com.media.dingping.cameramonitor.test;

import android.app.Application;

import com.ezvizuikit.open.EZUIKit;

/**
 * Created by Administrator on 2017/7/27 0027.
 */

public class TestApplocation extends Application {
    private String myAppKey = "47791e2719e0438683620743507e8aba";
    private String token = "at.9um85u4c38ixukc5a2ehqs126u0ttwjc-7hxh2gu1ys-16qxp8x-4752k7s88";

    @Override
    public void onCreate() {
        super.onCreate();
        EZUIKit.initWithAppKey(this, myAppKey);
        EZUIKit.setAccessToken(token);
        EZUIKit.setDebug(true);
    }
}
