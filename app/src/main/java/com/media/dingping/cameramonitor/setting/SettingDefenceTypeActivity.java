package com.media.dingping.cameramonitor.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.R2;
import com.media.dingping.cameramonitor.http.HttpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/10 0010.
 * 提醒声音类型
 */

public class SettingDefenceTypeActivity extends Activity {

    @BindView(R2.id.radiogroup)
    RadioGroup radiogroup;
    @BindView(R2.id.rbt_baojing)
    RadioButton rbtBaojing;
    @BindView(R2.id.rbt_tixing)
    RadioButton rbtTixing;
    @BindView(R2.id.rbt_jingyin)
    RadioButton rbtJingyin;
    @BindView(R2.id.cancel)
    ImageButton cancel;
    @BindView(R2.id.ok)
    ImageButton ok;

    private int defenceVoiceType = 0; //0-短叫，1-长叫，2-静音
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private String mCaremaId;
    private String accessToken;
    private Intent mIntent;
    private String warmType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_warmtype);
        ButterKnife.bind(this);

        mSharedPreferences = getSharedPreferences("setting",0);
        mEditor=mSharedPreferences.edit();

        mIntent = getIntent();
        mCaremaId = mIntent.getStringExtra("mCaremaId");
        accessToken = mIntent.getStringExtra("accessToken");
        warmType = mIntent.getStringExtra("warm_type");

        if (warmType.equals("静音")){
            defenceVoiceType=2;
            rbtJingyin.setChecked(true);
        }else if (warmType.equals("提醒")){
            defenceVoiceType=0;
            rbtTixing.setChecked(true);
        }else{
            defenceVoiceType=1;
            rbtBaojing.setChecked(true);
        }

        radiogroup.setOnCheckedChangeListener(new RadioGroupListener());
    }

    @OnClick(R2.id.cancel)
    public void onViewClicked() {
        finish();
    }
    @OnClick(R2.id.ok)
    public void onViewClicked1() {
        new SetDefenceTypeTask().execute(defenceVoiceType);
        mIntent.putExtra("wram_type",defenceVoiceType);
        this.setResult(1,mIntent);
        mEditor.putInt("type",defenceVoiceType);
        mEditor.commit();
        finish();
    }

//    @OnClick({R.id.cancel, R.id.ok})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.cancel:
//                finish();
//                break;
//            case R.id.ok:
//                new SetDefenceTypeTask().execute(defenceVoiceType);
//                mIntent.putExtra("wram_type",defenceVoiceType);
//                this.setResult(1,mIntent);
//                mEditor.putInt("type",defenceVoiceType);
//                mEditor.commit();
//                finish();
//                break;
//        }
//    }

    class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            if (checkedId == rbtBaojing.getId()) {//1
                defenceVoiceType = 1;
            } else if (checkedId == rbtTixing.getId()) {//0
                defenceVoiceType = 0;
            } else if (checkedId == rbtJingyin.getId()) {//2
                defenceVoiceType = 2;
            }
        }
    }

    /**
     * 设置活动检测的提醒声音类型
     */
    private class SetDefenceTypeTask extends AsyncTask<Integer, Void, Boolean> {
        Boolean result = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            int type = params[0];
            String results = HttpUtils.sendPostMessage("https://open.ys7.com/api/lapp/device/alarm/sound/set",
                    "accessToken=" + accessToken + "&deviceSerial=" + mCaremaId + "&type=" + type, "utf-8");

            if (results.contains("200"))
                result = true;
            else result = false;

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                switch (defenceVoiceType) {
                    case 0:
//                        mEditor.putInt("defenceVoiceType", 0);
//                        mEditor.commit();
//                        Toast.makeText(SettingDefenceTypeActivity.this, "设置提醒模式成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
//                        mEditor.putInt("defenceVoiceType", 1);
//                        mEditor.commit();
//                        Toast.makeText(SettingDefenceTypeActivity.this, "设置报警模式成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
//                        mEditor.putInt("defenceVoiceType", 2);
//                        mEditor.commit();
//                        Toast.makeText(SettingDefenceTypeActivity.this, "设置静音模式成功", Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                Toast.makeText(SettingDefenceTypeActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
