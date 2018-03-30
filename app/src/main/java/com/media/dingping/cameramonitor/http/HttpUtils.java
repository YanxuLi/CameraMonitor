package com.media.dingping.cameramonitor.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lu on 2016/8/3.
 */
public class HttpUtils {
    public static String TAG="HttpUtils";
    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;

    /*
     * params 填写的URL的参数 encode 字节编码
     */
    public static String sendPostMessage(String url, String params,
                                              String encode) {
        if (params != null && !params.isEmpty()) {
            try {
                Log.d("HttpUtils","url = "+url+"\nparams = "+params);
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(
                        url).openConnection();
                httpURLConnection.setConnectTimeout(120000);
                httpURLConnection.setDoInput(true);// 从服务器获取数据
                httpURLConnection.setDoOutput(true);// 向服务器写入数据
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                // byte[] mydata = URLEncoder.encode(params, encode).getBytes();
                byte[] mydata = params.getBytes(encode);
                httpURLConnection.setRequestProperty("Charset", encode);
                // 获得上传信息的字节大小及长度
                httpURLConnection.setRequestProperty("Content-Lenth",
                        String.valueOf(mydata.length));
                // 设置请求体的类型
                httpURLConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                // 获得输出流，向服务器输出数据
                OutputStream outputStream = (OutputStream) httpURLConnection
                        .getOutputStream();
                if (outputStream!=null) {
                    outputStream.write(mydata);
                }
                // 获得服务器响应的结果和状态码
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
//                    Log.i(TAG, "sendPostMessage: -----------------------连接成功");
                    // 获得输入流，从服务器端获得数据
                    InputStream inputStream = (InputStream) httpURLConnection
                            .getInputStream();
                    // return (changeInputStream(inputStream, encode));
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer sb = new StringBuffer();
                    String str;
                    try {
                        while ((str = br.readLine()) != null) {
                            sb.append(str);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, " ------------------设置返回信息=" + sb.toString());
                    return String.valueOf(sb);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    * params 填写的URL的参数 encode 字节编码
    */
    public static String getPostMessage(String url, String params,
                                          String encode) {
        if (params != null && !params.isEmpty()) {
            try {
                Log.d("HttpUtils","url = "+url+"\nparams = "+params);
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(
                        url).openConnection();
                httpURLConnection.setConnectTimeout(120000);
                httpURLConnection.setDoInput(true);// 从服务器获取数据
                httpURLConnection.setDoOutput(true);// 向服务器写入数据
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                // byte[] mydata = URLEncoder.encode(params, encode).getBytes();
                byte[] mydata = params.getBytes(encode);
                httpURLConnection.setRequestProperty("Charset", encode);
                // 获得上传信息的字节大小及长度
                httpURLConnection.setRequestProperty("Content-Lenth",
                        String.valueOf(mydata.length));
                // 设置请求体的类型
                httpURLConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                // 获得输出流，向服务器输出数据
                OutputStream outputStream = (OutputStream) httpURLConnection
                        .getOutputStream();
                if (outputStream!=null) {
                    outputStream.write(mydata);
                }
                // 获得服务器响应的结果和状态码
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
//                    Log.i(TAG, "sendPostMessage: -----------------------连接成功");
                    // 获得输入流，从服务器端获得数据
                    InputStream inputStream = (InputStream) httpURLConnection
                            .getInputStream();
                    // return (changeInputStream(inputStream, encode));
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer sb = new StringBuffer();
                    String str;
                    try {
                        while ((str = br.readLine()) != null) {
                            sb.append(str);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, " ------------------设置返回信息=" + sb.toString());
                    return String.valueOf(sb);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
