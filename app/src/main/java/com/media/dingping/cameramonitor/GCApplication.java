package com.media.dingping.cameramonitor;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.dianpingmedia.camerapush.PushManager;
import com.ezvizuikit.open.EZUIKit;
import com.media.dingping.cameramonitor.utils.ApiConfig;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.util.LocalInfo;

/**
 * Created by zsc on 2017/1/21 0021.
 */
public class GCApplication  {

    private static Application instance;

    private static SharedPreferences serviceInfo;
    private static GCApplication app;

    private SharedPreferences mPreferences;
    public static boolean isOpenDraw = true;

//    public static final String UPDATE_BASE_URL = "http://59.110.52.164/";
    //172.16.13.204 lbf本地测试
    public static final String UPDATE_BASE_URL = "http://172.16.13.204";
//    public static final String UPDATE_TEST_BASE_URL = "http://192.168.0.120:8080/";
    public static final String UPDATE_TEST_BASE_URL = "http://172.16.13.204:8080/";//测试
    private String mLastUsername;
    private String mLastPassword;

    public static void init(Application application){
        app = new GCApplication(application);
    }

    public GCApplication(Application application) {
        instance = application;
        onCreate();
    }

    public static Context getContext() {
        return instance;
    }


    public static EZOpenSDK getOpenSDK() {
        return EZOpenSDK.getInstance();
    }

    private static String AppKey = "876d4258b31648b4bbd6d407771f189a";//国彩key
//    private static String pushSecret = "5e712d50-4c26-4b18-a37a-e6d6461c411d";

//    final static String	appSecret= "1b4d9353538b4c4e7e0040e9a8e28141";//方
//    private static String AppKey = "ef1fb2cfb45340da90f32d3af1a952ad";
//    private static String pushSecret = "cd3c00a4-3be9-45f6-954d-f41eeff3c806";

    /**
     * 测试用的
     */
    private static String RealToken = "at.7j2uzyd78d1c36te7jfvfm257zh14i3h-3d1tsfmwad-0ne0e0c-exdkw9gjz";

    //测试用
    public static String mDeviceSerial = "705984190";

    public static String getServerIp() {
        // return serviceInfo.getString("serverIp", "47.93.83.64");
        return serviceInfo.getString("serverIp", "172.16.13.204");//http://192.168.91.1:8080 lbf本地测试
    }

    public static int getServerPort() {
        //return serviceInfo.getInt("serverPort", 8091);
        return serviceInfo.getInt("serverPort", 8080);//中间件更改
    }

    public static int getPushServerPort() {
//        return serviceInfo.getInt("push_serverPort", 8833);
        return serviceInfo.getInt("push_serverPort", 8733);
    }
    public static String getPushServerIp() {
        // return serviceInfo.getString("push_serverIp", "192.168.1.249");
        return serviceInfo.getString("push_serverIp", "172.16.16.233");
    }

    public void onCreate() {
        //设置服务器ip跟端口

        // 47.93.83.64:8091
        serviceInfo = instance.getSharedPreferences("serviceInfo", Context.MODE_PRIVATE);
//        ApiConfig.init(getServerIp(), getServerPort());
//        ApiConfig.init("47.93.83.64", 8091);
        ApiConfig.init("172.16.13.204", 8080);
        EZOpenSDK.showSDKLog(true);
        EZOpenSDK.enableP2P(true);
        EZOpenSDK.initLib(instance, AppKey, "");
        EZUIKit.initWithAppKey(instance, AppKey);
        EZUIKit.setDebug(true);
        LocalInfo.init( instance);
        mPreferences = GCApplication.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        mLastUsername = mPreferences.getString("username", "");
        mLastPassword = mPreferences.getString("password", "");

        starServer();
    }

    private void starServer() {
//        PushManager.init("192.168.1.12", 8833);//推送服务
//        PushManager.init("47.93.83.64", 8833);//推送服务
        PushManager.init("172.16.16.233", 8733);//推送服务 代理服务
        PushServer.setUser(mLastUsername,mLastPassword);
        instance.startService(new Intent(instance, PushServer.class));  //开启推送服务
    }

    public static void setAccessToken(String accessToken) {
        EZOpenSDK.getInstance().setAccessToken(accessToken);
        EZUIKit.setAccessToken(accessToken);
    }

    public static void saveServerInfo(String serverIp, int serverPort,String push_serverIp,int push_serverPort) {
        SharedPreferences.Editor edit = serviceInfo.edit();
        edit.putString("serverIp", serverIp);
        edit.putInt("serverPort", serverPort);
        edit.putString("push_serverIp",push_serverIp);
        edit.putInt("push_serverPort",push_serverPort);
        edit.apply();
        ApiConfig.init(serverIp, serverPort);
        PushManager.init(push_serverIp, push_serverPort);
    }


}
