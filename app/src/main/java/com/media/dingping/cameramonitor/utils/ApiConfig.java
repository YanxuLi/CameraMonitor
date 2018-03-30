package com.media.dingping.cameramonitor.utils;

/**
 * Created by zsc on 2017/5/21 0021.
 */
public final class ApiConfig {

    public static void init(String ip, int port) {
        BASE_URL = "http://" + ip + ":" + port + "/user/";
    }
    public static String BASE_URL = "";
}
