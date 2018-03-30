package com.media.dingping.cameramonitor.setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.media.dingping.cameramonitor.GCApplication;
import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.R2;
import com.media.dingping.cameramonitor.bean.DefencePlanTimes;
import com.media.dingping.cameramonitor.customview.WaitDialog;
import com.media.dingping.cameramonitor.http.HttpUtils;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.util.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/22 0022.
 * 设置
 */

public class SettingActivity extends Activity {

    @BindView(R2.id.tv_warmtype)
    TextView tvWarmtype;
    private String TAG = "SettingActivity";

    @BindView(R2.id.iscontrol)
    ImageButton isControl;
    @BindView(R2.id.defence_state)
    CheckBox defenceState;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private String mCaremaId;
    private String accessToken;
    private int mChannelNo = 0;

    private boolean isOpenRemend = true;
    private int isDefence = 0; //0:撤防  1:布防

    private EZDeviceInfo mDeviceInfo;
    private DefencePlanTimes mDefencePlanTimes;

    private int defenceWarmType=2;////0-短叫，1-长叫，2-静音

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    getSavedData();
                    setLastData();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        accessToken = EZOpenSDK.getInstance().getEZAccessToken().getAccessToken();
        mCaremaId = intent.getStringExtra("cameraId");
        Log.i(TAG, "onCreate: ------------------token=" + accessToken);
        Log.i(TAG, "onCreate: ------------------caremid=" + mCaremaId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mDeviceInfo = EZOpenSDK.getInstance().getDeviceInfo(mCaremaId);
                    isDefence = mDeviceInfo.getDefence();
                    List<EZCameraInfo> cameraInfoList = mDeviceInfo.getCameraInfoList();
                    if (cameraInfoList != null && cameraInfoList.size() > 0) {
                        final int cameraNo = cameraInfoList.get(0).getCameraNo();
                        mChannelNo = cameraNo;
                        Log.i(TAG, "onCreate: ------------------mChannelNo=" + mChannelNo);
                    }
                    String setTimes = HttpUtils.getPostMessage("https://open.ys7.com/api/lapp/device/defence/plan/get",
                            "accessToken=" + accessToken + "&deviceSerial=" + mCaremaId + "&channelNo=" + mChannelNo, "utf-8");
                    if (setTimes != null) {
                        mDefencePlanTimes = new Gson().fromJson(setTimes, DefencePlanTimes.class);
                        mHandler.sendEmptyMessage(101);
                        Log.i("-------", "mDefencePlanTimes.toString():"+mDefencePlanTimes.toString());
                    }
                    mHandler.sendEmptyMessage(100);
                } catch (BaseException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void getSavedData() {
        mSharedPreferences = getSharedPreferences("setting", 0);
        mEditor = mSharedPreferences.edit();
        isOpenRemend = mSharedPreferences.getBoolean("iscontrol", true);
        defenceWarmType=mSharedPreferences.getInt("type",2);
    }

    private void setLastData() {
        isControl.setSelected(isOpenRemend);
        defenceState.setSelected((isDefence == 1) ? true : false);
        if (defenceWarmType == 0) {
            tvWarmtype.setText("提醒");
        } else if (defenceWarmType == 1) {
            tvWarmtype.setText("报警");
        } else {
            tvWarmtype.setText("静音");
        }

    }

    @OnClick(R2.id.defence_state)
    public void onViewClicked() {
        if (isDefence == 1)
            isDefence = 0;
        else
            isDefence = 1;
        new SetDefenceOpenTask().execute(isDefence);
    }
    @OnClick(R2.id.goback)
    public void onViewClicked1() {
        finish();
    }
    @OnClick(R2.id.iscontrol)
    public void onViewClicked2() {
        if (isOpenRemend) {
            isOpenRemend = false;
            isControl.setSelected(false);
        } else {
            isOpenRemend = true;
            isControl.setSelected(true);
        }
        mEditor.putBoolean("iscontrol", isOpenRemend);
        mEditor.commit();
    }
    @OnClick(R2.id.setdefenceplan_rl)
    public void onViewClicked3() {
        Bundle bundle = new Bundle();
        Intent defencePlan = new Intent(SettingActivity.this, SettingDefencePlanActivity.class);
        defencePlan.putExtra("mCaremaId", mCaremaId);
        defencePlan.putExtra("accessToken", accessToken);
        defencePlan.putExtra("mChannelNo", mChannelNo);
        bundle.putSerializable("DefencePlanTimes",mDefencePlanTimes);
        defencePlan.putExtras(bundle);
        startActivity(defencePlan);
    }
    @OnClick(R2.id.setdefenctype_rl)
    public void onViewClicked4() {
        Intent defenceType = new Intent(SettingActivity.this, SettingDefenceTypeActivity.class);
        defenceType.putExtra("mCaremaId", mCaremaId);
        defenceType.putExtra("accessToken", accessToken);
        defenceType.putExtra("mChannelNo", mChannelNo);
        defenceType.putExtra("warm_type",tvWarmtype.getText().toString());
        startActivityForResult(defenceType, 1);
    }

//    @OnClick({R.id.defence_state, R.id.goback, R.id.iscontrol, R.id.setdefenceplan_rl, R.id.setdefenctype_rl})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.goback:
//                finish();
//                break;
//            case R.id.defence_state:
//                if (isDefence == 1)
//                    isDefence = 0;
//                else
//                    isDefence = 1;
//                new SetDefenceOpenTask().execute(isDefence);
////                new SetDefenceTask().execute(isDefence);
//                break;
//            case R.id.iscontrol://是否打开2G/3G/4G流量的提醒
//                if (isOpenRemend) {
//                    isOpenRemend = false;
//                    isControl.setSelected(false);
//                } else {
//                    isOpenRemend = true;
//                    isControl.setSelected(true);
//                }
//                mEditor.putBoolean("iscontrol", isOpenRemend);
//                mEditor.commit();
//                break;
//            case R.id.setdefenceplan_rl:
//                Bundle bundle = new Bundle();
//                Intent defencePlan = new Intent(SettingActivity.this, SettingDefencePlanActivity.class);
//                defencePlan.putExtra("mCaremaId", mCaremaId);
//                defencePlan.putExtra("accessToken", accessToken);
//                defencePlan.putExtra("mChannelNo", mChannelNo);
//                bundle.putSerializable("DefencePlanTimes",mDefencePlanTimes);
//                defencePlan.putExtras(bundle);
//                startActivity(defencePlan);
//                break;
//            case R.id.setdefenctype_rl:
//                Intent defenceType = new Intent(SettingActivity.this, SettingDefenceTypeActivity.class);
//                defenceType.putExtra("mCaremaId", mCaremaId);
//                defenceType.putExtra("accessToken", accessToken);
//                defenceType.putExtra("mChannelNo", mChannelNo);
//                defenceType.putExtra("warm_type",tvWarmtype.getText().toString());
//                startActivityForResult(defenceType, 1);
//                break;
//        }
//    }

    private class SetDefenceTask extends AsyncTask<Integer, Void, Boolean> {
        private Dialog mWaitDialog;
        private int mErrorCode = 0;
        int bSetDefence;
        boolean isSetDefence;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWaitDialog = new WaitDialog(SettingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
            mWaitDialog.setCancelable(false);
            mWaitDialog.show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            bSetDefence = (Integer) params[0];
            Boolean result = false;
            if (bSetDefence == 1)
                isSetDefence = true;
            else
                isSetDefence = false;

            try {
                if (result!=null) {
                    result = GCApplication.getOpenSDK().setDefence(mDeviceInfo.getDeviceSerial(), isSetDefence ? EZConstants.EZDefenceStatus.EZDefence_IPC_OPEN :
                            EZConstants.EZDefenceStatus.EZDefence_IPC_CLOSE);
                }
            } catch (BaseException e) {
                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                mErrorCode = errorInfo.errorCode;
                LogUtil.debugLog(TAG, errorInfo.toString());
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mWaitDialog.dismiss();
            if (result) {
                if (bSetDefence == 1) {
                    defenceState.setSelected(true);
                    mEditor.putBoolean("isOpenDefence", true);
                    Toast.makeText(SettingActivity.this, "设置提醒成功！", Toast.LENGTH_SHORT).show();
                } else {
                    defenceState.setSelected(false);
                    mEditor.putBoolean("isOpenDefence", false);
                    Toast.makeText(SettingActivity.this, "关闭提醒成功！", Toast.LENGTH_SHORT).show();
                }
                mEditor.commit();
            } else {
                Toast.makeText(SettingActivity.this, "设置提醒失败！", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 异步线程设置开关活动检测提醒
     */
    private class SetDefenceOpenTask extends AsyncTask<Integer, Void, Boolean> {
        int bSetDefence;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            bSetDefence = params[0];
            Boolean result = false;
            String results = HttpUtils.sendPostMessage("https://open.ys7.com/api/lapp/device/defence/set",
                    "accessToken=" + accessToken + "&deviceSerial=" + mCaremaId + "&isDefence=" + bSetDefence, "utf-8");

            if (results.contains("200"))
                result = true;
            else result = false;

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                if (bSetDefence == 1) {
                    defenceState.setSelected(true);
                    mEditor.putBoolean("isOpenDefence", true);
//                    Toast.makeText(SettingActivity.this, "设置提醒成功！", Toast.LENGTH_SHORT).show();
                } else {
                    defenceState.setSelected(false);
                    mEditor.putBoolean("isOpenDefence", false);
//                    Toast.makeText(SettingActivity.this, "关闭提醒成功！", Toast.LENGTH_SHORT).show();
                }
                mEditor.commit();
            } else {
                Toast.makeText(SettingActivity.this, "设置提醒失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data!=null) {
                    int type = data.getExtras().getInt("wram_type");
                    if (type == 0) {
                        tvWarmtype.setText("提醒");
                    } else if (type == 1) {
                        tvWarmtype.setText("报警");
                    } else {
                        tvWarmtype.setText("静音");
                    }
                }
                    break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
