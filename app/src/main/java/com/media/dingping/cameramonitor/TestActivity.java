package com.media.dingping.cameramonitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.media.dingping.cameramonitor.bean.StreamInfo;
import com.media.dingping.cameramonitor.bean.TokenInfo;
import com.media.dingping.cameramonitor.listener.OnCameraPlayUrlCallBackListener;
import com.media.dingping.cameramonitor.model.CameraModel;

/**
 * Created by zsc on 2017/5/27 0027.
 * 接口测试
 */
public class TestActivity extends AppCompatActivity {
    private static final String TAG = TestActivity.class.getSimpleName();
    private CameraModel mModel = new CameraModel();

//    @BindView(R.id.up)
//    Button btUp;
//    @BindView(R.id.down)
//    Button btDown;
//    @BindView(R.id.left)
//    Button btLeft;
//    @BindView(R.id.right)
//    Button btRight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.ctl_camera);
//        ButterKnife.bind(this);
//
//        btUp.setOnTouchListener(this);
//        btDown.setOnTouchListener(this);
//        btLeft.setOnTouchListener(this);
//        btRight.setOnTouchListener(this);
//
//        //连接
//        SocketClient.getInstance().connect("192.168.0.111",50000);


        /**
         * 测试获取直播流
         */
        TokenInfo tokenInfo = new TokenInfo();
        TokenInfo.DataBean dataBean = new TokenInfo.DataBean();
        dataBean.setToken("a9e0b6a5-97aa-4879-a6a9-b57bf6e09172");
        TokenInfo.DataBean.AddressBean address = new TokenInfo.DataBean.AddressBean("192.168.0.111", "8733");
        dataBean.setAddress(address);
        tokenInfo.setData(dataBean);
        Log.i("TestActivity", tokenInfo.toString());
        mModel.getCameraPlayUrl(tokenInfo, new OnCameraPlayUrlCallBackListener() {
            @Override
            public void OnSuccess(StreamInfo url) {

            }

            @Override
            public void OnGetURLError() {

            }
        });

//
//        /**
//         * 测试登陆
//         */
//        mModel.Login("18825144909", "123456", new OnStatusListener() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError() {
//
//            }
//
//            @Override
//            public void onExists() {
//
//            }
//        });
//
//        /**
//         * 测试注册
//         */
//        mModel.register("18825144909", "123456", new OnStatusListener() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError() {
//
//            }
//
//            @Override
//            public void onExists() {
//
//            }
//        });
//        /**
//         * 测试获取摄像头列表
//         */
//        mModel.getCameraList("18825144909", new OnCameraArrayCallBackListener() {
//            @Override
//            public void OnSuccess(Cameras cameras) {
//
//            }
//
//            @Override
//            public void OnGetArrayError() {
//
//            }
//        });
//        /**
//         * 测试获取token
//         */
//        mModel.getCameraToken("18825144909", "abcdefg", new OnCameraPlayUrlCallBackListener() {
//            @Override
//            public void OnSuccess(StreamInfo url) {
//
//            }
//
//            @Override
//            public void OnGetURLError() {
//
//            }
//        });
    }

//    @OnClick({R.id.up, R.id.down, R.id.left, R.id.right})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.up:
//                SocketClient.getInstance().ctlCameraMove(0,0);
//                break;
//            case R.id.down:
//                SocketClient.getInstance().ctlCameraMove(1,0);
//                break;
//            case R.id.left:
//                SocketClient.getInstance().ctlCameraMove(2,0);
//                break;
//            case R.id.right:
//                SocketClient.getInstance().ctlCameraMove(3,0);
//                break;
//        }
//    }
//@Override
//public boolean onTouch(View v, MotionEvent event) {
//    switch (event.getAction()){
//        case MotionEvent.ACTION_DOWN:
//            if (v.getId()==R.id.up){
////                mainPresenter.controlCamera(0,0);
//                Toast.makeText(this,"向上移动",Toast.LENGTH_SHORT);
//                Log.i("TestActivity","----------向上移动");
//                SocketClient.getInstance().ctlCameraMove(0,0);
//            }else if (v.getId()==R.id.down){
//                Toast.makeText(this,"向下移动",Toast.LENGTH_SHORT);
//                Log.i("TestActivity","----------向下移动");
//                SocketClient.getInstance().ctlCameraMove(1,0);
////                mainPresenter.controlCamera(1,0);
//            }else if (v.getId()==R.id.left){
////                mainPresenter.controlCamera(2,0);
//                SocketClient.getInstance().ctlCameraMove(2,0);
//            }else if (v.getId()==R.id.right){
////                mainPresenter.controlCamera(3,0);
//                SocketClient.getInstance().ctlCameraMove(3,0);
//            }
//            break;
//        case MotionEvent.ACTION_UP:
//            if (v.getId()==R.id.up){
////                mainPresenter.controlCamera(0,0);
//                Toast.makeText(this,"停止向上移动",Toast.LENGTH_SHORT);
//                Log.i("TestActivity","----------停止向上移动");
//                SocketClient.getInstance().ctlCameraMove(0,1);
//            }else if (v.getId()==R.id.down){
////                mainPresenter.controlCamera(1,0);
//                Toast.makeText(this,"停止向下移动",Toast.LENGTH_SHORT);
//                Log.i("TestActivity","----------停止向下移动");
//                SocketClient.getInstance().ctlCameraMove(1,1);
//            }else if (v.getId()==R.id.left){
////                mainPresenter.controlCamera(2,0);
//                SocketClient.getInstance().ctlCameraMove(2,1);
//            }else if (v.getId()==R.id.right){
////                mainPresenter.controlCamera(3,0);
//                SocketClient.getInstance().ctlCameraMove(3,1);
//            }
//            break;
//    }
//    return false;
//}

}
