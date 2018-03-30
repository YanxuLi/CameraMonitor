package com.media.dingping.cameramonitor.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/9/21 0021.
 */

public class NetWorkBroadcastListenner extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gprs = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
            if (gprs.isConnected()){
                Toast.makeText(context, "您当前正在使用接数据流量...", Toast.LENGTH_SHORT).show();
            }
            if (wifi.isConnected()){
//                Toast.makeText(context, "正在连接wifi。。。。。。。", Toast.LENGTH_SHORT).show();
            }
            if (!gprs.isConnected() && !wifi.isConnected()){
//                Toast.makeText(context, "断网啦...", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
