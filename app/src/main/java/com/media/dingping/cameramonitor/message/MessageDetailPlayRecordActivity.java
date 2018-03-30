package com.media.dingping.cameramonitor.message;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.R2;
import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.customview.loading.LoadingView;
import com.media.dingping.cameramonitor.playback.PlayBackUtils;
import com.media.dingping.cameramonitor.playback.RemoteListContant;
import com.videogo.constant.IntentConsts;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZAlarmInfo;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.openapi.bean.EZDeviceRecordFile;
import com.videogo.remoteplayback.RemoteFileInfo;
import com.videogo.util.LocalInfo;
import com.videogo.widget.CheckTextButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/23 0023.
 * 报警消息详情回放
 */

public class MessageDetailPlayRecordActivity extends Activity implements PlayBackUtils.OnEZDeviceRecordFileCallBack,
        View.OnTouchListener{
    @BindView(R2.id.remoteplayback_play_rl)
    RelativeLayout mRelativeLayout;
    @BindView(R2.id.record_control)
    LinearLayout recordControl;
    @BindView(R2.id.tip_tv)
    TextView tip_tv;
    @BindView(R2.id.remoteplayback_sv)
    SurfaceView mSurfaceView;
    @BindView(R2.id.loading)
    LoadingView loading;
    @BindView(R2.id.remoteplayback_replay_btn)
    ImageButton remoteplaybackReplayBtn;
    @BindView(R2.id.remoteplayback_sound_btn)
    ImageButton remoteplaybackSoundBtn;
    @BindView(R2.id.remoteplayback_play_btn)
    ImageButton remoteplaybackPlayBtn;
    @BindView(R2.id.fullscreen_button)
    CheckTextButton fullscreenButton;
    @BindView(R2.id.record_back)
    ImageButton recordBack;
    @BindView(R2.id.recordactivity_back)
    ImageView recordactivityBack;
    @BindView(R2.id.curr_camera)
    TextView currCamera;
    @BindView(R2.id.recordactivity_header)
    RelativeLayout recordactivityHeader;

    @BindView(R2.id.message_playback_begin_time_tv)
    TextView messagePlaybackBeginTimeTv;
    @BindView(R2.id.message_playback_progress_seekbar)
    SeekBar messagePlaybackProgressSeekbar;
    @BindView(R2.id.message_playback_end_time_tv)
    TextView messagePlaybackEndTimeTv;
    @BindView(R2.id.message_playback_progress_ly)
    LinearLayout messagePlaybackProgressLy;
    @BindView(R2.id.message_playback_progressbar)
    ProgressBar messagePlaybackProgressbar;

    private final String TAG = MessageDetailPlayRecordActivity.this.getClass().getSimpleName();

    private Timer mUpdateTimer = null;
    private TimerTask mUpdateTimerTask = null;
    private int recordTime = 0;//已播放的秒数
    public static int ALARM_MAX_DURATION = 45;

    /**
     * 屏幕当前方向
     */
    private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private RelativeLayout.LayoutParams mLayoutParams1;
    private RelativeLayout.LayoutParams mLayoutParams;
    private int mWidth;
    private int mHeight;
    private int mWidth1;
    private int mHeight1;
    private LocalInfo mLocalInfo = null;

    private Date mDate=null;
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();
    private Calendar dispTime=Calendar.getInstance();
    private String timeStr;
    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    private final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    private Cameras.DatasBean mCameraInfo;
    private EZAlarmInfo mEZAlarmInfo;
    private int mCameraNo = -1;
    private boolean mIsShound = false;
    private boolean isPlaying = false;
    private boolean mActivityStoped = false;
    private EZPlayer mEZPlayer;
    private List<EZDeviceRecordFile> mEZDeviceRecordFiles = null;

    private PowerManager.WakeLock m_wklk;
    private boolean lockWake=false;

    private SurfaceHolder mHolder;
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mHolder = holder;
            if (mEZDeviceRecordFiles != null) {
                mEZPlayer.setHandler(mHandler);
                mEZPlayer.setSurfaceHold(mHolder);
                mEZPlayer.setPlayVerifyCode(mCameraInfo.getPlayVerifyCode());
                mEZPlayer.startPlayback(mEZDeviceRecordFiles.get(0));
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            Log.i(TAG, "mHanderCode:" + what);
            switch (msg.what) {
                // 片段播放完毕
                // 380061即开始时间>=结束时间，播放完成
                case ErrorCode.ERROR_CAS_RECORD_SEARCH_START_TIME_ERROR:
                    Log.d(TAG, "ERROR_CAS_RECORD_SEARCH_START_TIME_ERROR");
                    stopPlay();
                    break;
                case EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_PLAY_FINISH:
                    Log.d(TAG, "MSG_REMOTEPLAYBACK_PLAY_FINISH");
                    remoteplaybackReplayBtn.setVisibility(View.VISIBLE);
                    stopTimer();
                    stopPlay();
                    break;
                // 画面显示第一帧
                case EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_PLAY_SUCCUSS:
                    if (mIsShound) {
                        mEZPlayer.openSound();
                    }
                    remoteplaybackPlayBtn.setSelected(true);
                    loading.setVisibility(View.GONE);
                    isPlaying = true;
                    m_wklk.acquire(); //设置保持唤醒
                    remoteplaybackReplayBtn.setVisibility(View.GONE);
                    if (recordTime>=45)
                    recordTime=0;
                    setTimer();
                    break;

                // 处理播放链接异常
                case RemoteListContant.MSG_REMOTELIST_CONNECTION_EXCEPTION:
                    if (msg.arg1 == ErrorCode.ERROR_CAS_RECORD_SEARCH_START_TIME_ERROR) {
                    } else {
                        String detail = (msg.obj != null ? msg.obj.toString() : "");
                    }
                    stopPlay();
                    break;
                case RemoteListContant.MSG_REMOTELIST_UI_UPDATE:
                    break;
                case RemoteListContant.MSG_REMOTELIST_STREAM_TIMEOUT:
                    stopPlay();
                    break;
                case 1:
                    messagePlaybackBeginTimeTv.setText(timeStr.substring(3));
                    messagePlaybackProgressbar.setProgress(recordTime);
                    messagePlaybackProgressSeekbar.setProgress(recordTime);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_details_record_activity);
        ButterKnife.bind(this);
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cn");
        mCameraInfo = (Cameras.DatasBean) getIntent().getExtras().getSerializable("cameraInfo");
        mEZAlarmInfo = getIntent().getParcelableExtra(IntentConsts.EXTRA_ALARM_INFO);
        try {
            mDate = mSdf.parse(mEZAlarmInfo.getAlarmStartTime());
            startTime.setTime(mDate);
            startTime.add(Calendar.SECOND, -5);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.SECOND, ALARM_MAX_DURATION);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initViewOnClick();
        initLocalInfo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EZDeviceInfo deviceInfo = EZOpenSDK.getInstance().getDeviceInfo(mCameraInfo.getCaremaID());
                    List<EZCameraInfo> cameraInfoList = deviceInfo.getCameraInfoList();
                    if (cameraInfoList != null && cameraInfoList.size() > 0) {
                        final int cameraNo = cameraInfoList.get(0).getCameraNo();
                        Log.i(TAG, "cameraNo:" + cameraNo);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCameraNo = cameraNo;
                                PlayBackUtils.loadEZDeviceRecordFiles(mCameraInfo.getCaremaID(), cameraNo, startTime, endTime, mHandler, MessageDetailPlayRecordActivity.this);
                            }
                        });
                    }
                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        getCurrentParamter();
    }

    private void getCurrentParamter() {
        // 取控件当前的布局参数
        mOrientation = this.getRequestedOrientation();
        mLayoutParams1 = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
        mWidth1 = mLayoutParams1.width;
        mHeight1 = mLayoutParams1.height;
        // 取控件当前的布局参数
        mLayoutParams = (RelativeLayout.LayoutParams) mRelativeLayout.getLayoutParams();
        mWidth = mLayoutParams.width;
        mHeight = mLayoutParams.height;
    }

    private void initViewOnClick() {
        mSurfaceView.getHolder().addCallback(callback);
        mSurfaceView.setZOrderOnTop(false);
//        currCamera.setText(mCameraInfo.getName());
//        recordactivityBack.setOnClickListener(this);
//        remoteplaybackSoundBtn.setOnClickListener(this);
//        remoteplaybackPlayBtn.setOnClickListener(this);
//        fullscreenButton.setOnClickListener(this);
//        recordBack.setOnClickListener(this);
//        mSurfaceView.setOnClickListener(this);
//        remoteplaybackReplayBtn.setOnClickListener(this);
    }

    private void initLocalInfo() {
        mLocalInfo = LocalInfo.getInstance();
        // 获取屏幕参数
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mLocalInfo.setScreenWidthHeight(metric.widthPixels, metric.heightPixels);
        mLocalInfo.setNavigationBarHeight((int) Math.ceil(25 * getResources().getDisplayMetrics().density));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @OnClick(R2.id.remoteplayback_sound_btn)
    public void onViewClicked() {
        //声音开关
        switchShound();
    }
    @OnClick(R2.id.remoteplayback_play_btn)
    public void onViewClicked1() {
        //暂停恢复
        pauseOResume();
    }
    @OnClick(R2.id.record_back)
    public void onViewClicked2() {
        if (mOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    @OnClick(R2.id.fullscreen_button)
    public void onViewClicked3() {
        if (mOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    @OnClick(R2.id.recordactivity_back)
    public void onViewClicked4() {
        finish();
    }
    @OnClick(R2.id.remoteplayback_sv)
    public void onViewClicked5() {
        if (recordControl.getVisibility()==View.VISIBLE){
            recordControl.setVisibility(View.GONE);
            messagePlaybackProgressbar.setVisibility(View.VISIBLE);
        }else {
            recordControl.setVisibility(View.VISIBLE);
            messagePlaybackProgressbar.setVisibility(View.GONE);
        }
    }
    @OnClick(R2.id.goback)
    public void onViewClicked6() {
        recordTime=0;
        mEZPlayer.startPlayback(startTime, endTime);
    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.remoteplayback_sound_btn:
//                //声音开关
//                switchShound();
//                break;
//            case R.id.remoteplayback_play_btn:
//                //暂停恢复
//                pauseOResume();
//                break;
//            case R.id.record_back:
//            case R.id.fullscreen_button:
//                if (mOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                } else {
//                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                }
//                break;
//            case R.id.recordactivity_back:
//                finish();
//                break;
//            case R.id.remoteplayback_sv:
//                if (recordControl.getVisibility()==View.VISIBLE){
//                    recordControl.setVisibility(View.GONE);
//                    messagePlaybackProgressbar.setVisibility(View.VISIBLE);
//                }else {
//                    recordControl.setVisibility(View.VISIBLE);
//                    messagePlaybackProgressbar.setVisibility(View.GONE);
//                }
//                break;
//            case R.id.remoteplayback_replay_btn:
//                recordTime=0;
//                mEZPlayer.startPlayback(startTime, endTime);
//                break;
//        }
//    }

    //回放：ezopen://[验证码@]open.ys7.com/序列号/通道号[.回放源].rec[?参数]
    @Override
    public void onHasData(List<EZDeviceRecordFile> ezDeviceRecordFiles) {
        mEZDeviceRecordFiles = ezDeviceRecordFiles;
        if (mEZPlayer != null) {
            mEZPlayer.stopPlayback();
        }
        List<RemoteFileInfo> remoteFileInfos = new ArrayList<>();
        for (EZDeviceRecordFile ezDeviceRecordFile : ezDeviceRecordFiles) {
            RemoteFileInfo remoteFileInfo = new RemoteFileInfo();
            Calendar startTime = ezDeviceRecordFile.getStartTime();
            remoteFileInfo.setStartTime(startTime);
            Calendar stopTime = ezDeviceRecordFile.getStopTime();
            remoteFileInfo.setStopTime(stopTime);
            remoteFileInfos.add(remoteFileInfo);
        }
        mEZPlayer = EZOpenSDK.getInstance().createPlayer(mCameraInfo.getCaremaID(), mCameraNo);
        if (mHolder != null) {
            mEZPlayer.setHandler(mHandler);
            mEZPlayer.setSurfaceHold(mHolder);
            mEZPlayer.setPlayVerifyCode(mCameraInfo.getPlayVerifyCode());
            loading.setVisibility(View.VISIBLE);
            mEZPlayer.startPlayback(startTime, endTime);

            messagePlaybackProgressSeekbar.setVisibility(View.VISIBLE);
            messagePlaybackProgressbar.setMax(ALARM_MAX_DURATION);
            messagePlaybackProgressbar.setProgress(0);
            messagePlaybackProgressSeekbar.setMax(ALARM_MAX_DURATION);
            messagePlaybackProgressSeekbar.setProgress(0);
            messagePlaybackProgressSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                /**
                 * 拖动条停止拖动的时候调用
                 */
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    stopTimer();
                    recordTime = seekBar.getProgress();
                    setTimer();
                }

                /**
                 * 拖动条开始拖动的时候调用
                 */
                @Override
                public void onStartTrackingTouch(SeekBar arg0) {

                }

                /**
                 * 拖动条进度改变的时候调用
                 */
                @Override
                public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivityStoped = true;
        stopTimer();
    }
    public void setTimer(){
        mUpdateTimer=new Timer();
        mUpdateTimerTask=new TimerTask() {
            @Override
            public void run() {
                if (recordTime<45) {
                    recordTime++;
                    dispTime.setTimeInMillis(recordTime * 1000);
                    timeStr = sdf.format(dispTime.getTime());
                    mHandler.sendEmptyMessage(1);
                }
            }
        };
        mUpdateTimer.schedule(mUpdateTimerTask,1000,1000);
    }

    public void stopTimer(){
        mHandler.removeMessages(1);
        // 停止录像计时
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mOrientation = newConfig.orientation;
        onOrientationChanged(mOrientation);
        super.onConfigurationChanged(newConfig);

    }

    private void onOrientationChanged(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            fullScreen(false);
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fullScreen(true);
        }

    }

    private void fullScreen(boolean enable) {
        if (enable) {
            recordactivityHeader.setVisibility(View.GONE);
            fullscreenButton.setVisibility(View.GONE);
            recordBack.setVisibility(View.VISIBLE);
            landscape();
        } else {
            recordactivityHeader.setVisibility(View.VISIBLE);
            fullscreenButton.setVisibility(View.VISIBLE);
            recordBack.setVisibility(View.GONE);
            portrait();
        }
    }

    /**
     * 设置为竖屏
     */
    private void portrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mLayoutParams1.height = mHeight1;
        mLayoutParams1.width = mWidth1;
        mSurfaceView.setLayoutParams(mLayoutParams1);// 使设置好的布局参数应用到控件
        mLayoutParams.height = mHeight;
        mLayoutParams.width = mWidth;
        mRelativeLayout.setLayoutParams(mLayoutParams);// 使设置好的布局参数应用到控件

    }

    /**
     * 设置为横屏
     */
    private void landscape() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置videoView全屏播放
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置videoView横屏播放
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) mRelativeLayout.getLayoutParams(); // 取控件当前的布局参数
        layoutParams1.height = height;//设置控件的高度
        layoutParams1.width = width;
        mRelativeLayout.setLayoutParams(layoutParams1);// 使设置好的布局参数应用到控件
        mSurfaceView.setLayoutParams(layoutParams1);// 使设置好的布局参数应用到控件
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    public int getRequestedOrientation() {
        return super.getRequestedOrientation();
    }

    @Override
    public void onLoadStart() {
        resetState();
        loading.setVisibility(View.VISIBLE);
        tip_tv.setVisibility(View.GONE);
    }

    @Override
    public void onNoData() {
        loading.setVisibility(View.GONE);
        tip_tv.setText(R.string.no_remote_data);
        tip_tv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOffline() {
        loading.setVisibility(View.GONE);
        tip_tv.setText(R.string.offline);
        tip_tv.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();

    }

    private void stopPlay() {
        loading.setVisibility(View.GONE);
        if (mEZPlayer != null) {
            mEZPlayer.stopPlayback();
            if (m_wklk!=null && lockWake) {
                m_wklk.release(); //解除保持唤醒
                lockWake=false;
            }
        }
    }

    private void switchShound() {
        if (mIsShound) {
            mEZPlayer.closeSound();
        } else {
            mEZPlayer.openSound();
        }
        mIsShound = !mIsShound;
        remoteplaybackSoundBtn.setSelected(mIsShound);
    }

    private boolean isPause = false;

    private void pauseOResume() {
        if (isPlaying) {
            mEZPlayer.pausePlayback();
            isPlaying = false;
            isPause = true;
            remoteplaybackPlayBtn.setSelected(false);
            stopTimer();
            if (m_wklk!=null && lockWake) {
                m_wklk.release(); //解除保持唤醒
                lockWake=false;
            }
        } else {
            if (mEZPlayer != null && isPause) {
                isPlaying = true;
                mEZPlayer.resumePlayback();
                remoteplaybackPlayBtn.setSelected(true);
                setTimer();
                mHandler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mActivityStoped = true;
        if (mEZPlayer != null) {
            mEZPlayer.stopPlayback();
            resetState();
            if (m_wklk!=null && lockWake) {
                m_wklk.release(); //解除保持唤醒
                lockWake=false;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraNo != -1 && mActivityStoped) {
            Calendar seletedTime=Calendar.getInstance();
            seletedTime.add(Calendar.SECOND,recordTime);
            seletedTime.setTimeInMillis(startTime.getTimeInMillis() + recordTime * 1000);
            mEZPlayer.seekPlayback(seletedTime);
//        PlayBackUtils.loadEZDeviceRecordFiles(mCameraInfo.getCaremaID(), mCameraNo, seletedTime, endTime, mHandler, this);
        }
        mActivityStoped = false;
    }

    private void resetState() {
        resetPlayState();
        resetSoundState();
    }

    private void resetPlayState() {
        isPlaying = false;
        isPause = false;
        remoteplaybackPlayBtn.setSelected(false);
    }

    private void resetSoundState() {
        mIsShound = false;
        remoteplaybackSoundBtn.setSelected(false);
    }

}

