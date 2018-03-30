package com.media.dingping.cameramonitor.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.media.dingping.cameramonitor.GCApplication;
import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.bean.DefenceVoiceType;
import com.media.dingping.cameramonitor.bean.IsDefence;
import com.media.dingping.cameramonitor.bean.SetDefenceVoivePatmer;
import com.media.dingping.cameramonitor.bean.StatusBean;
import com.media.dingping.cameramonitor.bean.StreamInfo;
import com.media.dingping.cameramonitor.bean.TokenInfo;
import com.media.dingping.cameramonitor.bean.UpdateInfo;
import com.media.dingping.cameramonitor.bean.UserInfo;
import com.media.dingping.cameramonitor.listener.OnCameraArrayCallBackListener;
import com.media.dingping.cameramonitor.listener.OnCameraPlayUrlCallBackListener;
import com.media.dingping.cameramonitor.listener.OnFirstLoginListener;
import com.media.dingping.cameramonitor.listener.OnStatusListener;
import com.media.dingping.cameramonitor.listener.api.ServiceApi;
import com.media.dingping.cameramonitor.model.impl.ICameraModel;
import com.media.dingping.cameramonitor.model.impl.OnUpdateListener;
import com.media.dingping.cameramonitor.utils.RetrofitFactory;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zsc on 2017/5/19 0019.
 */
public class CameraModel implements ICameraModel {
    private SharedPreferences mPreferences;
    private final String TAG = this.getClass().getSimpleName();

    public CameraModel() {
        Log.i(TAG, "CameraModel: ----------------------");
        mPreferences = GCApplication.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
    }

    /**
     * 登陆操作
     *
     * @param user     用户名
     * @param password 密码
     * @param listener
     */
    @Override
    public void Login(String user, String password, final OnStatusListener listener) {
        ServiceApi api = RetrofitFactory.getRetrofit().create(ServiceApi.class);
        Observable<StatusBean> status = api.login(user, password);
        status.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StatusBean>() {
                    @Override
                    public void accept(StatusBean sb) throws Exception {
                        if (sb.getStatus() == 200)
                            listener.onSuccess();
                        else
                            listener.onError();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        listener.onError();
                    }
                });
    }

    /**
     * 注册操作
     *
     * @param user     用户名
     * @param password 密码
     * @param listener
     */
    @Override
    public void register(String user, String password, final OnStatusListener listener) {
        ServiceApi api = RetrofitFactory.getRetrofit().create(ServiceApi.class);
        Observable<StatusBean> status = api.register(user, password);
        status.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StatusBean>() {
            @Override
            public void accept(StatusBean sb) throws Exception {
                if (sb.getStatus() == 200)
                    listener.onSuccess();
                else if (sb.getStatus() == 400)
                    listener.onError();
                else
                    listener.onExists();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                listener.onError();
            }
        });


    }

    /**
     * 修改密码操作
     *
     * @param user        用户名
     * @param oldPassword 密码
     * @param listener
     */
    @Override
    public void modify(String user, String oldPassword, String newPassword, final OnStatusListener listener) {
        ServiceApi api = RetrofitFactory.getRetrofit().create(ServiceApi.class);
        Observable<StatusBean> modify = api.modify(user, oldPassword, newPassword);
        modify.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StatusBean>() {
            @Override
            public void accept(StatusBean statusBean) throws Exception {
                if (statusBean.getStatus() == 200) {
                    listener.onSuccess();
                } else {
                    listener.onError();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                listener.onError();
            }
        });

    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @Override
    public UserInfo getUsersInfo() {
        String username = mPreferences.getString("username", "");
        String password = mPreferences.getString("password", "");
        return new UserInfo(username, password);
    }


    /**
     * 保存用户信息
     *
     * @param info
     */
    @Override
    public void saveUserInfo(UserInfo info) {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean("currstate", true);
        edit.putString("username", info.getUserName());
        edit.putString("password", info.getPassWord());
        edit.apply();
    }

    /**
     * 获取摄像头集合
     *
     * @param userName
     * @param listener
     */
    @Override
    public void getCameraList(String userName, final OnCameraArrayCallBackListener listener) {
        ServiceApi api = RetrofitFactory.getRetrofit().create(ServiceApi.class);
        Observable<Cameras> cameras = api.getCameras(userName);
        cameras.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Cameras>() {
            @Override
            public void accept(Cameras cameras) throws Exception {
                Log.i("model", cameras.toString());
                if (cameras.getStatus() == 200)
                    listener.OnSuccess(cameras);
                else
                    listener.OnGetArrayError();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                listener.OnGetArrayError();
            }
        });
    }

    @Override
    public void getCameraPlayUrl(final TokenInfo tokenInfo, final OnCameraPlayUrlCallBackListener listner) {
        TokenInfo.DataBean data = tokenInfo.getData();
        final TokenInfo.DataBean.AddressBean address = data.getAddress();
        ServiceApi api = RetrofitFactory.getRetrofit(address.getBaseUrl()).create(ServiceApi.class);
        final String tokenJson = data.getTokenJson();
        Log.i("tokenJson", tokenJson);
        Observable<StreamInfo> cameraUrl = api.getCameraUrl(RequestBody.create(null, tokenJson));
        cameraUrl.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StreamInfo>() {
            @Override
            public void accept(StreamInfo streamInfo) throws Exception {
                Log.i(TAG, streamInfo.toString());
                if (streamInfo == null || streamInfo.getStreamPort() == 0 || streamInfo.getControlPort() == 0) {
                    listner.OnGetURLError();
                } else {
                    //得到真实的token
                    String realToken = streamInfo.getAccessToken();
//                    EZOpenSDK.getInstance().setAccessToken(realToken);
                    GCApplication.setAccessToken(realToken);
                    streamInfo.setIp(address.getIp());
                    streamInfo.setDeviceSerial(tokenInfo.getDeviceSerial());
                    streamInfo.setPlayVerifyCode(tokenInfo.getVerifyCode());
                    Log.i(TAG, streamInfo.toString());
                    getRealCameraInfo(streamInfo, listner);
//                    SocketClient.getInstance().connect(streamInfo.getIp(), streamInfo.getControlPort());

                    Log.i(TAG, "-------------------通道号为accept: realToken=" + realToken);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                listner.OnGetURLError();
                Log.i(TAG, "获取真正的token失败");
            }
        });
    }


    /**
     * 获取真实的摄像头序列号跟通道号
     *
     * @param streamInfo
     * @param listner
     */
    @Override
    public void getRealCameraInfo(final StreamInfo streamInfo, final OnCameraPlayUrlCallBackListener listner) {
        Observable.create(new ObservableOnSubscribe<StreamInfo>() {
            @Override
            public void subscribe(ObservableEmitter<StreamInfo> e) throws Exception {
                EZDeviceInfo deviceInfo = null;
                try {
                    String deviceSerial = streamInfo.getDeviceSerial();
                    Log.i(TAG, "摄像头序列号:" + deviceSerial);
//                    deviceInfo = EZOpenSDK.getInstance().getDeviceInfo(deviceSerial);
                    deviceInfo = EZOpenSDK.getInstance().getDeviceInfo(deviceSerial);
                    int isEncrypt = deviceInfo.getIsEncrypt();
                    Log.i(TAG, "isEncrypt:" + isEncrypt);
                    streamInfo.setEncrypt(isEncrypt);
                    List<EZCameraInfo> infoList = deviceInfo.getCameraInfoList();
                    boolean exist = false;
                    if (infoList != null && infoList.size() != 0) {
                        for (EZCameraInfo ezCameraInfo : infoList) {
                            if (ezCameraInfo.getDeviceSerial().equals(deviceSerial)) {
                                streamInfo.setEzCameraInfo(ezCameraInfo);
                                exist = true;
                                e.onNext(streamInfo);
                                e.onComplete();
                            }
                        }
                        if (!exist) {
                            e.onError(new Throwable("获取流地址失败"));
                        }
                    } else {
                        e.onError(new Throwable("获取流地址失败"));
                    }

                } catch (BaseException e1) {
                    e.onError(new Throwable("获取流地址失败"));
                }

            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StreamInfo>() {
            @Override
            public void accept(StreamInfo streamInfo) throws Exception {
                Log.i(TAG, streamInfo.toString());
                Log.i(TAG, "获取流地址成功");
                Log.i(TAG, streamInfo.getEzCameraInfo().getCameraNo() + "");
                Log.i(TAG, streamInfo.getEzCameraInfo().getDeviceSerial());
                listner.OnSuccess(streamInfo);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                listner.OnGetURLError();
                Log.i(TAG, "获取流地址失败");
            }
        });
    }

    @Override
    public void getCameraToken(String userName, final String cameraId, final String verifyCode, final OnCameraPlayUrlCallBackListener listner) {
        //获取摄像头地址时，先停止之前的控制连接
//        SocketClient.getInstance().close();
        ServiceApi api = RetrofitFactory.getRetrofit().create(ServiceApi.class);
        Observable<TokenInfo> cameraUrl = api.getCameraToken(userName, cameraId);
        cameraUrl.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<TokenInfo>() {
            @Override
            public void accept(TokenInfo tokenInfo) throws Exception {
                Log.i("model", tokenInfo.toString());
                if (tokenInfo.getStatus() == 200) {
                    tokenInfo.setDeviceSerial(cameraId);
                    tokenInfo.setVerifyCode(verifyCode);
                    getCameraPlayUrl(tokenInfo, listner);
                } else {
                    listner.OnGetURLError();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.i(TAG, "获取中间件地址失败");
                throwable.printStackTrace();
                listner.OnGetURLError();
            }
        });
    }


    /**
     * 摄像头控制
     *
     * @param direction
     * @param listener
     */
    @Override
    public void controlCamera(int direction, int startOrStop, OnStatusListener listener) {
//        SocketClient.getInstance().ctlCameraMove(direction, startOrStop);
    }

    @Override
    public void isFristTime(OnFirstLoginListener listener) {
        if (mPreferences.getBoolean("firsttime", true)) {
            listener.onFirst();
            saveNoFristTime();
        }
    }

    @Override
    public void saveNoFristTime() {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean("firsttime", false);
        edit.apply();
    }

    @Override
    public void close() {
//        SocketClient.getInstance().close();
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean("currstate", false);
        edit.commit();
    }

    @Override
    public boolean getLoginState() {
        return mPreferences.getBoolean("currstate", false);
    }

    @Override
    public void update(final OnUpdateListener listener) {
        ServiceApi api = RetrofitFactory.getRetrofit(GCApplication.UPDATE_BASE_URL).create(ServiceApi.class);
        final Call<ResponseBody> updateInfo = api.getUpdateInfo();
        updateInfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    InputStream is = response.body().byteStream();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    int len = 0;
                    try {
                        while ((len = is.read()) != -1) {
                            outputStream.write(len);
                            outputStream.flush();
                        }
                        String josn = outputStream.toString("utf-8");
                        UpdateInfo info = new Gson().fromJson(josn, UpdateInfo.class);
                        if (info != null && !info.getVersion().equals("") && !info.getUrl().equals("")) {
                            Log.i(TAG, "服务器版本信息---" + info.toString());
                            int versionCode = getPackageInfo(GCApplication.getContext()).versionCode;
                            String versionName = getPackageInfo(GCApplication.getContext()).versionName;
                            Log.i(TAG, "目前版本信息---" + "versionCode:" + versionCode + "--" + "versionName:" + versionName);
                            String version = info.getVersion();
                            //判断1.0.10623 与服务器上的1.0.10627
                            int nativeVersion = 0;
                            int serverVersion = 0;
                            try {
                                nativeVersion = Integer.parseInt(versionName.replace(".", ""));
                                serverVersion = Integer.parseInt(version.replace(".", ""));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Log.i(TAG, "服务器版本设置有误，导致无法升级");
                            }
                            if (serverVersion > nativeVersion) {
                                listener.onUpdateUrl(info);
                            } else {
                                Log.i(TAG, "版本相同或者更低不更新");
                            }
                        } else {
                            Log.i(TAG, "服务器升级信息有误，不升级");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "error");
            }
        });
    }

    @Override
    public void setDefence(IsDefence isDefence) {
        Log.i(TAG, "setDefence: --------------------");
        ServiceApi api = RetrofitFactory.getRetrofit("https://open.ys7.com/").create(ServiceApi.class);
        final String json = new Gson().toJson(isDefence);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        final String tokenJson = "accessToken=" + isDefence.getAccessToken() + "&deviceSerial=" + isDefence.getDeviceSerial() + "&isDefence=" + isDefence.getIsDefence();
        Log.i("tokenJson", "---------------------" + json);
        Observable<StatusBean> defenceObser = api.setDefence(body);
        defenceObser.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StatusBean>() {
            @Override
            public void accept(StatusBean statusBean) throws Exception {

                Log.i(TAG, "accept: ----------" + statusBean.toString());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    @Override
    public void setDefenceTime(SetDefenceVoivePatmer patmer) {
        ServiceApi api = RetrofitFactory.getRetrofit("https://open.ys7.com/").create(ServiceApi.class);

    }

    @Override
    public void setDefenceVoiceType(DefenceVoiceType type) {
        ServiceApi api = RetrofitFactory.getRetrofit("https://open.ys7.com/").create(ServiceApi.class);
        final String json = new Gson().toJson(type);
        final String tokenJson = "accessToken=" + type.getAccessToken() + "&deviceSerial=" + type.getDeviceSerial() + "&isDefence=" + type.getDefenceVoiceType();
        Log.i("tokenJson", "-----------------------------------" + json);
        Observable<StatusBean> defenceObser = api.setDefenceVoiceType(RequestBody.create(null, tokenJson));
        defenceObser.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StatusBean>() {
            @Override
            public void accept(StatusBean statusBean) throws Exception {

                Log.i(TAG, "accept: ----------" + statusBean.toString());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });


    }


    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

}
