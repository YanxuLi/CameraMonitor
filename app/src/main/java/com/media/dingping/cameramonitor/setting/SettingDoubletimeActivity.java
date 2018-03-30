package com.media.dingping.cameramonitor.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/9/7 0007.
 * 重复时间
 */

public class SettingDoubletimeActivity extends Activity {

    @BindView(R2.id.goback)
    ImageButton goback;
    @BindView(R2.id.iv_sunday)
    ImageView ivSunday;
    @BindView(R2.id.sunday)
    RelativeLayout sunday;
    @BindView(R2.id.iv_monday)
    ImageView ivMonday;
    @BindView(R2.id.monday)
    RelativeLayout monday;
    @BindView(R2.id.iv_tuesday)
    ImageView ivTuesday;
    @BindView(R2.id.tuesday)
    RelativeLayout tuesday;
    @BindView(R2.id.iv_wednesday)
    ImageView ivWednesday;
    @BindView(R2.id.wednesday)
    RelativeLayout wednesday;
    @BindView(R2.id.iv_thursday)
    ImageView ivThursday;
    @BindView(R2.id.thursday)
    RelativeLayout thursday;
    @BindView(R2.id.iv_friday)
    ImageView ivFriday;
    @BindView(R2.id.friday)
    RelativeLayout friday;
    @BindView(R2.id.iv_saturday)
    ImageView ivSaturday;
    @BindView(R2.id.saturday)
    RelativeLayout saturday;

    String mStr_now ="";
    String mStrToSave="";
    String mStr_saved;
    private Intent mIntent;

    private ImageView[] imageviews;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_doubling_time);
        ButterKnife.bind(this);
        imageviews= new ImageView[]{ivMonday, ivTuesday, ivWednesday, ivThursday, ivFriday, ivSaturday, ivSunday};
        mIntent = getIntent();
        mStr_saved=mIntent.getStringExtra("tv_doubletime");
        setText();
    }

    private void setText() {
        if (!mStr_saved.equals("") && mStr_saved!=null) {
            for (int i = 0; i < 7; i++) {
                if (mStr_saved.contains(i + "")) {
                    imageviews[i].setVisibility(View.VISIBLE);
                }
            }
        }
//        if (mStr_saved.contains("一"))
//            imageviews[0].setVisibility(View.VISIBLE);
//        if (mStr_saved.contains("二"))
//            imageviews[1].setVisibility(View.VISIBLE);
//        if (mStr_saved.contains("三"))
//            imageviews[2].setVisibility(View.VISIBLE);
//        if (mStr_saved.contains("四"))
//            imageviews[3].setVisibility(View.VISIBLE);
//        if (mStr_saved.contains("五"))
//            imageviews[4].setVisibility(View.VISIBLE);
//        if (mStr_saved.contains("六"))
//            imageviews[5].setVisibility(View.VISIBLE);
//        if (mStr_saved.contains("七"))
//            imageviews[6].setVisibility(View.VISIBLE);
//        if (mStr_saved.equals("工作日")){
//            imageviews[0].setVisibility(View.VISIBLE);
//            imageviews[1].setVisibility(View.VISIBLE);
//            imageviews[2].setVisibility(View.VISIBLE);
//            imageviews[3].setVisibility(View.VISIBLE);
//            imageviews[4].setVisibility(View.VISIBLE);
//        }
//        if (mStr_saved.equals("周末")){
//            imageviews[5].setVisibility(View.VISIBLE);
//            imageviews[6].setVisibility(View.VISIBLE);
//        }
//        if (mStr_saved.equals("每天")){
//            imageviews[0].setVisibility(View.VISIBLE);
//            imageviews[1].setVisibility(View.VISIBLE);
//            imageviews[2].setVisibility(View.VISIBLE);
//            imageviews[3].setVisibility(View.VISIBLE);
//            imageviews[4].setVisibility(View.VISIBLE);
//            imageviews[5].setVisibility(View.VISIBLE);
//            imageviews[6].setVisibility(View.VISIBLE);
//        }
    }

    @OnClick(R2.id.goback)
    public void onViewClicked() {
        befaoreFinish();
    }
    @OnClick(R2.id.sunday)
    public void onViewClicked1() {
        if (ivSunday.getVisibility()==View.VISIBLE)
            ivSunday.setVisibility(View.INVISIBLE);
        else
            ivSunday.setVisibility(View.VISIBLE);
    }
    @OnClick(R2.id.monday)
    public void onViewClicked2() {
        if (ivMonday.getVisibility()==View.VISIBLE)
            ivMonday.setVisibility(View.INVISIBLE);
        else
            ivMonday.setVisibility(View.VISIBLE);
    }
    @OnClick(R2.id.tuesday)
    public void onViewClicked3() {
        if (ivTuesday.getVisibility()==View.VISIBLE)
            ivTuesday.setVisibility(View.INVISIBLE);
        else
            ivTuesday.setVisibility(View.VISIBLE);
    }
    @OnClick(R2.id.wednesday)
    public void onViewClicked4() {
        if (ivWednesday.getVisibility()==View.VISIBLE)
            ivWednesday.setVisibility(View.INVISIBLE);
        else
            ivWednesday.setVisibility(View.VISIBLE);
    }
    @OnClick(R2.id.thursday)
    public void onViewClicked5() {
        if (ivThursday.getVisibility()==View.VISIBLE)
            ivThursday.setVisibility(View.INVISIBLE);
        else
            ivThursday.setVisibility(View.VISIBLE);
    }
    @OnClick(R2.id.friday)
    public void onViewClicked6() {
        if (ivFriday.getVisibility()==View.VISIBLE)
            ivFriday.setVisibility(View.INVISIBLE);
        else
            ivFriday.setVisibility(View.VISIBLE);
    }
    @OnClick(R2.id.saturday)
    public void onViewClicked7() {
        if (ivSaturday.getVisibility()==View.VISIBLE)
            ivSaturday.setVisibility(View.INVISIBLE);
        else
            ivSaturday.setVisibility(View.VISIBLE);
    }


//    @OnClick({R.id.goback, R.id.sunday, R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.goback:
//
//                break;
//            case R.id.sunday:
//
//                break;
//            case R.id.monday:
//
//                break;
//            case R.id.tuesday:
//
//                break;
//            case R.id.wednesday:
//
//                break;
//            case R.id.thursday:
//
//                break;
//            case R.id.friday:
//
//                break;
//            case R.id.saturday:
//
//                break;
//        }
//    }

    private void befaoreFinish() {
        if (ivMonday.getVisibility()== View.VISIBLE) {
            mStr_now += "一,";
            mStrToSave += "0,";
        }
        if (ivTuesday.getVisibility()==View.VISIBLE) {
            mStr_now += "二,";
            mStrToSave += "1,";
        }
        if (ivWednesday.getVisibility()==View.VISIBLE) {
            mStr_now += "三,";
            mStrToSave += "2,";
        }
        if (ivThursday.getVisibility()==View.VISIBLE) {
            mStr_now += "四,";
            mStrToSave += "3,";
        }
        if (ivFriday.getVisibility()==View.VISIBLE) {
            mStr_now += "五,";
            mStrToSave += "4,";
        }
        if (ivSaturday.getVisibility()==View.VISIBLE) {
            mStr_now += "六,";
            mStrToSave += "5,";
        }
        if (ivSunday.getVisibility()==View.VISIBLE) {
            mStr_now += "日,";
            mStrToSave += "6,";
        }
        if (mStr_now.endsWith(","))
            mStr_now = mStr_now.substring(0, mStr_now.length()-1);
        if (mStr_now.equals("一,二,三,四,五,六,日"))
            mStr_now="每天";
        if (mStr_now.equals("一,二,三,四,五"))
            mStr_now="工作日";
        if (mStr_now.equals("六,日"))
            mStr_now="周末";

        if (mStrToSave.endsWith(","))
            mStrToSave = mStrToSave.substring(0, mStrToSave.length()-1);
        mIntent.putExtra("doubletime", mStr_now);
        mIntent.putExtra("doubletime_send",mStrToSave);

        if (!mStrToSave.equals("") && mStrToSave!=null){
            this.setResult(2,mIntent);
            finish();
        }else {
            Toast.makeText(this, "您还没有选择重复时间！", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onBackPressed() {
        befaoreFinish();
        super.onBackPressed();
    }
}
