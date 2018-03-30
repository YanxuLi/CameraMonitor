package com.media.dingping.cameramonitor;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.dianpingmedia.camerapush.AlarmMessage;
import com.dianpingmedia.camerapush.OnAlarmMessageListener;
import com.dianpingmedia.camerapush.PushManager;
import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.listener.OnCameraArrayCallBackListener;
import com.media.dingping.cameramonitor.message.MessageImageActivity;
import com.media.dingping.cameramonitor.model.CameraModel;
import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.bean.EZAlarmInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/14 0014.
 */


/**
 * AlarmMessage [msgType=1, time=2017-10-25 17:04:42, sn=720264637, cn=1, alarmType=10002,
 * picUrl=https://open.ys7.com:443/api/alarm/sus/download?susUrl=https://i.ys7.com/c/xpGz7I_720264637?
 * accessToken=at.0qf115ffbtca6n9e6pueqsfh9cssxvh9-1hpeijlohg-00pdqhy-xdcyrruee,
 * videoUrl=, definedContent=, definedType=, isEncripted=false, dsn=null, dcn=null]
 */
public class PushServer extends IntentService {
    private String TAG = "PushServer";

    static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private CameraModel mCameraModel;
    private Cameras mCameras;
    private Cameras.DatasBean mCameraInfo;
    private String mDevideVerifyCode;
    private String cameraID;
    private String alarm_time;
    private String imageUrl;
    private List<EZAlarmInfo> mMessageList = new ArrayList<>();

    private boolean isOnlyOneMessage = true;
    private static int mAlarm_dialog_Count = 0;
    public static int mAlarm_notication_Count = 0;
    private TextView mFrom;
    private TextView mAlarmCount;
    private TextView mMessageTime;
    private ImageView mMessageImage;
    private View mView;
    private Drawable mDrawable;

    private static String userName="";
    private static String password="";

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        PushServer.userName = userName;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        PushServer.password = password;
    }

    public PushServer() {
        super("");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public PushServer(String name) {
        super("");
    }

    public static void setUser(String username,String password){
//        Log.i("PushServer", "setUser: ------------------username="+username+",password="+password);
        setUserName(username);
        setPassword(password);
    }


    public static String getDateTime() {
        return FORMAT.format(new Date());
    }

    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    showNotifictionIcon(getApplicationContext());
                    break;
                case 102:
                    showHostOnlineAlert();
                    break;
                case 103:
//                    Log.i("PushServer", "handleMessage: -----------alarm_time=" + alarm_time + ",mAlarm_dialog_Count" + mAlarm_dialog_Count);
                    if (mDialog != null) {
                        ((TextView) (mDialog.findViewById(R.id.message_from))).setText(cameraID+"");
                        ((TextView) (mDialog.findViewById(R.id.message_time))).setText(alarm_time.substring(10));
                        ((TextView) (mDialog.findViewById(R.id.message_count))).setText(mAlarm_dialog_Count + "");
                        ((ImageView) (mDialog.findViewById(R.id.message_image))).setImageDrawable(mDrawable);
                        if (!mDialog.isShowing())
                            mDialog.show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.i(TAG, "onCreate: -------------------------username="+getUserName());
        mCameraModel = new CameraModel();
        mCameraModel.getCameraList(getUserName(), new OnCameraArrayCallBackListener() {
            @Override
            public void OnSuccess(Cameras cameras) {
                mCameras = cameras;
            }

            @Override
            public void OnGetArrayError() {

            }
        });
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

//        showRemind();
        /**
         * 正式使用
         * */
//        Log.i(TAG, "onHandleIntent: -----------------username="+getUserName()+",password="+getPassword());
        PushManager.getInstance().openPush(getUserName(), getPassword(), new OnAlarmMessageListener() {
            @Override
            public void onAlarmNotifi(AlarmMessage alarmMessage) {
                Log.i("NetTest", "--------------------" + alarmMessage.toString());
                for (int i = 0; i < mCameras.getDatas().size(); i++) {
                    if (mCameras.getDatas().get(i).getCaremaID().equals(alarmMessage.getSn())) {
                        mCameraInfo = mCameras.getDatas().get(i);
                        mDevideVerifyCode = mCameraInfo.getPlayVerifyCode();
                        cameraID = alarmMessage.getSn();
                        alarm_time = alarmMessage.getTime();
                        imageUrl = alarmMessage.getPicUrl();
                        mDrawable = LoadImageFromWebOperations(imageUrl);
                        new GetAlarmMessageTask().execute();
                    }
                }
                showRemind();

            }
        });
    }

    private void showRemind() {
        if (isApplicationBroughtToBackground(getApplicationContext())) {
//            Log.i(TAG, "onHandleIntent: -------------------应用在后台");
//            showNotifictionIcon(getApplicationContext());
            mHandler.sendEmptyMessage(101);
        } else {
//            Log.i(TAG, "onHandleIntent: -------------------应用在前台");
//            showHostOnlineAlert();
            mHandler.sendEmptyMessage(102);
        }
    }

    public boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public void showNotifictionIcon(Context context) {

        mAlarm_notication_Count++;
        Log.i("PushServer", "showNotifictionIcon: -------------" + mAlarm_notication_Count);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);//点击后消失
        builder.setSmallIcon(R.drawable.sport2);//设置通知栏消息标题的头像
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        RemoteViews remoteViews = new RemoteViews("com.media.dingping.cameramonitor", R.layout.notication_layout);
        remoteViews.setTextViewText(R.id.notication_time, alarm_time.substring(10));
        if (mAlarm_notication_Count > 1)
            remoteViews.setTextViewText(R.id.tv_alarm_message_count, "收到" + mAlarm_notication_Count + "条萤石云消息");
        builder.setContent(remoteViews);
        if (mAlarm_notication_Count > 1) {
            //利用PendingIntent来包装我们的intent对象,使其延迟跳转
            Intent intent = new Intent(context, com.media.dingping.cameramonitor.message.MessageActivity.class);//将要跳转的界面
            Bundle bundle = new Bundle();
            bundle.putSerializable("cameraInfo", mCameraInfo);
            intent.putExtra("cameraId", cameraID);
            intent.putExtra("devideVerifyCode", mDevideVerifyCode);
            intent.putExtra("mAlarm_notication_Count",mAlarm_notication_Count);
            intent.putExtras(bundle);
            PendingIntent intentPend = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(intentPend);
//            mAlarm_notication_Count = 0;
        } else {
            if (mMessageList.size() > 0 && mMessageList != null) {
//                Log.i("pushserver", "showNotifictionIcon: --------------mMessageList>0");
                Intent intent = new Intent(context, com.media.dingping.cameramonitor.message.MessageImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("cameraInfo", mCameraInfo);
                intent.putExtra("devideVerifyCode", mDevideVerifyCode);
                intent.putExtra("mAlarm_notication_Count",mAlarm_notication_Count);
                intent.putExtra(IntentConsts.EXTRA_ALARM_INFO, mMessageList.get(0));
                intent.putExtras(bundle);
                PendingIntent intentPend = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(intentPend);
//                mAlarm_notication_Count = 0;
            }
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void showHostOnlineAlert() {
        mAlarm_dialog_Count++;
        Log.i("PushServer", "showHostOnlineAlert: --------------" + mAlarm_dialog_Count);
        initDialog();
        initDialogView();
        dialogListnner();

        if (mAlarm_dialog_Count == 1) {
//            Log.i("PushServer", "showHostOnlineAlert: --------------------------");
            mDialog.show();
            mDialog.getWindow().setGravity(Gravity.TOP);
            mDialog.getWindow().setLayout(-1, -2);
            if (mAlarm_dialog_Count == 1)
                mAlarmCount.setText("");
            else
                mAlarmCount.setText(mAlarm_dialog_Count + "");
            mFrom.setText(cameraID + "");
            mMessageTime.setText(alarm_time.substring(10));
            mMessageImage.setImageDrawable(mDrawable);
            mDialog.setContentView(mView);
        } else {
//            Log.i("PushServer", "showHostOnlineAlert: --------------------");
            mHandler.sendEmptyMessage(103);
        }

    }

    private Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            System.out.println("Exc=" + e);
            return null;
        }
    }

    private void initDialog() {
        if (mBuilder == null && mDialog == null) {
            mBuilder = new AlertDialog.Builder(PushServer.this);
            mDialog = mBuilder.create();
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.getWindow().setDimAmount(0f);//核心代码 解决了无法去除遮罩 
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//set background was transparent
            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);//需要添加的语句
        }
    }

    private void initDialogView() {
        mView = View.inflate(PushServer.this, R.layout.push_layout, null);
        mFrom = (TextView) mView.findViewById(R.id.message_from);
        mAlarmCount = (TextView) mView.findViewById(R.id.message_count);
        mMessageTime = (TextView) mView.findViewById(R.id.message_time);
        mMessageImage = (ImageView) mView.findViewById(R.id.message_image);
    }


    private void dialogListnner() {
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mAlarm_dialog_Count > 1) {
                    Intent intent = new Intent(PushServer.this, com.media.dingping.cameramonitor.message.MessageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("cameraInfo", mCameraInfo);
                    intent.putExtra("cameraId", cameraID);
                    intent.putExtra("devideVerifyCode", mDevideVerifyCode);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    if (mMessageList.size() > 0 && mMessageList != null) {
                        Intent intent = new Intent(PushServer.this, MessageImageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("cameraInfo", mCameraInfo);
                        intent.putExtra("devideVerifyCode", mDevideVerifyCode);
                        intent.putExtra(IntentConsts.EXTRA_ALARM_INFO, mMessageList.get(0));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                mAlarm_dialog_Count = 0;
            }
        });
    }

    public void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PushServer.this);
        alertDialog.setMessage("有新消息，是否查看？");
        alertDialog.setPositiveButton("否",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        alertDialog.setNegativeButton("是",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        mDialog = alertDialog.create();

        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        mDialog.setCanceledOnTouchOutside(false);//点击外面区域不会让dialog消失
        mDialog.show();

    }

    /**
     * 获取事件消息任务
     */
    private class GetAlarmMessageTask extends AsyncTask<String, Void, List<EZAlarmInfo>> {
        private int mErrorCode = 100000;// ErrorCode.ERROR_WEB_NO_ERROR;

        public GetAlarmMessageTask() {
        }

        @Override
        protected List<EZAlarmInfo> doInBackground(String... params) {

            if (!ConnectionDetector.isNetworkAvailable(PushServer.this)) {
                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
                return null;
            }

            List<EZAlarmInfo> result = null;
            Calendar mStartTime;
            Calendar mEndTime;
            mStartTime = Calendar.getInstance();
            mStartTime.set(Calendar.AM_PM, 0);
            mStartTime.set(mStartTime.get(Calendar.YEAR), mStartTime.get(Calendar.MONTH),
                    mStartTime.get(Calendar.DAY_OF_MONTH) - 2, 0, 0, 0);
            mEndTime = Calendar.getInstance();
            mEndTime.set(Calendar.AM_PM, 0);
            mEndTime.set(mEndTime.get(Calendar.YEAR), mEndTime.get(Calendar.MONTH),
                    mEndTime.get(Calendar.DAY_OF_MONTH), 23, 59, 59);

            int pageSize = 3;
            int pageStart = 0;

            try {
                result = GCApplication.getOpenSDK().getAlarmList(cameraID, pageStart, pageSize, mStartTime,
                        mEndTime);
            } catch (BaseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                mErrorCode = errorInfo.errorCode;
                LogUtil.debugLog("EM", errorInfo.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<EZAlarmInfo> result) {
            super.onPostExecute(result);
            if (mMessageList != null) {
                mMessageList.clear();
                mMessageList.addAll(result);
            }
        }
    }
}