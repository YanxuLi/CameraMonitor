package com.media.dingping.cameramonitor;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/9/28 0028.
 */

public class Test extends Activity {

    Animation mAnimation;
    private EditText edit;
    private int key;
    private int lastkey;
    private long time;
    private long num;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t);
        edit = (EditText) findViewById(R.id.et);
        edit.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                // TODO Auto-generated method stub
                Log.e("hoheiya",
                        ">>>key：" + keyCode + "，action:" + event.getAction()
                                + "，longpress：" + event.isLongPress());
                num++;
                if (num == 1) {
                    lastkey = keyCode;
                    time = System.currentTimeMillis();
                    // 指定特定按键后删除
                    key = lastkey;
                } else {
                    if (lastkey == keyCode && key == keyCode)
                    {
                        if (2000 < System.currentTimeMillis() - time)
                        {
                            // 一直按这个键
                            Log.e("hoheiya", "共计：" + num);
                            if (num >= 60)
                            {
                                Toast.makeText(Test.this,
                                        "长按了键：" + keyCode, Toast.LENGTH_SHORT)
                                        .show();
                            }
                            num = 0;
                        }
                    } else {
                        // 按下了别的键
                        num = 0;
                    }
                }
                return true;
            }
        });
//        mAnimation = AnimationUtils.loadAnimation(this,R.anim.tranlateanim);
//        findViewById(R.id.bt_test).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.tv_test).startAnimation(mAnimation);
//            }
//        });

    }
}
