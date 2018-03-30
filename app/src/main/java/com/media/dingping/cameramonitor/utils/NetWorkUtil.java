package com.media.dingping.cameramonitor.utils;

/**
 * Created by lu on 2016/10/10.
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.text.DecimalFormat;

public class NetWorkUtil {

    public static boolean isWifiAvailable(Context context) {

//        ConnectivityManager connectivityManager = (ConnectivityManager) ConfigManager
//        .getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected() && networkInfo
        .getType() == ConnectivityManager.TYPE_WIFI);
        }

            /**
        * 网络是否可用
        *
        * @param context
        * @return
        */
    public static boolean IsNetWorkEnable(Context context) {
        try {

            ConnectivityManager connectivity = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivity == null) {
                Toast.makeText(context,"无法连接网络", Toast.LENGTH_SHORT).show();
                return false;
                }
            NetworkInfo info = connectivity.getActiveNetworkInfo();

            if (info != null && info.isConnected()) {
                // 判断当前网络是否已经连接
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    Toast.makeText(context,"已连接网络", Toast.LENGTH_SHORT).show();
                    return true;
                    }
               }
            } catch (Exception e) {
                e.printStackTrace();
            }
                Toast.makeText(context,"无法连接网络", Toast.LENGTH_SHORT).show();
        return false;
        }

    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    // private static final int NETWORK_TYPE_MOBILE = -100;
            private static final int NETWORK_TYPE_WIFI = -101;
            private static final int NETWORK_CLASS_WIFI = -101;
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;
    /** Unknown network class. */
            private static final int NETWORK_CLASS_UNKNOWN = 0;
    /** Class of broadly defined "2G" networks. */
            private static final int NETWORK_CLASS_2_G = 1;
    /** Class of broadly defined "3G" networks. */
            private static final int NETWORK_CLASS_3_G = 2;
    /** Class of broadly defined "4G" networks. */
            private static final int NETWORK_CLASS_4_G = 3;
            private static DecimalFormat df = new DecimalFormat("#.##");
            // 适配低版本手机
            /** Network type is unknown */
            public static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
            public static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
            public static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
            public static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B */
            public static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0 */
            public static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A */
            public static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT */
            public static final int NETWORK_TYPE_1xRTT = 7;
    /** Current network is HSDPA */
            public static final int NETWORK_TYPE_HSDPA = 8;
    /** Current network is HSUPA */
            public static final int NETWORK_TYPE_HSUPA = 9;
    /** Current network is HSPA */
            public static final int NETWORK_TYPE_HSPA = 10;
    /** Current network is iDen */
            public static final int NETWORK_TYPE_IDEN = 11;
    /** Current network is EVDO revision B */
            public static final int NETWORK_TYPE_EVDO_B = 12;
    /** Current network is LTE */
            public static final int NETWORK_TYPE_LTE = 13;
    /** Current network is eHRPD */
            public static final int NETWORK_TYPE_EHRPD = 14;
    /** Current network is HSPA+ */
            public static final int NETWORK_TYPE_HSPAP = 15;
            /**
        * 格式化大小
        *
        * @param size
        * @return
        */

    public static String formatSize(long size) {
        String unit = "B";
        float len = size;
        if (len > 900) {
            len /= 1024f;
            unit = "KB";
            }
        if (len > 900) {
            len /= 1024f;
            unit = "MB";
            }
        if (len > 900) {
            len /= 1024f;
            unit = "GB";
            }
        if (len > 900) {
            len /= 1024f;
            unit = "TB";
            }
        return df.format(len) + unit;
        }

    public static String formatSizeBySecond(long size) {
        String unit = "B";
        float len = size;
        if (len > 900) {
            len /= 1024f;
            unit = "KB";
           }
        if (len > 900) {
            len /= 1024f;
            unit = "MB";
            }
        if (len > 900) {
            len /= 1024f;
            unit = "GB";
            }
        if (len > 900) {
            len /= 1024f;
            unit = "TB";
            }
        return df.format(len) + unit + "/s";
        }

    public static String format(long size) {
        String unit = "B";
        float len = size;
        if (len > 1000) {
            len /= 1024f;
            unit = "KB";
            if (len > 1000) {
                len /= 1024f;
                unit = "MB";
                if (len > 1000) {
                    len /= 1024f;
                    unit = "GB";
                    }
                }
            }
        return df.format(len) + "\n" + unit + "/s";
        }
            /**
        * 获取运营商
        *
        * @return
        */
    public static String getProvider(Context context) {
        String provider = "未知";
        try {
//           TelephonyManager telephonyManager = (TelephonyManager) ConfigManager
//            .getContext().getSystemService(Context.TELEPHONY_SERVICE);
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String IMSI = telephonyManager.getSubscriberId();
            Log.v("tag", "getProvider.IMSI:" + IMSI);
            if (IMSI == null) {
                if (TelephonyManager.SIM_STATE_READY == telephonyManager
                .getSimState()) {
                    String operator = telephonyManager.getSimOperator();
                    Log.v("tag", "getProvider.operator:" + operator);
                    if (operator != null) {
                        if (operator.equals("46000")
                        || operator.equals("46002")
                        || operator.equals("46007")) {
                            provider = "中国移动";
                            } else if (operator.equals("46001")) {
                            provider = "中国联通";
                            } else if (operator.equals("46003")) {
                            provider = "中国电信";
                            }
                        }
                    }
                } else {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002")
                || IMSI.startsWith("46007")) {
                    provider = "中国移动";
                    } else if (IMSI.startsWith("46001")) {
                    provider = "中国联通";
                    } else if (IMSI.startsWith("46003")) {
                    provider = "中国电信";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return provider;
            }
            /**
        * 获取网络类型
        *
        * @return
        */

     public static String getCurrentNetworkType(Context context) {
//         if (!IsNetWorkEnable(context))
//             return "网络无连接";
        int networkClass = getNetworkClass(context);
        String type = "未知";
        switch (networkClass) {
            case NETWORK_CLASS_UNAVAILABLE:
                type = "无";
                break;
            case NETWORK_CLASS_WIFI:
                type = "Wi-Fi";
               break;
            case NETWORK_CLASS_2_G:
                type = "2G";
                break;
            case NETWORK_CLASS_3_G:
                type = "3G";
                break;
            case NETWORK_CLASS_4_G:
                type = "4G";
                break;
            case NETWORK_CLASS_UNKNOWN:
                type = "有线网络";
                break;
            }
        return type;
        }

    private static int getNetworkClassByType(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_UNAVAILABLE:
                return NETWORK_CLASS_UNAVAILABLE;
            case NETWORK_TYPE_WIFI:
                return NETWORK_CLASS_WIFI;
            case NETWORK_TYPE_GPRS:
                case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
                case NETWORK_TYPE_1xRTT:
                case NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
                case NETWORK_TYPE_EVDO_0:
                case NETWORK_TYPE_EVDO_A:
                case NETWORK_TYPE_HSDPA:
                case NETWORK_TYPE_HSUPA:
                case NETWORK_TYPE_HSPA:
                case NETWORK_TYPE_EVDO_B:
                case NETWORK_TYPE_EHRPD:
                case NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
            }
        }

    private static int getNetworkClass(Context context) {
        int networkType = NETWORK_TYPE_UNKNOWN;
        try {
//            final NetworkInfo network = ((ConnectivityManager) ConfigManager
//            .getContext()
//            .getSystemService(Context.CONNECTIVITY_SERVICE))
//            .getActiveNetworkInfo();
            final NetworkInfo network = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
            && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI;
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
//                    TelephonyManager telephonyManager = (TelephonyManager) ConfigManager
//                    .getContext().getSystemService(
//                                             Context.TELEPHONY_SERVICE);
                    TelephonyManager telephonyManager = (TelephonyManager) context
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    networkType = telephonyManager.getNetworkType();
                    }
                } else {
                networkType = NETWORK_TYPE_UNAVAILABLE;
                }

            } catch (Exception ex) {
            ex.printStackTrace();
            }
        return getNetworkClassByType(networkType);

        }

    public static String getWifiRssi(Context context) {
        int asu = 85;
        try {
//            final NetworkInfo network = ((ConnectivityManager) ConfigManager
//            .getContext()
//            .getSystemService(Context.CONNECTIVITY_SERVICE))
//            .getActiveNetworkInfo();
            final NetworkInfo network = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
            && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
//                    WifiManager wifiManager = (WifiManager) ConfigManager
//                    .getContext()
//                    .getSystemService(Context.WIFI_SERVICE);
                    WifiManager wifiManager = (WifiManager) context
                            .getSystemService(Context.WIFI_SERVICE);

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        asu = wifiInfo.getRssi();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return asu + "dBm";
        }

    public static String getWifiSsid(Context context) {
        String ssid = "";
        try {
//            final NetworkInfo network = ((ConnectivityManager) ConfigManager
//            .getContext()
//            .getSystemService(Context.CONNECTIVITY_SERVICE))
//            .getActiveNetworkInfo();
            final NetworkInfo network = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
            && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
//                    WifiManager wifiManager = (WifiManager) ConfigManager
//                    .getContext()
//                    .getSystemService(Context.WIFI_SERVICE);
                    WifiManager wifiManager = (WifiManager) context
                            .getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        ssid = wifiInfo.getSSID();
                        if (ssid == null) {
                            ssid = "";
                            }
                        ssid = ssid.replaceAll("\"", "");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return ssid;
        }
/**
* 检查sim卡状态
        *
        * @param ctx
        * @return
        */
    public static boolean checkSimState(Context context) {
//        TelephonyManager tm = (TelephonyManager) ConfigManager.getContext()
//        .getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT
        || tm.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN) {
            return false;
            }
        return true;
        }
}
