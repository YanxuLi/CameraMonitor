package com.media.dingping.cameramonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.dianpingmedia.camerapush.PushManager;
import com.media.dingping.cameramonitor.model.CameraModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/9/15 0015.
 * 个人中心
 */

public class PersonalSettingActivity extends Activity {

    @BindView(R2.id.username)
    TextView username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pset_activity);
        ButterKnife.bind(this);
        username.setText(new CameraModel().getUsersInfo().getUserName());
    }


    @OnClick(R2.id.modify_password)
    public void onViewClicked() {
        Intent intent = new Intent(this, ModifyActvity.class);
        startActivity(intent);
    }
    @OnClick(R2.id.bt_exit)
    public void onViewClicked1() {
        PushManager.getInstance().exit();
        Intent stopPlay = new Intent("donot.pressed.update");
        sendBroadcast(stopPlay);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R2.id.back)
    public void onViewClicked2() {
        finish();
    }

//    @OnClick({R.id.modify_password, R.id.bt_exit, R.id.back})
//    public void onViewClicked(View view) {
//        Intent intent = null;
//        switch (view.getId()) {
//            case R.id.modify_password:
//                intent = new Intent(this, ModifyActvity.class);
//                startActivity(intent);
//                break;
//            case R.id.bt_exit:
//                PushManager.getInstance().exit();
//                Intent stopPlay = new Intent("donot.pressed.update");
//                sendBroadcast(stopPlay);
//                intent = new Intent(this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//                break;
//            case R.id.back:
//                finish();
//                break;
//        }
//    }
}
