package com.media.dingping.cameramonitor.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dianpingmedia.camerapush.PushManager;
import com.media.dingping.cameramonitor.LoginActivity;
import com.media.dingping.cameramonitor.PersonalSettingActivity;
import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.R2;
import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.bean.StreamInfo;
import com.media.dingping.cameramonitor.bean.UpdateInfo;
import com.media.dingping.cameramonitor.customview.MySurfaceView;
import com.media.dingping.cameramonitor.customview.WaitDialog;
import com.media.dingping.cameramonitor.presenter.MainPresenter2;
import com.media.dingping.cameramonitor.presenter.impl.IMainPresenter;
import com.media.dingping.cameramonitor.updater.AppUpdater;
import com.media.dingping.cameramonitor.utils.NetWorkUtil;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.util.SDCardUtil;
import com.videogo.util.Utils;
import com.videogo.widget.CheckTextButton;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.media.dingping.cameramonitor.R2.id.realplay_control_rl;

/**
 * Created by Administrator on 2017/6/6 0006.
 * 播放影像及控制
 */


public class MainActivity2 extends AppCompatActivity implements IMainView, View.OnTouchListener, SurfaceHolder.Callback, MySurfaceView.OnCtrlListener {
    private final String TAG = getClass().getSimpleName();
    @BindView(R2.id.loading)
    ProgressBar loading;
    @BindView(R2.id.realplay_play_iv)
    ImageView realplayPlayIv;
    @BindView(R2.id.realplay_tip_tv)
    TextView realplayTipTv;

    @BindView(R2.id.realplay_play_btn)
    ImageButton realplayPlayBtn;
    @BindView(R2.id.realplay_sound_btn)
    ImageButton realplaySoundBtn;
    @BindView(R2.id.realplay_talk_btn)
    ImageButton realplayTalkBtn;
    @BindView(R2.id.realplay_record_btn)
    ImageButton realplayRecordBtn;
    @BindView(R2.id.realplay_quality_btn)
    Button realplayQualityBtn;
    @BindView(R2.id.fullscreen_button)
    CheckTextButton fullscreenButton;

    @BindView(R2.id.gotoportrait)
    CheckTextButton gotoPortrait;
    @BindView(R2.id.realplay_full_play_btn)
    ImageButton realplayFullPlayBtn;
    @BindView(R2.id.realplay_full_sound_btn)
    ImageButton realplayFullSoundBtn;
    @BindView(R2.id.realplay_full_talk_btn)
    ImageButton realplayFullTalkBtn;
    @BindView(R2.id.realplay_full_record_btn)
    ImageButton realplayFullRecordBtn;
    @BindView(R2.id.realplay_full_quality_btn)
    Button realplayFullQualityBtn;

    @BindView(R2.id.play_empty)
    ImageView playEmpty;
    @BindView(R2.id.realplay_loading_rl)
    RelativeLayout realplayLoadingRl;

    @BindView(R2.id.down_layout)
    RelativeLayout downLayout;
    @BindView(realplay_control_rl)
    RelativeLayout realplayControlRl;
    @BindView(R2.id.realplay_full_ctr_layout)
    RelativeLayout realplayFullCtrLayout;
    @BindView(R2.id.back)
    ImageButton back;
    @BindView(R2.id.ptz_tip)
    RelativeLayout ptzTip;
//    @BindView(R2.id.ctrlView2)
//    MySurfaceView ctrlView2;

    private ExecutorService es = Executors.newSingleThreadExecutor();
    @BindView(R2.id.draw_switch)
    ImageView drawSwitch;
    @BindView(R2.id.curr_camera)
    TextView currCamera;
    @BindView(R2.id.next_camera)
    TextView nextCamera;
    @BindView(R2.id.header)
    RelativeLayout header;
    //    @BindView(R2.id.play_empty)
//    ImageView playEmpty;
    @BindView(R2.id.play_layout)
    RelativeLayout playLayout;
    @BindView(R2.id.header_iv)
    ImageView headerIv;
    @BindView(R2.id.username)
    TextView username;
    @BindView(R2.id.listview)
    ListView listview;
    @BindView(R2.id.bt_exit)
    Button btExit;

    @BindView(R2.id.rl_layout)
    RelativeLayout rlLayout;
    @BindView(R2.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R2.id.surfaceView)
    SurfaceView mSurfaceView;
    @BindView(R2.id.header_layout)
    LinearLayout headerLayout;
    @BindView(R2.id.ctrlView)
    MySurfaceView ctrlView;
    TextView cameraCount;


    private IMainPresenter mMainPresenter;
    private CameraAdapter mAdapter;

    private SurfaceHolder mHolder;
    private EZPlayer mEZPlayer;
    private StreamInfo mStreamInfo;
    private WaitDialog mWaitDialog = null;
    private PopupWindow mQualityPopupWindow = null;

    private EZConstants.EZVideoLevel mCurrentQulityMode = EZConstants.EZVideoLevel.VIDEO_LEVEL_BALANCED;
    private CheckTextButton mFullScreenTitleBarBackBtn;
    private ExitBroadcast exitBroadcast;

    private boolean isStop;
    private RelativeLayout.LayoutParams mLayoutParams1;
    private RelativeLayout.LayoutParams mLayoutParams;
    private int mWidth;
    private int mHeight;
    private int mWidth1;
    private int mHeight1;
    private boolean mIsShound = false;
    private boolean isTalking = false;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private boolean playSuccess = false;

    private SharedPreferences mSharedPreferences;
    private String mNetwokType;
    private GestureDetector mGestureDetector;

    private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;  //默认竖屏


    private PowerManager.WakeLock m_wklk;
    private boolean lockWake=false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
                    Log.i(TAG, "播放成功");
                    loading.setVisibility(View.GONE);
                    realplayPlayBtn.setSelected(true);
                    realplayFullPlayBtn.setSelected(true);
                    isPlaying = true;
                    playSuccess = true;
                    m_wklk.acquire(); //设置保持唤醒
                    lockWake=true;
                    break;
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL:
                    ErrorInfo errorInfo = (ErrorInfo) msg.obj;
                    updateRealPlayFailUI(errorInfo.errorCode);
                    Log.i(TAG, "播放出错");
                    playSuccess = false;
                    lockWake=false;
                    break;
            }
        }


    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //查看升级，拿版本号
        setContentView(R.layout.activity_main_2);
        ButterKnife.bind(this);
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cn");
        mMainPresenter = new MainPresenter2(this);
        mMainPresenter.isFirstTime();
        mMainPresenter.getCameraList(getLoginUserName());
        initViewAndDatas();
        mOrientation = this.getRequestedOrientation();
        mSharedPreferences = getSharedPreferences("setting", 0);
        mNetwokType = NetWorkUtil.getCurrentNetworkType(this);
        getCurrentParams();

        if (mSharedPreferences.getBoolean("iscontrol", true)) {
            if (mNetwokType.equals("2G") || mNetwokType.equals("3G") || mNetwokType.equals("4G")) {
                Toast.makeText(this, "当前处于2G/3G/4G网络状态下", Toast.LENGTH_SHORT).show();
            }
        }
        mGestureDetector = new GestureDetector(this, onGestureListener);
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

    }

    public  Activity getActivity(){
        return this;
    }

    private void getCurrentParams() {
        // 取控件当前的布局参数
        mLayoutParams1 = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
        mWidth1 = mLayoutParams1.width;
        mHeight1 = mLayoutParams1.height;
        // 取控件当前的布局参数
        mLayoutParams = (RelativeLayout.LayoutParams) playLayout.getLayoutParams();
        mWidth = mLayoutParams.width;
        mHeight = mLayoutParams.height;
    }

    private void initViewAndDatas() {
        mWaitDialog = new WaitDialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mWaitDialog.setCancelable(false);
        mSurfaceView.getHolder().addCallback(this);
        mSurfaceView.setZOrderOnTop(false);
        ctrlView.addCtrlListener(this);
        username.setText(getLoginUserName());
        mAdapter = new CameraAdapter(new Cameras(), this);
        ptzTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ptzTip.setVisibility(View.GONE);
            }
        });
        mAdapter.setOnClickListener(new CameraAdapter.OnClickListener() {
            @Override
            public void onPlayClick(BaseAdapter adapter, View view, int position) {

            }

            @Override
            public void onAlarmListClick(BaseAdapter adapter, View view, int position) {

            }

            @Override
            public void onRemotePlayBackClick(BaseAdapter adapter, View view, int position) {

            }

            @Override
            public void onSetDeviceClick(BaseAdapter adapter, View view, final int position) {
            }

            @Override
            public void onDeviceDefenceClick(BaseAdapter adapter, View view, int position) {


            }
        });
        View view = getLayoutInflater().inflate(R.layout.listview_header, listview, false);
        cameraCount= (TextView) view.findViewById(R.id.camera_count);
        listview.addHeaderView(view);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(TAG, mAdapter.getCurrPosition() + "");
                if (position == 0) return;
                resetSound();
                resetTalk();
                if (mEZPlayer != null) {
                    mEZPlayer.stopRealPlay();
                    mEZPlayer.release();
                    mEZPlayer = null;
                    if (m_wklk!=null && lockWake) {
                        m_wklk.release(); //解除保持唤醒
                        lockWake=false;
                    }
                }
                Cameras.DatasBean camera = mAdapter.getClickCameraIdToPosition(position);
                String cameraId = camera.getCaremaID();
                if (cameraId != null && !cameraId.equals("")) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    mAdapter.setCurrPosition(position);
                    currCamera.setText(camera.getName());
//                    playEmpty.setVisibility(View.VISIBLE);
                    sethide();
                    mMainPresenter.getCameraPlayUrl(getLoginUserName(), cameraId, camera.getPlayVerifyCode());
                } else {
                    Toast.makeText(MainActivity2.this, "摄像头ID为空，该终端未绑定摄像头", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //设置下拉刷新动画颜色
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        //设置下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMainPresenter.getCameraList(getLoginUserName());
            }
        });
        realplayPlayIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reGetDataBeanToStart();
                //重新读取
            }
        });

        mFullScreenTitleBarBackBtn = new CheckTextButton(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mFullScreenTitleBarBackBtn.setBackground(getResources().getDrawable(R.drawable.common_title_back_selector));
        }
    }

    @Override
    public void isFirstLogin() {

    }

    @Override
    protected void onResume() {
        if (mEZPlayer != null && isStop && mStreamInfo != null) {
            isStop = false;
            String deviceSerial = mStreamInfo.getEzCameraInfo().getDeviceSerial();
            int cameraNo = mStreamInfo.getEzCameraInfo().getCameraNo();
            mEZPlayer = EZOpenSDK.getInstance().createPlayer(deviceSerial, cameraNo);
            Log.i(TAG, "序列号：" + deviceSerial);
            Log.i(TAG, "通道号：" + cameraNo);
            Log.i(TAG, "是否被加密：streamInfo.getEncrypt()=" + mStreamInfo.getEncrypt());
            Log.i(TAG, "验证码：" + mStreamInfo.getPlayVerifyCode());
            if (mStreamInfo.getEncrypt() == 1) {
                Log.i(TAG, "验证码：" + mStreamInfo.getPlayVerifyCode());
                mEZPlayer.setPlayVerifyCode(mStreamInfo.getPlayVerifyCode());
            }
//            if (mStreamInfo.getEncrypt() == 1) {
//                mEZPlayer.setPlayVerifyCode(mStreamInfo.getPlayVerifyCode());
//            }
            if (mHolder != null) {
//            playEmpty.setVisibility(View.INVISIBLE);
                mEZPlayer.setHandler(mHandler);
                mEZPlayer.setSurfaceHold(mHolder);
                mEZPlayer.startRealPlay();
                shoundSwitch();
                talkSwitch();
//            if (streamInfo.getEncrypt() == 1) {
////            Log.i(TAG,"需要输入验证码");
//            mEZPlayer.setPlayVerifyCode("YUWKPG");
//                mEZPlayer.startRealPlay();
//            }
            }
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        isStop = true;
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
            if (m_wklk!=null && lockWake)
            m_wklk.release(); //解除保持唤醒
        }
        super.onStop();
    }

    @Override
    public void onSuccessUrl(StreamInfo streamInfo) {
        mStreamInfo = streamInfo;
        if (mCurrentQulityMode.getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_FLUNET.getVideoLevel()) {
            realplayQualityBtn.setText(R.string.quality_flunet);
            realplayFullQualityBtn.setText(R.string.quality_flunet);
        } else if (mCurrentQulityMode.getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_BALANCED.getVideoLevel()) {
            realplayQualityBtn.setText(R.string.quality_balanced);
            realplayFullQualityBtn.setText(R.string.quality_balanced);
        } else if (mCurrentQulityMode.getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_HD.getVideoLevel()) {
            realplayQualityBtn.setText(R.string.quality_hd);
            realplayFullQualityBtn.setText(R.string.quality_hd);
        }
//        Toast.makeText(MainActivity2.this, "获取摄像头地址成功", Toast.LENGTH_SHORT).show();
        String deviceSerial = streamInfo.getEzCameraInfo().getDeviceSerial();
        int cameraNo = streamInfo.getEzCameraInfo().getCameraNo();
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
            mEZPlayer.release();
            mEZPlayer = null;
            if (m_wklk!=null && lockWake) {
                m_wklk.release(); //解除保持唤醒
                lockWake=false;
            }
        }
        mEZPlayer = EZOpenSDK.getInstance().createPlayer(deviceSerial, cameraNo);
        Log.i(TAG, "序列号：" + deviceSerial);
        Log.i(TAG, "通道号：" + cameraNo);
        Log.i(TAG, "是否被加密：streamInfo.getEncrypt()=" + streamInfo.getEncrypt());
        Log.i(TAG, "验证码：" + streamInfo.getPlayVerifyCode());
        if (streamInfo.getEncrypt() == 1) {
            Log.i(TAG, "验证码：" + streamInfo.getPlayVerifyCode());
            mEZPlayer.setPlayVerifyCode(streamInfo.getPlayVerifyCode());
        }
        if (mHolder != null) {
//            playEmpty.setVisibility(View.INVISIBLE);
            mEZPlayer.setHandler(mHandler);
            mEZPlayer.setSurfaceHold(mHolder);
            mEZPlayer.startRealPlay();
//            if (streamInfo.getEncrypt() == 1) {
////            Log.i(TAG,"需要输入验证码");
//            mEZPlayer.setPlayVerifyCode("YUWKPG");
//                mEZPlayer.startRealPlay();
//            }
        }
    }

    @Override
    public void onErrorUrl() {
        sethide();
        setLoadingFail("与服务器连接异常");
        Toast.makeText(MainActivity2.this, "获取摄像头地址失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessCameras(Cameras cameras) {
        swipeRefreshLayout.setRefreshing(false);
        if (cameras != null && cameras.getDatas().size() != 0) {
            playEmpty.setVisibility(View.INVISIBLE);
            cameraCount.setText("共" + cameras.getDatas().size() + "块屏");
            mAdapter.setCameras(cameras);
            Cameras.DatasBean bean = cameras.getDatas().get(0);
            if (bean.getCaremaID() != null && !bean.getCaremaID().equals("")) {
                sethide();
                mMainPresenter.getCameraPlayUrl(getLoginUserName(), bean.getCaremaID(), bean.getPlayVerifyCode());
                currCamera.setText(bean.getName());
            } else {
                Toast.makeText(MainActivity2.this, "摄像头ID为空，该终端未绑定摄像头", Toast.LENGTH_SHORT).show();
            }
        } else {
            //为空
            playEmpty.setVisibility(View.VISIBLE);
            Toast.makeText(this, "此账号尚无设备,尝试刷新", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorCameras() {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(MainActivity2.this, "获取摄像头列表失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getLoginUserName() {
        return mMainPresenter.getLoginUser();
    }

    @Override
    public void onUpdate(final UpdateInfo info) {
        exitBroadcast = new ExitBroadcast();
        IntentFilter intentFilter = new IntentFilter("donot.pressed.update");
        registerReceiver(exitBroadcast, intentFilter);
        new AppUpdater.Builder(MainActivity2.this).content("摄像头监控v" + info.getVersion())
                .url(info.getUrl())
                .force(true)
                .app("摄像头监控")
                .title("发现新版本")
                .build()
                .update();
    }

    @Override
    protected void onDestroy() {
        if (exitBroadcast != null) {
            unregisterReceiver(exitBroadcast);
        }
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
            mEZPlayer.closeSound();
            mEZPlayer.release();
            if (m_wklk!=null && lockWake) {
                m_wklk.release(); //解除保持唤醒
                lockWake=false;
            }
        }
        super.onDestroy();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (v.getId() == R.id.bt_up) {
                    mMainPresenter.controlCamera(0, 0);
                } else if (v.getId() == R.id.bt_down) {
                    mMainPresenter.controlCamera(1, 0);
                } else if (v.getId() == R.id.bt_left) {
                    mMainPresenter.controlCamera(2, 0);
                } else if (v.getId() == R.id.bt_right) {
                    mMainPresenter.controlCamera(3, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (v.getId() == R.id.bt_up) {
                    mMainPresenter.controlCamera(0, 0);
                } else if (v.getId() == R.id.bt_down) {
                    mMainPresenter.controlCamera(1, 0);
                } else if (v.getId() == R.id.bt_left) {
                    mMainPresenter.controlCamera(2, 0);
                } else if (v.getId() == R.id.bt_right) {
                    mMainPresenter.controlCamera(3, 0);
                }
                break;
        }
        return false;
    }

    @OnClick(R2.id.header_layout)
    public void onViewClicked() {
        Intent intentModify = new Intent(MainActivity2.this, PersonalSettingActivity.class);
        startActivity(intentModify);
    }
    @OnClick(R2.id.draw_switch)
    public void onViewClicked1() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }
    @OnClick(R2.id.next_camera)
    public void onViewClicked2() {
        resetSound();
        resetTalk();
        nextCamera();
    }
    @OnClick(R2.id.bt_exit)
    public void onViewClicked3() {
        mMainPresenter.close();
        PushManager.getInstance().exit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R2.id.realplay_play_btn)
    public void onViewClicked4() {
        if (mEZPlayer==null)
            return;
        if (!playSuccess) {
            return;
        }
        if (isPlaying)
            isPlaying = false;
        else
            isPlaying = true;
        playSwitch();
    }
    @OnClick(R2.id.realplay_full_play_btn)
    public void onViewClicked5() {
        if (mEZPlayer==null)
            return;
        if (!playSuccess) {
            return;
        }
        if (isPlaying)
            isPlaying = false;
        else
            isPlaying = true;
        playSwitch();
    }
    @OnClick(R2.id.realplay_sound_btn)
    public void onViewClicked6() {
        if (!playSuccess) {
            return;
        }
        if (mEZPlayer == null)
            return;
        if (mIsShound)
            mIsShound = false;
        else
            mIsShound = true;
        shoundSwitch();
    }
    @OnClick(R2.id.realplay_full_sound_btn)
    public void onViewClicked7() {
        if (!playSuccess) {
            return;
        }
        if (mEZPlayer == null)
            return;
        if (mIsShound)
            mIsShound = false;
        else
            mIsShound = true;
        shoundSwitch();
    }
    @OnClick(R2.id.realplay_talk_btn)
    public void onViewClicked8() {
        if (!playSuccess) {
            return;
        }
        if (isTalking)
            isTalking = false;
        else
            isTalking = true;
        talkSwitch();
    }
    @OnClick(R2.id.realplay_full_talk_btn)
    public void onViewClicked9() {
        if (!playSuccess) {
            return;
        }
        if (isTalking)
            isTalking = false;
        else
            isTalking = true;
        talkSwitch();
    }
    @OnClick(R2.id.realplay_record_btn)
    public void onViewClicked10() {
        recordSwitch();
    }
    @OnClick(R2.id.realplay_full_record_btn)
    public void onViewClicked11() {
        recordSwitch();
    }
    @OnClick(R2.id.realplay_quality_btn)
    public void onViewClicked12(View view) {
        openQualityPopupWindow(view);
    }
    @OnClick(R2.id.realplay_full_quality_btn)
    public void onViewClicked13(View view) {
        openQualityPopupWindow(view);
    }
    @OnClick(R2.id.back)
    public void onViewClicked14() {
        if (mOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    @OnClick(R2.id.fullscreen_button)
    public void onViewClicked15() {
        if (mOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

//
//    @OnClick({R2.id.header_layout,R2.id.draw_switch, R2.id.next_camera, R2.id.bt_exit, R2.id.realplay_play_btn, R2.id.realplay_sound_btn,
//            R2.id.realplay_talk_btn, R2.id.realplay_record_btn, R2.id.realplay_quality_btn, R2.id.fullscreen_button, R2.id.gotoportrait,
//            R2.id.realplay_full_play_btn, R2.id.realplay_full_sound_btn, R2.id.realplay_full_talk_btn, R2.id.realplay_full_record_btn,
//            R2.id.realplay_full_quality_btn, R2.id.back})
//    public void onClick(View view) {
//    }

    private void nextCamera() {
        if (mAdapter.getCount() == 0) {
            Toast.makeText(MainActivity2.this, "此账号尚无设备", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
            mEZPlayer.release();
            mEZPlayer = null;
            if (m_wklk!=null && lockWake) {
                m_wklk.release(); //解除保持唤醒
                lockWake=false;
            }

        }
        mAdapter.setCurrPosition(((mAdapter.getCurrPosition() + 1)));
        Log.i(TAG, mAdapter.getCurrPosition() + "");
        Cameras.DatasBean camera = mAdapter.getNextCameraIdToPosition();
        String caremaID = camera.getCaremaID();
        currCamera.setText(camera.getName());
        if (caremaID != null && !caremaID.equals("")) {
            sethide();
            mMainPresenter.getCameraPlayUrl(getLoginUserName(), caremaID, camera.getPlayVerifyCode());
        } else {
            Toast.makeText(MainActivity2.this, "此终端未绑定摄像头", Toast.LENGTH_SHORT).show();
        }
    }

    private void playSwitch() {
//        Log.i(TAG, "playSwitch: -----------------"+isPlaying);
        if (isPlaying) {
            realplayPlayBtn.setSelected(true);
            realplayFullPlayBtn.setSelected(true);
            mEZPlayer.startRealPlay();
            loading.setVisibility(View.VISIBLE);
        } else {
            realplayPlayBtn.setSelected(false);
            realplayFullPlayBtn.setSelected(false);
            mEZPlayer.stopRealPlay();
            if (m_wklk!=null && lockWake) {
                m_wklk.release(); //解除保持唤醒
                lockWake=false;
            }
        }
        shoundSwitch();
        talkSwitch();
    }

    private void recordSwitch() {
        if (!playSuccess) {
            return;
        }
        if (isRecording)
            isRecording = false;
        else
            isRecording = true;
        if (isRecording) {
            Toast.makeText(this, "开始录像", Toast.LENGTH_SHORT).show();
            realplayRecordBtn.setSelected(true);
            realplayFullRecordBtn.setSelected(true);
            onRecordBtnClick();
        } else {
            Toast.makeText(this, "停止录像", Toast.LENGTH_SHORT).show();
            realplayRecordBtn.setSelected(false);
            realplayFullRecordBtn.setSelected(false);
            stopRealPlayRecord();
        }
        return;
    }

    private void talkSwitch() {
//        Log.i(TAG, "talkSwitch: ------------------"+isTalking);
        if (isTalking) {
            realplayTalkBtn.setSelected(true);
            realplayFullTalkBtn.setSelected(true);
            mEZPlayer.closeSound();
            mIsShound=false;
            mEZPlayer.startVoiceTalk();
        } else {
            realplayTalkBtn.setSelected(false);
            realplayFullTalkBtn.setSelected(false);
            mEZPlayer.stopVoiceTalk();
        }
    }

    private void shoundSwitch() {
//        Log.i(TAG, "shoundSwitch: ------------------"+mIsShound);
      mHandler.postDelayed(new Runnable() {
          @Override
          public void run() {
              if (mIsShound) {
                  mEZPlayer.openSound();
                  realplaySoundBtn.setSelected(true);
                  realplayFullSoundBtn.setSelected(true);
              } else {
                  mEZPlayer.closeSound();
                  realplaySoundBtn.setSelected(false);
                  realplayFullSoundBtn.setSelected(false);
              }
          }
      },500);

    }

    private void resetTalk(){
        realplayTalkBtn.setBackgroundResource(R.drawable.speech_btn_selector);
        realplayFullTalkBtn.setBackgroundResource(R.drawable.fullsrceen_speech_btn_selector);
        isTalking=false;
    }
    private void resetSound() {
        realplaySoundBtn.setBackgroundResource(R.drawable.soundon_btn_selector);
        realplayFullSoundBtn.setBackgroundResource(R.drawable.fullsrceen_soundon_btn_selector);
        mIsShound = false;
    }

    /**
     * 重新读取进行播放
     */
    private void reGetDataBeanToStart() {
        Cameras.DatasBean datasBean = mAdapter.getcurrCameraId();
        if (datasBean!=null) {
            if (datasBean.getCaremaID() != null && !datasBean.getCaremaID().equals("")) {
                if (mEZPlayer != null) {
                    mEZPlayer.stopRealPlay();
                    mEZPlayer.release();
                    mEZPlayer = null;
                    if (m_wklk != null && lockWake) {
                        m_wklk.release(); //解除保持唤醒
                        lockWake = false;
                    }
                }
                mMainPresenter.getCameraPlayUrl(getLoginUserName(), datasBean.getCaremaID(), datasBean.getPlayVerifyCode());
                resetSound();
                resetTalk();
                sethide();
            }
        }
    }

    /**
     * 开始录像
     */
    private void onRecordBtnClick() {

        if (!SDCardUtil.isSDCardUseable()) {
            // 提示SD卡不可用
            Utils.showToast(MainActivity2.this, "SD卡不可用");
            return;
        }

        if (SDCardUtil.getSDCardRemainSize() < SDCardUtil.PIC_MIN_MEM_SPACE) {
            // 提示内存不足
            Utils.showToast(MainActivity2.this, "内存不足");
            return;
        }

        if (mEZPlayer != null) {
            // 可以采用deviceSerial+时间作为文件命名，demo中简化，只用时间命名
            Date date = new Date();
            String strRecordFile = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/Records/" + String.format("%tY", date)
                    + String.format("%tm", date) + String.format("%td", date) + "/"
                    + String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) + ".mp4";

            Log.i(TAG, "onRecordBtnClick: -----------------录像存放的路径：" + strRecordFile);
            if (mEZPlayer.startLocalRecordWithFile(strRecordFile)) {
                Log.i(TAG, "onRecordBtnClick: ----------------------录像开始成功");
            } else {
                Log.i(TAG, "onRecordBtnClick: ----------------------录像开始失败");
            }
        }
    }

    /**
     * 停止录像
     *
     * @see
     * @since V1.0
     */
    private void stopRealPlayRecord() {
//        if (mEZPlayer == null || !mIsRecording) {
//            return;
//        }
        Toast.makeText(MainActivity2.this, "停止录像,已保存", Toast.LENGTH_SHORT).show();
        mEZPlayer.stopLocalRecord();

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
            isTalking=false;
            if (m_wklk!=null && lockWake) {
                m_wklk.release(); //解除保持唤醒
                lockWake=false;
            }
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
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            fullScreen(false);
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            fullScreen(true);
        }

    }

    private void fullScreen(boolean enable) {
        if (enable) {
            header.setVisibility(View.GONE);
            downLayout.setVisibility(View.GONE);
            realplayControlRl.setVisibility(View.GONE);
            realplayFullCtrLayout.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            if (isPlaying) {
                ptzTip.setVisibility(View.VISIBLE);
            }
            landscape();
        } else {
            ptzTip.setVisibility(View.GONE);
            header.setVisibility(View.VISIBLE);
            downLayout.setVisibility(View.VISIBLE);
            realplayControlRl.setVisibility(View.VISIBLE);
            realplayFullCtrLayout.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
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
        playLayout.setLayoutParams(mLayoutParams);// 使设置好的布局参数应用到控件

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
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams(); // 取控件当前的布局参数
        layoutParams1.height = height;//设置控件的高度
        layoutParams1.width = width;
        mSurfaceView.setLayoutParams(layoutParams1);// 使设置好的布局参数应用到控件
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playLayout.getLayoutParams(); // 取控件当前的布局参数
        layoutParams.height = height;//设置控件的高度
        layoutParams.width = width;
        playLayout.setLayoutParams(layoutParams);// 使设置好的布局参数应用到控件
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

    float minMove = 120;         //最小滑动距离
    float minVelocity = 0;      //最小滑动速度
    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (!isPlaying){
                        return false;
                    }
                    float beginX = e1.getX();
                    float endX = e2.getX();
                    float beginY = e1.getY();
                    float endY = e2.getY();
                    EZConstants.EZPTZCommand command = null;
                    if (beginX - endX > minMove && Math.abs(velocityX) > minVelocity) {   //左滑
                        command = EZConstants.EZPTZCommand.EZPTZCommandLeft;
                    } else if (endX - beginX > minMove && Math.abs(velocityX) > minVelocity) {
                        //右滑
                        command = EZConstants.EZPTZCommand.EZPTZCommandRight;
                    } else if (beginY - endY > minMove && Math.abs(velocityY) > minVelocity) {
                        //上滑
                        command = EZConstants.EZPTZCommand.EZPTZCommandUp;
                    } else if (endY - beginY > minMove && Math.abs(velocityY) > minVelocity) {
                        //下滑
                        command = EZConstants.EZPTZCommand.EZPTZCommandDown;
                    }
                    if (command==null){
                        return false;
                    }
                    ptzOption(command, EZConstants.EZPTZAction.EZPTZActionSTART);
                    final EZConstants.EZPTZCommand fCommand = command;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ptzOption(fCommand, EZConstants.EZPTZAction.EZPTZActionSTOP);
                        }
                    }, 1000);
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            };

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mEZPlayer != null) {
            mEZPlayer.setSurfaceHold(surfaceHolder);
        }
        this.mHolder = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    /**
     * 云台操作
     *
     * @param command ptz控制命令
     * @param action  控制启动/停止
     */
    public void ptzOption(final EZConstants.EZPTZCommand command, final EZConstants.EZPTZAction action) {
        if (mStreamInfo == null) {
            return;
        }
        es.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean b = EZOpenSDK.getInstance().controlPTZ(mStreamInfo.getEzCameraInfo().getDeviceSerial(), mStreamInfo.getEzCameraInfo().getCameraNo(), command,
                            action, EZConstants.PTZ_SPEED_DEFAULT);
                    Log.i(TAG, "操作" + (b ? "成功" : "失败"));
                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onCtrl(int direction, int state) {
        Log.i(TAG, "方向：" + direction + "状态：" + state);
        EZConstants.EZPTZCommand command = null;
        switch (direction) {
            case 0:
                command = EZConstants.EZPTZCommand.EZPTZCommandUp;
                break;
            case 1:
                command = EZConstants.EZPTZCommand.EZPTZCommandDown;
                break;
            case 2:
                command = EZConstants.EZPTZCommand.EZPTZCommandLeft;
                break;
            case 3:
                command = EZConstants.EZPTZCommand.EZPTZCommandRight;
                break;
        }
        ptzOption(command, state == 0 ? EZConstants.EZPTZAction.EZPTZActionSTART : EZConstants.EZPTZAction.EZPTZActionSTOP);
    }

    private void updateRealPlayFailUI(int errorCode) {
        String txt = null;
        LogUtil.i(TAG, "updateRealPlayFailUI: errorCode:" + errorCode);
        // 判断返回的错误码
        switch (errorCode) {
            case ErrorCode.ERROR_TRANSF_ACCESSTOKEN_ERROR:
                txt = "播放异常";
                return;
            case ErrorCode.ERROR_CAS_MSG_PU_NO_RESOURCE:
                txt = "播放异常";
                break;
            case ErrorCode.ERROR_TRANSF_DEVICE_OFFLINE:
                txt = "设备不在线";
                break;
            case ErrorCode.ERROR_INNER_STREAM_TIMEOUT:
                txt = "网络连接超时";
                break;
            case ErrorCode.ERROR_WEB_CODE_ERROR:
                //VerifySmsCodeUtil.openSmsVerifyDialog(Constant.SMS_VERIFY_LOGIN, this, this);
                //txt = Utils.getErrorTip(this, R.string.check_feature_code_fail, errorCode);
                break;
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_OP_ERROR:
                //VerifySmsCodeUtil.openSmsVerifyDialog(Constant.SMS_VERIFY_HARDWARE, this, null);
//                SecureValidate.secureValidateDialog(this, this);
                //txt = Utils.getErrorTip(this, R.string.check_feature_code_fail, errorCode);
                break;
            case ErrorCode.ERROR_TRANSF_TERMINAL_BINDING:
                txt = "请在萤石客户端关闭终端绑定";
                break;
            // 收到这两个错误码，可以弹出对话框，让用户输入密码后，重新取流预览
            case ErrorCode.ERROR_INNER_VERIFYCODE_NEED:
            case ErrorCode.ERROR_INNER_VERIFYCODE_ERROR: {
                txt = "摄像头被加密";
            }
            break;
            case ErrorCode.ERROR_EXTRA_SQUARE_NO_SHARING:
            default:
                break;
        }
        setLoadingFail(txt);
    }

    public void setLoadingFail(String errorStr) {
        realplayLoadingRl.setVisibility(View.VISIBLE);
        realplayTipTv.setText(errorStr);
    }

    public void sethide() {
        realplayLoadingRl.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }


    /**
     * 码流配置 清晰度 2-高清，1-标清，0-流畅
     *
     * @see
     * @since V2.0
     */
    private void setQualityMode(final EZConstants.EZVideoLevel mode) {
        // 检查网络是否可用
        if (!ConnectionDetector.isNetworkAvailable(this)) {
            // 提示没有连接网络
            Utils.showToast(this, R.string.realplay_set_fail_network);
            return;
        }

        if (mEZPlayer != null) {
            mWaitDialog.setWaitText(this.getString(R.string.setting_video_level));
            mWaitDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // need to modify by yudan at 08-11
                        boolean b = EZOpenSDK.getInstance().setVideoLevel(mStreamInfo.getEzCameraInfo().getDeviceSerial(), mStreamInfo.getEzCameraInfo().getCameraNo(), mode.getVideoLevel());
                        if (b) {
                            Log.i(TAG, "清晰度设置成功");
                            mCurrentQulityMode = mode;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    closeQualityPopupWindow();
                                    setVideoLevel();
                                    if (mEZPlayer != null) {
                                        mEZPlayer.stopRealPlay();
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mEZPlayer.setHandler(mHandler);
                                                mEZPlayer.setSurfaceHold(mHolder);
                                                mEZPlayer.startRealPlay();
                                                try {
                                                    mWaitDialog.setWaitText(null);
                                                    mWaitDialog.dismiss();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, 500);
                                    }
                                }
                            });
                        } else {
                            Log.i(TAG, "清晰度设置失败");
                        }

                    } catch (BaseException e) {
                        mCurrentQulityMode = EZConstants.EZVideoLevel.VIDEO_LEVEL_FLUNET;
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    private View.OnClickListener mOnPopWndClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.quality_hd_btn) {
                setQualityMode(EZConstants.EZVideoLevel.VIDEO_LEVEL_HD);

            } else if (i == R.id.quality_balanced_btn) {
                setQualityMode(EZConstants.EZVideoLevel.VIDEO_LEVEL_BALANCED);

            } else if (i == R.id.quality_flunet_btn) {
                setQualityMode(EZConstants.EZVideoLevel.VIDEO_LEVEL_FLUNET);

            } else {
            }
        }
    };

    private void openQualityPopupWindow(View anchor) {
        if (!playSuccess) {
            return;
        }
        if (mEZPlayer == null) {
            return;
        }
        closeQualityPopupWindow();
        LayoutInflater layoutInflater = getLayoutInflater();
        ViewGroup layoutView = (ViewGroup) layoutInflater.inflate(R.layout.realplay_quality_items, null, true);

        Button qualityHdBtn = (Button) layoutView.findViewById(R.id.quality_hd_btn);
        qualityHdBtn.setOnClickListener(mOnPopWndClickListener);
        Button qualityBalancedBtn = (Button) layoutView.findViewById(R.id.quality_balanced_btn);
        qualityBalancedBtn.setOnClickListener(mOnPopWndClickListener);
        Button qualityFlunetBtn = (Button) layoutView.findViewById(R.id.quality_flunet_btn);
        qualityFlunetBtn.setOnClickListener(mOnPopWndClickListener);

        // 视频质量，2-高清，1-标清，0-流畅
        if (mStreamInfo.getEzCameraInfo().getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_FLUNET) {
            qualityFlunetBtn.setEnabled(false);
        } else if (mStreamInfo.getEzCameraInfo().getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_BALANCED) {
            qualityBalancedBtn.setEnabled(false);
        } else if (mStreamInfo.getEzCameraInfo().getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_HD) {
            qualityHdBtn.setEnabled(false);
        }

        int height = 105;

        qualityFlunetBtn.setVisibility(View.VISIBLE);
        qualityBalancedBtn.setVisibility(View.VISIBLE);
        qualityHdBtn.setVisibility(View.VISIBLE);

        height = Utils.dip2px(this, height);
        mQualityPopupWindow = new PopupWindow(layoutView, RelativeLayout.LayoutParams.WRAP_CONTENT, height, true);
        mQualityPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mQualityPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                LogUtil.infoLog(TAG, "KEYCODE_BACK DOWN");
                mQualityPopupWindow = null;
                closeQualityPopupWindow();
            }
        });
        try {
            mQualityPopupWindow.showAsDropDown(anchor, -Utils.dip2px(this, 5),
                    -(height + anchor.getHeight() + Utils.dip2px(this, 8)));
        } catch (Exception e) {
            e.printStackTrace();
            closeQualityPopupWindow();
        }
    }

    private void closeQualityPopupWindow() {
        if (mQualityPopupWindow != null) {
            dismissPopWindow(mQualityPopupWindow);
            mQualityPopupWindow = null;
        }
    }

    private void dismissPopWindow(PopupWindow popupWindow) {
        if (popupWindow != null && !isFinishing()) {
            try {
                popupWindow.dismiss();
            } catch (Exception e) {
            }
        }
    }

    private void setVideoLevel() {
        if (mStreamInfo.getEzCameraInfo() == null || mEZPlayer == null) {
            return;
        }
        mStreamInfo.getEzCameraInfo().setVideoLevel(mCurrentQulityMode.getVideoLevel());
        // 视频质量，2-高清，1-标清，0-流畅
        if (mCurrentQulityMode.getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_FLUNET.getVideoLevel()) {
            realplayQualityBtn.setText(R.string.quality_flunet);
            realplayFullQualityBtn.setText(R.string.quality_flunet);
        } else if (mCurrentQulityMode.getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_BALANCED.getVideoLevel()) {
            realplayQualityBtn.setText(R.string.quality_balanced);
            realplayFullQualityBtn.setText(R.string.quality_balanced);
        } else if (mCurrentQulityMode.getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_HD.getVideoLevel()) {
            realplayQualityBtn.setText(R.string.quality_hd);
            realplayFullQualityBtn.setText(R.string.quality_hd);
        }
    }


    @Override
    public void onBackPressed() {
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            portrait();
            return;
        }
        finish();
    }

    class ExitBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

}
