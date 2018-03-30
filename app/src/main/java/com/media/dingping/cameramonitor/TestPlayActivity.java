package com.media.dingping.cameramonitor;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/6/2 0002.
 */
public class TestPlayActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    @BindView(R2.id.edit_url)
    EditText editUrl;
    @BindView(R2.id.play)
    Button play;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.play)
    public void onClick() {
        String uriString = editUrl.getText().toString();
        Log.i(TAG, "播放路径为：" + uriString);
    }
}
