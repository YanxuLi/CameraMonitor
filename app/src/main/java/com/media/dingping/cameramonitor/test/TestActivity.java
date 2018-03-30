package com.media.dingping.cameramonitor.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIPlayer;
import com.media.dingping.cameramonitor.R;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/7/27 0027.
 */

public class TestActivity extends Activity {
    private EZUIPlayer mPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mPlayer = (EZUIPlayer) this.findViewById(R.id.ezuiplayer);
        mPlayer.setCallBack(new EZUIPlayer.EZUIPlayerCallBack() {
            @Override
            public void onPlaySuccess() {

            }

            @Override
            public void onPlayFail(EZUIError ezuiError) {

            }

            @Override
            public void onVideoSizeChange(int i, int i1) {

            }

            @Override
            public void onPrepared() {
                mPlayer.startPlay();
            }

            @Override
            public void onPlayTime(Calendar calendar) {

            }

            @Override
            public void onPlayFinish() {

            }
        });
        mPlayer.setUrl("ezopen://open.ys7.com/705984190/1.hd.live");
    }

    @Override
    protected void onStop() {
        super.onStop();

        //停止播放
        mPlayer.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放资源
        mPlayer.releasePlayer();
    }
}
