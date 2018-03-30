package com.media.dingping.cameramonitor.utils;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zsc on 2017/5/21 0022.
 */
public class RetrofitFactory {
    /**
     * 传入相对应的base地址
     * @param
     * @return
     */
    public static Retrofit getRetrofit(){
        return new Retrofit.Builder()
                //添加Gson解析
                .addConverterFactory(GsonConverterFactory.create())
                //添加Rxjave
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //设置baseUrl

                .baseUrl(ApiConfig.BASE_URL)
                //创建
                .build();
    }

    public static Retrofit getRetrofit(String baseUrl){
        return new Retrofit.Builder()
                //添加Gson解析
                .addConverterFactory(GsonConverterFactory.create())
                //添加Rxjave
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //设置baseUrl
                //创建
                .baseUrl(baseUrl)
                .build();
    }

}
