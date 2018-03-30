package com.media.dingping.cameramonitor.playback;

import android.os.Handler;

import com.bumptech.glide.load.engine.executor.FifoPriorityThreadPoolExecutor;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZDeviceRecordFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Administrator on 2017/8/30 0030.
 */

public class PlayBackUtils {
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void loadEZDeviceRecordFiles(final String deviceSerial, final int cameraNo, final Date queryDate, final Handler handler, final OnEZDeviceRecordFileCallBack2 onEZCloudRecordFileCallBack) {
        onEZCloudRecordFileCallBack.onLoadStart(queryDate);
        //拿录像前判断是否在线
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int status = EZOpenSDK.getInstance().getDeviceInfo(deviceSerial).getStatus();
                    if (status == 2) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onEZCloudRecordFileCallBack.onOffline();
                            }
                        });
                        return;
                    }
                } catch (BaseException e) {

                }
                final Calendar startTime = Calendar.getInstance();
                final Calendar endTime = Calendar.getInstance();
                startTime.setTime(queryDate);
                endTime.setTime(queryDate);
                startTime.set(Calendar.HOUR_OF_DAY, 0);
                startTime.set(Calendar.MINUTE, 0);
                startTime.set(Calendar.SECOND, 0);
                endTime.set(Calendar.HOUR_OF_DAY, 23);
                endTime.set(Calendar.MINUTE, 59);
                endTime.set(Calendar.SECOND, 59);
                try {
                    final List<EZDeviceRecordFile> ezDeviceRecordFiles = EZOpenSDK.getInstance().searchRecordFileFromDevice(deviceSerial, cameraNo, startTime, endTime);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (ezDeviceRecordFiles != null && ezDeviceRecordFiles.size() > 0) {
                                onEZCloudRecordFileCallBack.onHasData(ezDeviceRecordFiles,queryDate);
                            } else {
                                onEZCloudRecordFileCallBack.onNoData(queryDate);
                            }
                        }
                    });
                } catch (BaseException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onEZCloudRecordFileCallBack.onNoData(queryDate);
                        }
                    });

                }
            }
        });

    }

    public static void loadEZDeviceRecordFiles(final String deviceSerial, final int cameraNo, final Calendar startTime, final Calendar endTime,
                                               final Handler handler, final OnEZDeviceRecordFileCallBack onEZCloudRecordFileCallBack) {
        onEZCloudRecordFileCallBack.onLoadStart();
        //拿录像前判断是否在线
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int status = EZOpenSDK.getInstance().getDeviceInfo(deviceSerial).getStatus();
                    if (status == 2) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onEZCloudRecordFileCallBack.onOffline();
                            }
                        });
                        return;
                    }
                } catch (BaseException e) {

                }
//                final Calendar startTime = Calendar.getInstance();
//                final Calendar endTime = Calendar.getInstance();
//                startTime.setTime(queryDate);
//                endTime.setTime(queryDate);
//                startTime.set(Calendar.HOUR_OF_DAY, 0);
//                startTime.set(Calendar.MINUTE, 0);
//                startTime.set(Calendar.SECOND, 0);
//                endTime.set(Calendar.HOUR_OF_DAY, 23);
//                endTime.set(Calendar.MINUTE, 59);
//                endTime.set(Calendar.SECOND, 59);
                try {
                    final List<EZDeviceRecordFile> ezDeviceRecordFiles = EZOpenSDK.getInstance().searchRecordFileFromDevice(deviceSerial, cameraNo, startTime, endTime);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (ezDeviceRecordFiles != null && ezDeviceRecordFiles.size() > 0) {
                                onEZCloudRecordFileCallBack.onHasData(ezDeviceRecordFiles);
                            } else {
                                onEZCloudRecordFileCallBack.onNoData();
                            }
                        }
                    });
                } catch (BaseException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onEZCloudRecordFileCallBack.onNoData();
                        }
                    });

                }
            }
        });

    }

    public interface OnEZDeviceRecordFileCallBack {
        void onHasData(List<EZDeviceRecordFile> files);

        void onLoadStart();

        void onNoData();

        void onOffline();
    }

    public interface OnEZDeviceRecordFileCallBack2 {
        void onHasData(List<EZDeviceRecordFile> files, Date querDate);

        void onLoadStart(Date querDate);

        void onNoData(Date querDate);

        void onOffline();
    }

    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    public static Calendar getStartTime(String date) {
        Calendar startTime = Calendar.getInstance();
        try {
            startTime.setTime(sdf2.parse(date));
        } catch (ParseException e) {

        }
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        return startTime;
    }

    public static Calendar getEndTime(String date) {
        Calendar endTime = Calendar.getInstance();
        try {
            endTime.setTime(sdf2.parse(date));
        } catch (ParseException e) {

        }
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        return endTime;
    }
}
