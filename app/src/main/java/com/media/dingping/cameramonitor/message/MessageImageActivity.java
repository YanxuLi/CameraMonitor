package com.media.dingping.cameramonitor.message;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.media.dingping.cameramonitor.PushServer;
import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.R2;
import com.media.dingping.cameramonitor.bean.AlarmType;
import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.playback.EZUtils;
import com.media.dingping.cameramonitor.playback.VerifyCodeInput;
import com.videogo.alarm.AlarmLogInfoManager;
import com.videogo.constant.IntentConsts;
import com.videogo.openapi.bean.EZAlarmInfo;
import com.videogo.util.LocalInfo;
import com.videogo.util.LogUtil;
import com.videogo.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 消息详情
 */
public class MessageImageActivity extends Activity implements VerifyCodeInput.VerifyCodeErrorListener {

    private static final long HIDE_BAR_DELAY = 2000;
    private static final int MSG_HIDE_BAR = 1;
    public static final int ERROR_WEB_NO_ERROR = 100000; // /< 没有错误
    public static final int ERROR_WEB_NO_DATA = 100000 - 2; // /< 数据为空或不存在
    @BindView(R2.id.goback)
    ImageButton goback;
    @BindView(R2.id.alarm_image)
    ImageView mAlarmImageView;
    /** 底部栏——消息类型 */
    @BindView(R2.id.message_type)
    TextView mMessageTypeView;
    /** 底部栏——消息时间 */
    @BindView(R2.id.message_time)
    TextView mMessageTimeView;
    /** 底部栏——消息来源 */
    @BindView(R2.id.message_from)
    TextView mMessageFromView;
    /** 底部栏——消息录像 */
    @BindView(R2.id.video_button)
    Button mVideoButton;
    /** 底部栏 */
    @BindView(R2.id.bottom_bar)
    RelativeLayout mBottomBar;

    private LocalInfo mLocalInfo;

    /**
     * 报警消息管理
     */
    private AlarmLogInfoManager mAlarmLogInfoManager;
    private EZAlarmInfo mEZAlarmInfo;
    private String mDevideVerifyCode;
    private Cameras.DatasBean mCameraInfo;
    private int mFromNotication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 页面统计
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_image_page);
        ButterKnife.bind(this);
        initData();
        initViews();
//        setListner();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mAlarmLogInfoManager = AlarmLogInfoManager.getInstance();

        mEZAlarmInfo = getIntent().getParcelableExtra(IntentConsts.EXTRA_ALARM_INFO);
        mDevideVerifyCode=getIntent().getStringExtra("devideVerifyCode");
        mCameraInfo = (Cameras.DatasBean) getIntent().getExtras().getSerializable("cameraInfo");
        mFromNotication = getIntent().getIntExtra("mAlarm_notication_Count",-1);
        if (mFromNotication!=-1)
            PushServer.mAlarm_notication_Count=0;

        if (mEZAlarmInfo == null) {
            LogUtil.debugLog("MessageImageActivity", "------------------mEZAlarmInfo is null");
            finish();
            return;
        }
        mLocalInfo = LocalInfo.getInstance();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        setupAlarmInfo(mEZAlarmInfo);
        setAlarmImage();
    }

    private void setAlarmImage() {
//        EZUtils.loadImage(this, mAlarmImageView, mEZAlarmInfo.getAlarmPicUrl(), mEZAlarmInfo.getDeviceSerial(), mDevideVerifyCode, this);
        EZUtils.loadImage(this, mAlarmImageView, mEZAlarmInfo.getAlarmPicUrl(), mCameraInfo.getCaremaID(), mDevideVerifyCode, this);
    }

    @Override
    public void verifyCodeError() {
    }


    @OnClick(R2.id.video_button)
    public void onViewClicked1() {
        Intent intent = new Intent(MessageImageActivity.this, MessageDetailPlayRecordActivity.class);
        intent.putExtra(IntentConsts.EXTRA_ALARM_INFO, mEZAlarmInfo);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cameraInfo",mCameraInfo);
        intent.putExtras(bundle);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    @OnClick(R2.id.goback)
    public void onViewClicked2() {
        finish();
    }

//    /**
//     * 设置监听
//     */
//    @SuppressLint("ClickableViewAccessibility")
//    private void setListner() {
//        OnClickListener clickListener = new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.video_button:
//
//                        break;
//                    case R.id.goback:
//                        finish();
//                        break;
//                }
//            }
//
//        };
//
//        goback.setOnClickListener(clickListener);
//        mVideoButton.setOnClickListener(clickListener);
//        mBottomBar.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//    }

    private void setupAlarmInfo(EZAlarmInfo alarm) {
        // 消息类型
        AlarmType alarmType = AlarmType.BODY_ALARM;//alarm.getEnumAlarmType();
        mMessageTypeView.setText(getString(alarmType.getTextResId()));
        // 消息来源
        mMessageFromView.setText("来自" + alarm.getAlarmName());
        // 消息时间
        mMessageTimeView.setText(alarm.getAlarmStartTime());

        setButtonEnable(alarm);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            // 消息底部栏变化
            LayoutParams layoutParams = (LayoutParams) mMessageTypeView.getLayoutParams();

            layoutParams = (LayoutParams) mMessageTimeView.getLayoutParams();
            layoutParams.topMargin = 0;
            layoutParams.leftMargin = Utils.dip2px(this, 15);
            layoutParams.width = LayoutParams.WRAP_CONTENT;
            layoutParams.addRule(RelativeLayout.BELOW, 0);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.message_type);

            layoutParams = (LayoutParams) mMessageFromView.getLayoutParams();
            layoutParams.topMargin = 0;
            layoutParams.leftMargin = Utils.dip2px(this, 15);
            layoutParams.width = LayoutParams.WRAP_CONTENT;
            layoutParams.addRule(RelativeLayout.BELOW, 0);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.message_time);
            mMessageFromView.setSingleLine(true);
            mMessageFromView.setEllipsize(TruncateAt.END);

            layoutParams = (LayoutParams) mVideoButton.getLayoutParams();
            layoutParams.width = LayoutParams.WRAP_CONTENT;
            layoutParams.height = LayoutParams.WRAP_CONTENT;

            mBottomBar.setPadding(mBottomBar.getPaddingLeft(), mBottomBar.getPaddingTop(),
                    mBottomBar.getPaddingRight(), Utils.dip2px(this, 10));

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            // 消息底部栏变化
            LayoutParams layoutParams = (LayoutParams) mMessageTypeView.getLayoutParams();

            layoutParams = (LayoutParams) mMessageTimeView.getLayoutParams();
            layoutParams.topMargin = Utils.dip2px(this, 3);
            layoutParams.leftMargin = 0;
            layoutParams.width = LayoutParams.MATCH_PARENT;
            layoutParams.addRule(RelativeLayout.BELOW, R.id.message_type);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, 0);

            layoutParams = (LayoutParams) mMessageFromView.getLayoutParams();
            layoutParams.topMargin = Utils.dip2px(this, 3);
            layoutParams.leftMargin = 0;
            layoutParams.width = LayoutParams.MATCH_PARENT;
            layoutParams.addRule(RelativeLayout.BELOW, R.id.message_time);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, 0);
            mMessageFromView.setSingleLine(false);
            mMessageFromView.setEllipsize(null);

            mVideoButton.setBackgroundResource(R.drawable.login_btn_selector);
            layoutParams = (LayoutParams) mVideoButton.getLayoutParams();
            layoutParams.width = LayoutParams.MATCH_PARENT;
            layoutParams.height = Utils.dip2px(this, 39);

            mBottomBar.setPadding(mBottomBar.getPaddingLeft(), mBottomBar.getPaddingTop(),
                    mBottomBar.getPaddingRight(), Utils.dip2px(this, 30));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (mOpenService != null)
//            mOpenService.loadOnActivityResult(requestCode, resultCode, data);
    }

    private void setButtonEnable(EZAlarmInfo alarm) {
        AlarmType alarmType = AlarmType.BODY_ALARM;
//        AlarmLogInfo relAlarm = alarm.getRelationAlarms();

        mVideoButton.setEnabled(true);
    }

}