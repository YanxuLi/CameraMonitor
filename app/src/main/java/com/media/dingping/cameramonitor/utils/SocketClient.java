package com.media.dingping.cameramonitor.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient {

    private final static String TAG = "SocketClient";
    /**
     * 线程池
     */
    private ExecutorService executors = Executors.newCachedThreadPool();

    /**
     * Socket读取流
     */
    private InputStream is = null;
    /**
     * Socket写入流
     */
    private OutputStream os = null;
    /**
     * 心跳周期，单位毫秒
     */
    private final static int FREQUENCY = 20000;


    private final static int TIMEOUT = 10000;
    /**
     * 控制线程运行
     */
    private boolean isRun = true;
    /**
     * 网络连接实体
     */
    private Socket mSocket;

//    /**
//     * 发送心跳
//     */
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            while (isRun) {
                try {
                    sendFrequency();
                    Thread.sleep(FREQUENCY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 连接到摄像头控制服务端
     *
     * @param serviceIp
     * @param servicePort
     * @return
     */
    public  void  connect(final String serviceIp, final int servicePort) {
        isRun = true;
        mSocket = new Socket();
        executors.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket.connect(new InetSocketAddress(serviceIp, servicePort), TIMEOUT);
                    is = mSocket.getInputStream();
                    os = mSocket.getOutputStream();
                    executors.execute(run);
                    Log.i(TAG, "摄像头控制服务连接成功!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.i(TAG, "摄像头控制服务连接失败!");
                }
            }
        });
    }

    /**
     * 每次切换摄像头需要调用
     */
    public void close() {
        try {
            isRun = false;
//            if (executors.isShutdown()) {
//                executors.shutdown();
//            }
            if (socketIsConnect()) {
                mSocket.close();
            }
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 发送心跳
     */
    private void sendFrequency() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(0x8e);
            baos.write(ByteUtil.shortToByte_c((short) 0x0000));
            baos.write(ByteUtil.shortToByte_c((short) 1));
            baos.write(0x02);
            os.write(baos.toByteArray());
            Log.i(TAG, "发送心跳包成功");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "发送心跳包失败");
        }
    }

    /**
     * 获取控制的类型
     *
     * @param direction 控制方向0-上 1-下 2-左 3-右
     * @param type      开始或者停止
     * @return
     */
    private byte[] getByte(int direction, int type) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(0x8e);
            baos.write(ByteUtil.shortToByte_c((short) 0x0001));
            baos.write(ByteUtil.shortToByte_c((short) 2));
            baos.write(direction);
            baos.write(type);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }

    /**
     * 获取控制的类型
     *
     * @param direction 控制方向0-上 1-下 2-左 3-右
     * @param type      开始或者停止
     * @return
     */
    public void ctlCameraMove(final int direction, final int type) {
        if (mSocket != null && socketIsConnect()) {
            executors.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] b = getByte(direction, type);
                    try {
                        os.write(b);
                        Log.i(TAG, "发送控制命令成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "发送控制命令失败");
                    }
                }
            });

        }

    }

    /**
     * 判断socket连接状态
     *
     * @return
     */
    private boolean socketIsConnect() {
        if (mSocket != null) {
            boolean connected = mSocket.isConnected();
            if (connected) {
                boolean closed = mSocket.isClosed();
                return !closed;
            }
            return false;
        }
        return false;
    }


    /**
     * 单例模式
     *
     * @return
     */
    public static SocketClient getInstance() {
        return ClientHolder.instance;
    }


    private static class ClientHolder {
        private static final SocketClient instance = new SocketClient();
    }

    private SocketClient() {

    }
}
