package com.media.dingping.cameramonitor;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
public class PushServer_test extends IntentService{

    static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private  String cameraID="123456";
    private int alarm_time=11;

    private boolean isOnlyOneMessage=true;
    private static int mAlarm_dialog_Count=0;
    private static int mAlarm_notication_Count=0;
    private TextView mFrom;
    private TextView mAlarmCount;
    private TextView mMessageTime;
    private View mView;


    public PushServer_test(){
        super("");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public PushServer_test(String name) {
        super("");
    }


    public static String getDateTime() {
        return FORMAT.format(new Date());
    }

    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 101:
                    showNotifictionIcon(getApplicationContext());
                    break;
                case 102:
                    showHostOnlineAlert();
                    break;
                case 103:
                    Log.i("PushServer", "handleMessage: -----------alarm_time="+alarm_time+",mAlarm_dialog_Count"+mAlarm_dialog_Count);
                    if (mDialog != null ) {
                        ((TextView) (mDialog.findViewById(R.id.message_from))).setText(cameraID);
                        ((TextView) (mDialog.findViewById(R.id.message_time))).setText(alarm_time+"");
                        ((TextView) (mDialog.findViewById(R.id.message_count))).setText(mAlarm_dialog_Count+"");
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

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("PushServer_test", "onHandleIntent: ---------------------");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Log.i("PushServer_test", "run: -----------------------------");
                        Thread.sleep(10000);
                        alarm_time++;
                        showRemind();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
//        /**
//         * 正式使用
//           * */
//        PushManager.getInstance().openPush("13534025327", "", new OnAlarmMessageListener() {
//            @Override
//            public void onAlarmNotifi(AlarmMessage alarmMessage) {
//                Log.i("NetTest", "--------------------"+alarmMessage.toString());
//                for (int i = 0; i < mCameras.getDatas().size(); i++) {
//                    if (mCameras.getDatas().get(i).getCaremaID().equals(alarmMessage.getSn())){
//                        mCameraInfo=mCameras.getDatas().get(i);
//                        mDevideVerifyCode=mCameraInfo.getPlayVerifyCode();
//                        cameraID=alarmMessage.getSn();
//                        alarm_time=alarmMessage.getTime();
//                        new GetAlarmMessageTask().execute();
//                    }
//                }
//                showRemind();
//
//            }
//        });

    }

    private void showRemind() {
        if (isApplicationBroughtToBackground(getApplicationContext())){
            Log.i("", "onHandleIntent: -------------------应用在后台");
//            showNotifictionIcon(getApplicationContext());
            mHandler.sendEmptyMessage(101);
        }else {
            Log.i("", "onHandleIntent: -------------------应用在前台");
//            showHostOnlineAlert();
            mHandler.sendEmptyMessage(102);
        }
    }

    public  boolean isApplicationBroughtToBackground(final Context context){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks=am.getRunningTasks(1);
        if (!tasks.isEmpty()){
            ComponentName topActivity=tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())){
                return true;
            }
        }
        return false;
    }

    public void showNotifictionIcon(Context context) {

        mAlarm_notication_Count++;
        Log.i("PushServer", "showNotifictionIcon: -------------"+mAlarm_notication_Count);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(false);//点击后消失
        builder.setSmallIcon(R.drawable.icon);//设置通知栏消息标题的头像
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        RemoteViews remoteViews = new RemoteViews("com.media.dingping.cameramonitor", R.layout.notication_layout);
        remoteViews.setTextViewText(R.id.notication_time,alarm_time+"");
        if (mAlarm_notication_Count>1) {
            Log.i("PushServer", "重新设置 tv_alarm_message_count-------------"+mAlarm_notication_Count);
            remoteViews.setTextViewText(R.id.tv_alarm_message_count, "收到" + mAlarm_notication_Count + "条萤石云消息");
        }
        builder.setContent(remoteViews);
        if (mAlarm_notication_Count>1) {
//            //利用PendingIntent来包装我们的intent对象,使其延迟跳转
//            Intent intent = new Intent(context,com.media.dingping.cameramonitor.message.MessageActivity.class);//将要跳转的界面
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("cameraInfo",mCameraInfo);
//            intent.putExtra("cameraId",cameraID);
//            intent.putExtra("devideVerifyCode",mDevideVerifyCode);
//            intent.putExtras(bundle);
//            PendingIntent intentPend = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//            builder.setContentIntent(intentPend);
//            mAlarm_notication_Count=0;
        }else {
//            if (mMessageList.size()>0 && mMessageList!=null) {
//                Log.i("pushserver", "showNotifictionIcon: --------------mMessageList>0");
//                Intent intent = new Intent(context, MessageImageActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("cameraInfo", mCameraInfo);
//                intent.putExtra("devideVerifyCode", mDevideVerifyCode);
//                intent.putExtra(IntentConsts.EXTRA_ALARM_INFO, mMessageList.get(0));
//                intent.putExtras(bundle);
//                PendingIntent intentPend = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//                builder.setContentIntent(intentPend);
//                mAlarm_notication_Count=0;
//            }
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
            Log.i("PushServer", "showHostOnlineAlert: --------------------------"+mAlarm_dialog_Count);
            mDialog.show();
            mDialog.getWindow().setGravity(Gravity.TOP);
            mAlarmCount.setText("");
            mFrom.setText(cameraID + "");
            mMessageTime.setText(alarm_time+"");
            mDialog.setContentView(mView);
        } else {
            Log.i("PushServer", "showHostOnlineAlert: --------------------"+mAlarm_dialog_Count);
            mHandler.sendEmptyMessage(103);
        }

    }

    private void initDialog() {
        if (mBuilder==null && mDialog==null) {
            mBuilder = new AlertDialog.Builder(PushServer_test.this);
            mDialog = mBuilder.create();
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//set background was transparent
            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);//需要添加的语句
        }
    }

    private void initDialogView() {
        mView = View.inflate(PushServer_test.this, R.layout.push_layout, null);
        mFrom = (TextView) mView.findViewById(R.id.message_from);
        mAlarmCount = (TextView) mView.findViewById(R.id.message_count);
        mMessageTime = (TextView) mView.findViewById(R.id.message_time);
    }


    private void dialogListnner() {
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mAlarm_dialog_Count=0;
            }
        });
    }
}