package com.media.dingping.cameramonitor.listener.api;

import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.bean.StatusBean;
import com.media.dingping.cameramonitor.bean.StreamInfo;
import com.media.dingping.cameramonitor.bean.TokenInfo;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by zsc on 2017/5/18 0018.
 */
public interface ServiceApi {

    /**
     * 登陆接口
     *
     * @param userName
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("login")
    Observable<StatusBean> login(@Field("username") String userName, @Field("password") String password);

    /**
     * 注册接口
     *
     * @param userName
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("register")
    Observable<StatusBean> register(@Field("username") String userName, @Field("password") String password);

    @FormUrlEncoded
    @POST("password")
    Observable<StatusBean> modify(@Field("username") String userName, @Field("password") String oldPassword, @Field("password") String newPassword);


    /**
     * 获取摄像头列表
     * 参数待定
     *
     * @param userName
     * @return
     */
    @FormUrlEncoded
    @POST("terminals")
    Observable<Cameras> getCameras(@Field("username") String userName);


    /**
     * 获取token和中间件地址
     *
     * @param username
     * @param caremaID
     * @return
     */
    @FormUrlEncoded
    @POST("gettoken")
    Observable<TokenInfo> getCameraToken(@Field("username") String username, @Field("CaremaID") String caremaID);


    /**
     * 得到真正的摄像头地址
     *
     * @param tokenJson
     * @return
     */
    @POST("GetMediaStream.do")
    Observable<StreamInfo> getCameraUrl(@Body RequestBody tokenJson);

//    //测试使用
//    @GET("myselect/update.txt")
//    Call<ResponseBody> getUpdateInfo();

    // 正式使用
    @GET("camareupgrade/update.txt")
    Call<ResponseBody> getUpdateInfo();

//    /**
//     * 設置是否布防
//     * @param accessToken
//     * @param deviceSerial
//     * @param isDefence
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("api/lapp/device/defence/set")
//    Observable<StatusBean> setDefence(@Field("accesstoken") String accessToken, @Field("deviceserial") String deviceSerial,@Field("isdefence") int isDefence);

    /**
     * 設置是否布防
     *
     * @param defence
     * @return
     */
    @POST("api/lapp/device/defence/set")
    Observable<StatusBean> setDefence(@Body RequestBody defence);

    /**
     * 設置是否布防時間
     *
     * @param defenceTime
     * @return
     */
    @POST("api/lapp/device/defence/plan/set")
    Observable<StatusBean> setDefenceTime(@Body RequestBody defenceTime);

    /**
     * 設置報警提醒聲音類型
     *
     * @param defencevoicetype
     * @return
     */
    @POST("api/lapp/device/alarm/sound/set")
    Observable<StatusBean> setDefenceVoiceType(@Body RequestBody defencevoicetype);
}
