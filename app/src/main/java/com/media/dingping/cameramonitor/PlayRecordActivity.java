package com.media.dingping.cameramonitor;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.customview.RemoteFileTimeBar;
import com.media.dingping.cameramonitor.customview.loading.LoadingView;
import com.media.dingping.cameramonitor.playback.PlayBackUtils;
import com.media.dingping.cameramonitor.playback.RemoteListContant;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.openapi.bean.EZDeviceRecordFile;
import com.videogo.remoteplayback.RemoteFileInfo;
import com.videogo.util.LocalInfo;
import com.videogo.widget.CheckTextButton;
import com.videogo.widget.TimeBarHorizontalScrollView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/23 0023.
 * 播放历史影像
 */

public class PlayRecordActivity extends Activity implements TimeBarHorizontalScrollView.TimeScrollBarScrollListener, PlayBackUtils.OnEZDeviceRecordFileCallBack2, View.OnTouchListener{

    //    @BindView(R.id.title_bar)
//    TitleBar mTitleBar;
    @BindView(R2.id.remoteplayback_play_rl)
    RelativeLayout mRelativeLayout;
    @BindView(R2.id.record_control)
    LinearLayout recordControl;
    @BindView(R2.id.remoteplayback_file_time_bar)
    RemoteFileTimeBar mRemoteFileTimeBar;
    @BindView(R2.id.remoteplayback_timebar)
    TimeBarHorizontalScrollView mRemotePlayBackTimeBar;
    @BindView(R2.id.remoteplayback_time_tv)
    TextView mRemotePlayBackTimeTv;
    @BindView(R2.id.calendar)
    Button mCalendar;
    @BindView(R2.id.left_button)
    ImageButton leftButton;
    @BindView(R2.id.right_button)
    ImageButton rightButton;
    @BindView(R2.id.loading_view)
    LoadingView loadingView;
    @BindView(R2.id.remoteplayback_timebar_rl)
    RelativeLayout remoteplaybackTimebarRl;
    @BindView(R2.id.kong)
    RelativeLayout kong;
    @BindView(R2.id.tip_tv)
    TextView tip_tv;
    @BindView(R2.id.remoteplayback_sv)
    SurfaceView mSurfaceView;
    @BindView(R2.id.loading)
    LoadingView loading;
    @BindView(R2.id.remoteplayback_sound_btn)
    ImageButton remoteplaybackSoundBtn;
    @BindView(R2.id.remoteplayback_play_btn)
    ImageButton remoteplaybackPlayBtn;
    @BindView(R2.id.fullscreen_button)
    CheckTextButton fullscreenButton;
    @BindView(R2.id.record_downlayout)
    RelativeLayout recordDownlayout;
    @BindView(R2.id.record_back)
    ImageButton recordBack;
    @BindView(R2.id.recordactivity_back)
    ImageView recordactivityBack;
    @BindView(R2.id.curr_camera)
    TextView currCamera;
    @BindView(R2.id.recordactivity_header)
    RelativeLayout recordactivityHeader;

    private RelativeLayout.LayoutParams mLayoutParams1;
    private RelativeLayout.LayoutParams mLayoutParams;
    private int mWidth;
    private int mHeight;
    private int mWidth1;
    private int mHeight1;

    private long mPlayTime = 0;
    private final String TAG = PlayRecordActivity.this.getClass().getSimpleName();
    private LocalInfo mLocalInfo = null;
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    private final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
    private Cameras.DatasBean mCameraInfo;
    private int mCameraNo = -1;
    private AlertDialog mAlertDialog;
    private boolean mIsShound = false;
    private boolean isPlaying = false;
    private boolean mActivityStoped = false;

    private Date currQueryDate;

    private List<EZDeviceRecordFile> mEZDeviceRecordFiles = null;

    private PowerManager.WakeLock m_wklk;
    private boolean lockWake=false;

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
                    tip_tv.setText("录像回放完成");
                    tip_tv.setVisibility(View.VISIBLE);
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
                    break;
                case EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_STOP_SUCCESS:
                    break;
                case EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_PLAY_FAIL:
                    stopPlay();
                    tip_tv.setText("没有更多录像");
                    tip_tv.setVisibility(View.VISIBLE);
                    if (m_wklk!=null && lockWake) {
                        m_wklk.release(); //解除保持唤醒
                        lockWake=false;
                    }
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
                default:
                    break;
            }
        }

        ;

    };
    /**
     * 屏幕当前方向
     */
    private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private EZPlayer mEZPlayer;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        ButterKnife.bind(this);
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cn");
        mCameraInfo = (Cameras.DatasBean) getIntent().getExtras().getSerializable("cameraInfo");
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
                                try {
                                    mCameraNo = cameraNo;
                                    Date date = sdf2.parse(mCalendar.getText().toString());
                                    currQueryDate = date;
                                    PlayBackUtils.loadEZDeviceRecordFiles(mCameraInfo.getCaremaID(), cameraNo, date, mHandler, PlayRecordActivity.this);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        mCalendar.setText(sdf2.format(new Date()));
        View view = getLayoutInflater().inflate(R.layout.calendar_layout, null);
        MaterialCalendarView cv = initMaterialCalendarView(view);
        mAlertDialog = new AlertDialog
                .Builder(this)
                .setView(view)
                .setTitle("")
                .create();
        cv.setOnDateChangedListener(onDateSelectedListener);
        mCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFinishing()) {
                    mAlertDialog.show();
                }
            }
        });

        currCamera.setText(mCameraInfo.getName());
//        recordactivityBack.setOnClickListener(this);
//        rightButton.setOnClickListener(this);
//        leftButton.setOnClickListener(this);
//        remoteplaybackSoundBtn.setOnClickListener(this);
//        remoteplaybackPlayBtn.setOnClickListener(this);
//        fullscreenButton.setOnClickListener(this);
//        recordBack.setOnClickListener(this);
    }

//    private void initPlayer() {
//        PlayBackUtils.loadEZDeviceRecordFiles(mCameraInfo.getCaremaID(), new Date(), mHandler, this);
//    }


    @NonNull
    private MaterialCalendarView initMaterialCalendarView(View view) {
        MaterialCalendarView cv = (MaterialCalendarView) view.findViewById(R.id.calendar_view);
        cv.setCurrentDate(new Date());
        cv.setSelectionColor(R.color.auto_wifi_tip_red);
        cv.state().edit()
                // 设置你的日历 第一天是周一还是周一
                .setFirstDayOfWeek(Calendar.SATURDAY)
                // 设置你的日历的最小的月份  月份为你设置的最小月份的下个月 比如 你设置最小为1月 其实你只能滑到2月
                .setMinimumDate(CalendarDay.from(2015, 1, 1))
                // 同最小 设置最大
                .setMaximumDate(new Date())
                // 设置你的日历的模式  是按月 还是按周
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        return cv;
    }

    private void changeDate(int tag) {
        try {
            Date oldDate = sdf2.parse(mCalendar.getText().toString());
            Date newDate = null;
            int oneDataTime = 60 * 1000 * 60 * 24;
            if (tag == 0) {
                newDate = new Date(oldDate.getTime() - oneDataTime);
            } else {
                newDate = new Date(oldDate.getTime() + oneDataTime);
                if (newDate.after(new Date())) {
                    Toast.makeText(this, "无法穿越到未来呢", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            String newDateStr = sdf2.format(newDate);
            mCalendar.setText(newDateStr);
            stopPlay();
            currQueryDate = newDate;
            PlayBackUtils.loadEZDeviceRecordFiles(mCameraInfo.getCaremaID(), mCameraNo, newDate, mHandler, this);
        } catch (ParseException e) {

        }
    }

    private void initLocalInfo() {
        mLocalInfo = LocalInfo.getInstance();
        // 获取屏幕参数
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mLocalInfo.setScreenWidthHeight(metric.widthPixels, metric.heightPixels);
        mLocalInfo.setNavigationBarHeight((int) Math.ceil(25 * getResources().getDisplayMetrics().density));
        mRemoteFileTimeBar.setX(0, mLocalInfo.getScreenWidth() * 6);
        mRemotePlayBackTimeTv.setText("00:00:00");
        mRemotePlayBackTimeBar.setTimeScrollBarScrollListener(this);
        mRemotePlayBackTimeBar.smoothScrollTo(0, 0);
    }

    private OnDateSelectedListener onDateSelectedListener = new OnDateSelectedListener() {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
            mCalendar.setText(calendarDay.getYear() + "-" + (calendarDay.getMonth() + 1) + "-" + calendarDay.getDay());
            if (!isFinishing()) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAlertDialog.dismiss();
                        try {
                            if (mEZPlayer != null) {
                                mEZPlayer.stopPlayback();
                            }
                            Date parse = sdf2.parse(mCalendar.getText().toString());
                            currQueryDate = parse;
                            PlayBackUtils.loadEZDeviceRecordFiles(mCameraInfo.getCaremaID(), mCameraNo, parse, mHandler, PlayRecordActivity.this);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, 700);
            }
        }
    };

    @Override
    public void onScrollChanged(int left, int top, int oldLeft, int oldTop, HorizontalScrollView scrollView) {
        Calendar startCalendar = mRemoteFileTimeBar.pos2Calendar(left, mOrientation);
        if (startCalendar != null) {
            mPlayTime = startCalendar.getTimeInMillis();
            mRemotePlayBackTimeTv.setText(sdf.format(mPlayTime));
        }
    }

    @Override
    public void onScrollStart(HorizontalScrollView horizontalScrollView) {
        //移动开始，显示
        mHandler.removeCallbacks(mCurrJumpRunable);
        tip_tv.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onScrollStop(HorizontalScrollView horizontalScrollView) {
        if (mEZPlayer != null && mEZDeviceRecordFiles != null) {
            resetPlayState();
            Calendar seekTime = Calendar.getInstance();
            seekTime.setTimeInMillis(mPlayTime);
            Calendar stopTime = mEZDeviceRecordFiles.get(mEZDeviceRecordFiles.size() - 1).getStopTime();
            if (seekTime.after(stopTime)) {
                mHandler.sendEmptyMessageDelayed(206, 500);
            } else {
                mEZPlayer.stopPlayback();
                mEZPlayer.setHandler(mHandler);
                mEZPlayer.setSurfaceHold(mHolder);
                mEZPlayer.setPlayVerifyCode(mCameraInfo.getPlayVerifyCode());
                loading.setVisibility(View.VISIBLE);
                boolean hasJump = true;
                Calendar jumpTime = null;
                for (EZDeviceRecordFile mEZDeviceRecordFile : mEZDeviceRecordFiles) {
                    Calendar startTime = mEZDeviceRecordFile.getStartTime();
                    if (seekTime.before(mEZDeviceRecordFile.getStopTime()) && seekTime.after(startTime)) {
                        hasJump = false;
                    }
                    if (jumpTime == null && seekTime.before(startTime)) {
                        jumpTime = startTime;
                        break;
                    }
                }
                mEZPlayer.startPlayback(seekTime, stopTime);
                //不在录像时段当中
                if (hasJump && jumpTime != null) {
                    //那么需要跳转到下一个时段的开始，找到下一个时段
                    mCurrJumpRunable = new JumpRunable(jumpTime);
                    mHandler.postDelayed(mCurrJumpRunable, 2000);
                }

            }
        }
    }

    private JumpRunable mCurrJumpRunable;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private class JumpRunable implements Runnable {
        private Calendar jumpTime;

        public JumpRunable(Calendar jumpTime) {
            this.jumpTime = jumpTime;
        }

        @Override
        public void run() {
            float pos = mRemoteFileTimeBar.getScrollPosByPlayTime(jumpTime.getTimeInMillis(), mOrientation);
            mRemotePlayBackTimeBar.smoothScrollTo((int) pos, 0);
            mRemotePlayBackTimeTv.setText(sdf.format(jumpTime.getTimeInMillis()));
        }
    }

    @OnClick(R2.id.left_button)
    public void onViewClicked() {
        changeDate(0);
    }
    @OnClick(R2.id.right_button)
    public void onViewClicked1() {
        changeDate(1);
    }
    @OnClick(R2.id.remoteplayback_sound_btn)
    public void onViewClicked2() {
//声音开关
        switchShound();
    }
    @OnClick(R2.id.remoteplayback_play_btn)
    public void onViewClicked3() {
//暂停恢复
        pauseOResume();
    }
    @OnClick(R2.id.record_back)
    public void onViewClicked4() {
        if (mOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    @OnClick(R2.id.fullscreen_button)
    public void onViewClicked5() {
        if (mOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    @OnClick(R2.id.recordactivity_back)
    public void onViewClicked6() {
        finish();
    }







//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.left_button:
//                changeDate(0);
//                break;
//            case R.id.right_button:
//                changeDate(1);
//                break;
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
//        }
//
//    }

    //回放：ezopen://[验证码@]open.ys7.com/序列号/通道号[.回放源].rec[?参数]
    @Override
    public void onHasData(List<EZDeviceRecordFile> ezDeviceRecordFiles, Date queryDate) {
        if (currQueryDate.compareTo(queryDate) != 0) {
            return;
        }
        mEZDeviceRecordFiles = ezDeviceRecordFiles;
        loadingView.setVisibility(View.GONE);
        kong.setVisibility(View.GONE);
        remoteplaybackTimebarRl.setVisibility(View.VISIBLE);
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
        final Calendar startTime = ezDeviceRecordFiles.get(0).getStartTime();
        mRemoteFileTimeBar.drawFileLayout(remoteFileInfos, null, PlayBackUtils.getStartTime(mCalendar.getText().toString()), PlayBackUtils.getEndTime(mCalendar.getText().toString()));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float pos = mRemoteFileTimeBar.getScrollPosByPlayTime(startTime.getTimeInMillis(), mOrientation);
                mRemotePlayBackTimeBar.smoothScrollTo((int) pos, 0);
                mRemotePlayBackTimeTv.setText(sdf.format(startTime.getTimeInMillis()));
            }
        }, 500);
        mEZPlayer = EZOpenSDK.getInstance().createPlayer(mCameraInfo.getCaremaID(), mCameraNo);
        if (mHolder != null) {
            mEZPlayer.setHandler(mHandler);
            mEZPlayer.setSurfaceHold(mHolder);
            mEZPlayer.setPlayVerifyCode(mCameraInfo.getPlayVerifyCode());
            loading.setVisibility(View.VISIBLE);
            mEZPlayer.startPlayback(ezDeviceRecordFiles.get(0));
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
            recordDownlayout.setVisibility(View.GONE);
            fullscreenButton.setVisibility(View.GONE);
            recordBack.setVisibility(View.VISIBLE);
            landscape();
        } else {
            recordactivityHeader.setVisibility(View.VISIBLE);
            recordDownlayout.setVisibility(View.VISIBLE);
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
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mRelativeLayout.setLayoutParams(layoutParams1);// 使设置好的布局参数应用到控件
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) recordDownlayout.getLayoutParams(); // 取控件当前的布局参数
//        layoutParams.height = height;//设置控件的高度
//        layoutParams.width = width;
        mSurfaceView.setLayoutParams(layoutParams1);// 使设置好的布局参数应用到控件
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        Log.i(TAG, "setRequestedOrientation: requestedOrientation=" + requestedOrientation);
        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    public int getRequestedOrientation() {
        return super.getRequestedOrientation();
    }

    @Override
    public void onLoadStart(Date queryDate) {
        if (currQueryDate.compareTo(queryDate) != 0) {
            return;
        }
        resetState();
        loadingView.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        tip_tv.setVisibility(View.GONE);
        remoteplaybackTimebarRl.setVisibility(View.GONE);
        kong.setVisibility(View.GONE);
    }

    @Override
    public void onNoData(Date queryDate) {
        if (currQueryDate.compareTo(queryDate) != 0) {
            return;
        }
        loadingView.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        remoteplaybackTimebarRl.setVisibility(View.GONE);
        tip_tv.setText(R.string.no_remote_data);
        tip_tv.setVisibility(View.VISIBLE);
        kong.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOffline() {
        loadingView.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        remoteplaybackTimebarRl.setVisibility(View.GONE);
        tip_tv.setText(R.string.offline);
        tip_tv.setVisibility(View.VISIBLE);
        kong.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            portrait();
            return;
        }
        super.onBackPressed();
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
        } else {
            if (mEZPlayer != null && isPause) {
                isPlaying = true;
                mEZPlayer.resumePlayback();
                remoteplaybackPlayBtn.setSelected(true);
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
            try {
                Date date = sdf2.parse(mCalendar.getText().toString());
                currQueryDate = date;
                PlayBackUtils.loadEZDeviceRecordFiles(mCameraInfo.getCaremaID(), mCameraNo, date, mHandler, this);
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

