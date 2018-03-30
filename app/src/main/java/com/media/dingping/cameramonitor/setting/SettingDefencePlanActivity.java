package com.media.dingping.cameramonitor.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.R2;
import com.media.dingping.cameramonitor.bean.DefencePlanTimes;
import com.media.dingping.cameramonitor.customview.codbking.widget.DatePickDialog;
import com.media.dingping.cameramonitor.customview.codbking.widget.OnSureLisener;
import com.media.dingping.cameramonitor.customview.codbking.widget.bean.DateType;
import com.media.dingping.cameramonitor.http.HttpUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/10 0010.
 * 布防计划
 */

public class SettingDefencePlanActivity extends Activity {

    @BindView(R2.id.cancel)
    ImageButton cancel;
    @BindView(R2.id.ok)
    ImageButton ok;
    @BindView(R2.id.cb_defence_plan)
    CheckBox cbDefencePlan;
    @BindView(R2.id.defenceplan_layout)
    RelativeLayout defenceplanLayout;
    @BindView(R2.id.tv_starttime)
    TextView tvStarttime;
    @BindView(R2.id.rl_starttime)
    RelativeLayout rlStarttime;
    @BindView(R2.id.tv_endtime)
    TextView tvEndtime;
    @BindView(R2.id.rl_endtime)
    RelativeLayout rlEndtime;
    @BindView(R2.id.nextday)
    CheckBox cb_nextday;

    @BindView(R2.id.tv_doubletime)
    TextView tvDoubletime;
    @BindView(R2.id.rl_doubletime)
    RelativeLayout rlDoubletime;
    @BindView(R2.id.plantime_ll)
    LinearLayout plantimeLl;
    private int isSetDefencePlan = 0;//是否设置布防计划，0-不启用，1-启用
    private DatePickDialog mDialog_startime;
    private DatePickDialog mDialog_endtime;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private Date mStarDate;
    private Date mEndData;

    private DefencePlanTimes mDefencePlanTimes;

    private String mCaremaId;
    private String accessToken;
    private int mChannelNo = 0;
    private String mDoubleTime;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    if (mDefencePlanTimes!=null) {
                        tvStarttime.setText(mDefencePlanTimes.getData().getStartTime());
                        if (mDefencePlanTimes.getData().getStopTime().contains("n")){
                            cb_nextday.setChecked(true);
                            tvEndtime.setText(mDefencePlanTimes.getData().getStopTime().substring(1));
                        }else {
                            tvEndtime.setText(mDefencePlanTimes.getData().getStopTime());
                        }
                        tvDoubletime.setText(mDefencePlanTimes.getData().getPeriod());
                        if (mDefencePlanTimes.getData().getEnable() == 1) { //是否设置布防计划，0-不启用，1-启用
                            cbDefencePlan.setSelected(true);
                            plantimeLl.setVisibility(View.VISIBLE);
                        } else {
                            cbDefencePlan.setSelected(false);
                            plantimeLl.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_defenceplan);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mCaremaId=intent.getStringExtra("mCaremaId");
        accessToken=intent.getStringExtra("accessToken");
        mChannelNo=intent.getIntExtra("mChannelNo",1);
        getDataThread();
        setTimeDialog();
    }

    private void getDataThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                    String setTimes = HttpUtils.getPostMessage("https://open.ys7.com/api/lapp/device/defence/plan/get",
                            "accessToken=" + accessToken + "&deviceSerial=" + mCaremaId + "&channelNo=" + mChannelNo, "utf-8");
                    if (setTimes != null) {
                        mDefencePlanTimes = new Gson().fromJson(setTimes, DefencePlanTimes.class);
                        mHandler.sendEmptyMessage(101);
                    }
            }
        }).start();
    }
//    private void setChange() {
//        if (mDoubleTime != null) {
//            if (mDoubleTime.contains("0"))
//                doubleTime_inTextView += "一,";
//            if (mDoubleTime.contains("1"))
//                doubleTime_inTextView += "二,";
//            if (mDoubleTime.contains("2"))
//                doubleTime_inTextView += "三,";
//            if (mDoubleTime.contains("3"))
//                doubleTime_inTextView += "四,";
//            if (mDoubleTime.contains("4"))
//                doubleTime_inTextView += "五,";
//            if (mDoubleTime.contains("5"))
//                doubleTime_inTextView += "六,";
//            if (mDoubleTime.contains("6"))
//                doubleTime_inTextView += "日";
//            if (doubleTime_inTextView.endsWith(","))
//                doubleTime_inTextView = doubleTime_inTextView.substring(0, doubleTime_inTextView.length() - 1);
//            if (doubleTime_inTextView.equals("一,二,三,四,五,六,日"))
//                doubleTime_inTextView = "每天";
//            if (doubleTime_inTextView.equals("一,二,三,四,五"))
//                doubleTime_inTextView = "工作日";
//            if (doubleTime_inTextView.equals("六,日"))
//                doubleTime_inTextView = "周末";
//        } else {
//            doubleTime_inTextView = "一";
//        }
//    }

    private void setTimeDialog() {
        mDialog_startime = new DatePickDialog(this);
        mDialog_startime.setType(DateType.TYPE_HM);
        mDialog_startime.setOnSureLisener(new OnSureLisener() {
            @Override
            public void onSure(Date date) {
                tvStarttime.setText(format.format(date));
//                mEditor.putString("startTime", format.format(date));
//                mEditor.commit();
                if (isFinishing()) {
                    mDialog_startime.dismiss();
                }
            }
        });
        mDialog_endtime = new DatePickDialog(this);
        mDialog_endtime.setType(DateType.TYPE_HM);
        mDialog_endtime.setOnSureLisener(new OnSureLisener() {
            @Override
            public void onSure(Date date) {
                tvEndtime.setText(format.format(date));
//                mEditor.putString("endTime", format.format(date));
//                mEditor.commit();
                if (isFinishing()) {
                    mDialog_endtime.dismiss();
                }
            }
        });
    }

    @OnClick(R2.id.cancel)
    public void onViewClicked() {
        finish();
    }
    @OnClick(R2.id.ok)
    public void onViewClicked1() {
        try {
            if ((tvStarttime.getText().toString()!=null && !tvStarttime.getText().toString().equals(""))
                    && (tvEndtime.getText().toString()!=null && !tvEndtime.getText().toString().equals(""))) {
                if (cbDefencePlan.isSelected()) {
                    Calendar calendar= Calendar.getInstance();
                    int year=calendar.get(Calendar.YEAR);
                    int month=calendar.get(Calendar.MONTH)+1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    String starTime=tvStarttime.getText().toString();
                    String time=year+"-"+month+"-"+day+" "+starTime+":00";
                    Log.i("", "onClick: ---------------格式化的时间为："+time);
                    Date starDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);

                    if (starDate.getTime() < System.currentTimeMillis()){

                        new AlertDialog.Builder(this)
                                .setTitle("安全防护计划")
                                .setMessage("开始时间小于当前时间，生效时间为第二天，是否继续？")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        nextSettingPlantime( tvStarttime.getText().toString(), tvEndtime.getText().toString(), tvDoubletime.getText().toString());
                                    }
                                })
                                .setNegativeButton("否",null)
                                .show();
                    }else {
                        nextSettingPlantime( tvStarttime.getText().toString(), tvEndtime.getText().toString(), tvDoubletime.getText().toString());
                    }
                } else {
                    setCloseplanTime();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @OnClick(R2.id.cb_defence_plan)
    public void onViewClicked2() {
        if (cbDefencePlan.isSelected()) {
            cbDefencePlan.setSelected(false);
            plantimeLl.setVisibility(View.GONE);
        } else {
            cbDefencePlan.setSelected(true);
            plantimeLl.setVisibility(View.VISIBLE);
        }
    }
    @OnClick(R2.id.rl_starttime)
    public void onViewClicked3() {
        mDialog_startime.setTitle("设置开始时间");
        try {
            mDialog_startime.setStartDate(format.parse(tvStarttime.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mDialog_startime.show();
    }
    @OnClick(R2.id.rl_endtime)
    public void onViewClicked4() {
        mDialog_endtime.setTitle("设置结束时间");
        mDialog_endtime.show();
        try {
            mDialog_endtime.setStartDate(format.parse(tvEndtime.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @OnClick(R2.id.rl_doubletime)
    public void onViewClicked5() {
        Intent intent = new Intent(SettingDefencePlanActivity.this, SettingDoubletimeActivity.class);
        intent.putExtra("tv_doubletime", tvDoubletime.getText().toString());
        startActivityForResult(intent, 2);
    }



//    @OnClick({R.id.cancel, R.id.ok, R.id.cb_defence_plan, R.id.rl_starttime, R.id.rl_endtime, R.id.rl_doubletime})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.cancel:
//
//                break;
//            case R.id.ok:
//
//                break;
//            case R.id.cb_defence_plan:
//
//                break;
//            case R.id.rl_starttime:
//
//                break;
//            case R.id.rl_endtime:
//
//                break;
//            case R.id.rl_doubletime:
//
//                break;
//        }
//    }

    private void nextSettingPlantime( String startime, String endtime, String doubletime) {
        try {
            if (cb_nextday.isChecked() && format.parse(startime).getTime() < format.parse(endtime).getTime()){
                new AlertDialog.Builder(SettingDefencePlanActivity.this)
                        .setTitle("安全防护计划")
                        .setMessage("时间跨度不能超过24小时哦~")
                        .setPositiveButton("是", null).show();
            }else {
                    if (!cb_nextday.isChecked() && format.parse(startime).getTime() > format.parse(endtime).getTime()) {  //结束时间必须比开始时间晚
                        timeErrorDialog();
                    }else {
                        setOpenPlanTime(startime, endtime, doubletime);
                    }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void timeErrorDialog() {
        new AlertDialog.Builder(SettingDefencePlanActivity.this)
                .setTitle("安全防护计划")
                .setMessage("结束时间必须比开始时间晚，请重新设置")
                .setPositiveButton("是", null).show();
    }

    private void setCloseplanTime() {
        if (mDefencePlanTimes!=null) {
            new SetDefenceTimeTask("", "", "", mChannelNo).execute("0");//不启用
            finish();
        }
    }

    private void setOpenPlanTime(String startime, String endtime, String doubletime) {
        if (cb_nextday.isChecked()) {
            new SetDefenceTimeTask(startime, "n"+endtime, doubletime, mChannelNo).execute("1");
        }else {
            new SetDefenceTimeTask(startime, endtime, doubletime, mChannelNo).execute("1");
        }
        finish();
    }

    /**
     * 异步设置活动检测的提醒时间
     */
    private class SetDefenceTimeTask extends AsyncTask<String, Void, Boolean> {
        private String startTime;
        private String endTime;
        private String doubletime;
        private int channelNo;

        public SetDefenceTimeTask(String startTime, String endTime, String doubletime, int channelNo) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.doubletime = doubletime;
            this.channelNo = channelNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String enable = params[0];
            String params1 = "accessToken=" + accessToken + "&deviceSerial=" + mCaremaId + "&channelNo=" + channelNo + "&period=" + doubletime
                        + "&startTime=" + startTime + "&stopTime=" + endTime + "&enable=" + enable;

            Log.i("SettingDefenceplan", "doInBackground: -------------------params1:" + params1);
            Boolean result;
            String results = HttpUtils.sendPostMessage("https://open.ys7.com/api/lapp/device/defence/plan/set",
                    params1, "utf-8");
            if (results.contains("200"))
                result = true;
            else result = false;

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(SettingDefencePlanActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingDefencePlanActivity.this, "设置失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                mDoubleTime = data.getExtras().getString("doubletime_send");
                tvDoubletime.setText(mDoubleTime);
                break;
        }
    }

}
