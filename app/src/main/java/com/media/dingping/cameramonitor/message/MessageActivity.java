package com.media.dingping.cameramonitor.message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.media.dingping.cameramonitor.GCApplication;
import com.media.dingping.cameramonitor.PushServer;
import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.R2;
import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.customview.WaitDialog;
import com.media.dingping.cameramonitor.playback.VerifyCodeInput;
import com.videogo.constant.Constant;
import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.bean.EZAlarmInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.media.dingping.cameramonitor.message.MessageImageActivity.ERROR_WEB_NO_ERROR;

/**
 * Created by Administrator on 2017/8/29 0029.
 * 消息
 */

public class MessageActivity extends Activity implements VerifyCodeInput.VerifyCodeErrorListener {

    @BindView(R2.id.check_mode_bottom_divider)
    View mCheckModeBottomDivider;
    @BindView(R2.id.check_mode_bottom)
    LinearLayout mCheckModeBottomLayout;
    @BindView(R2.id.check_all)
    CheckBox mCheckAllView;
    @BindView(R2.id.check_mode_top)
    LinearLayout mCheckModeTopLayout;
    @BindView(R2.id.del_button)
    Button mDeleteButton;
    @BindView(R2.id.read_button)
    Button mReadButton;

    private String TAG = "MessageActivity";

    @BindView(R2.id.goback)
    ImageButton goback;
    @BindView(R2.id.editor)
    TextView editor;
    @BindView(R2.id.message_swipe_refresh_layout)
    SwipeRefreshLayout msg_refresh_layout;
    @BindView(R2.id.message_listview)
    ListView messageListview;
    @BindView(R2.id.message_empty_view)
    LinearLayout emptyView;


    /**
     * 没有更多
     */
    private View mNoMoreView;

    String mCaremaId;
    String mDevideVerifyCode;
    private Cameras.DatasBean mCameraInfo;

    MessageListAdapter mAdapter = null;
    private List<EZAlarmInfo> mMessageList = new ArrayList<>();
    private int mMenuPosition;
    private int mDataType = Constant.MESSAGE_LIST;

    private boolean mCheckMode;
    private int mFromNotication;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        ButterKnife.bind(this);
        initView();
        Intent intent = getIntent();
        mCaremaId = intent.getStringExtra("cameraId");
        mDevideVerifyCode = intent.getStringExtra("devideVerifyCode");
        mFromNotication = intent.getIntExtra("mAlarm_notication_Count",-1);
        if (mFromNotication!=-1)
            PushServer.mAlarm_notication_Count=0;
        mCameraInfo = (Cameras.DatasBean) getIntent().getExtras().getSerializable("cameraInfo");
        new GetAlarmMessageTask(true).execute();  //获取报警消息列表
        mAdapter = new MessageListAdapter(this, mMessageList, mCaremaId, mDevideVerifyCode, this);
        messageListview.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new MessageListAdapter.OnClickListener() {
            @Override
            public void onCheckClick(BaseAdapter adapter, View view, int position, boolean checked) {
                setupCheckModeLayout(false);
            }

            @Override
            public void onItemLongClick(BaseAdapter adapter, View view, int position) {
                mMenuPosition = position;
            }

            @Override
            public void onItemClick(BaseAdapter adapter, View view, int position) {
                EZAlarmInfo alarmInfo = (EZAlarmInfo) adapter.getItem(position);
                setAlarmInfoChecked(alarmInfo);

                Intent intent = new Intent(MessageActivity.this, MessageImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("cameraInfo", mCameraInfo);
                intent.putExtra("devideVerifyCode", mDevideVerifyCode);
                intent.putExtra(IntentConsts.EXTRA_ALARM_INFO, alarmInfo);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mNoMoreView = getLayoutInflater().inflate(R.layout.no_more_footer, null);
        mNoMoreView.setVisibility(View.GONE);
        ((TextView) mNoMoreView.findViewById(R.id.no_more_hint)).setText(R.string.no_more_alarm_tip);
        messageListview.addFooterView(mNoMoreView);
        //设置下拉刷新动画颜色
        msg_refresh_layout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        //设置下拉刷新监听
        msg_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetAlarmMessageTask(true).execute();
            }
        });
    }

    /**
     * 设置消息已读（暂保留）
     *
     * @param alarmInfo
     */
    private void setAlarmInfoChecked(EZAlarmInfo alarmInfo) {
        // 判断是否已读
        if (alarmInfo.getIsRead() == 0)
            new CheckAlarmInfoTask2(false).execute(alarmInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return true;
        }
        EZAlarmInfo alarmInfo = (EZAlarmInfo) mAdapter.getItem(mMenuPosition);
        if (alarmInfo == null) {
            return true;
        }
        switch (item.getItemId()) {
            case com.media.dingping.cameramonitor.MessageListAdapter.MENU_DEL_ID:
                deleteMessage(alarmInfo);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteMessage(final Object param) {
        Context context = (getParent() == null ? this : getParent());

        new AlertDialog.Builder(context).setMessage(R.string.delete_confirm)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteAlarmMessageTask().execute(param);
            }
        }).show();
    }

    @OnClick(R2.id.goback)
    public void onViewClicked() {
        finish();
    }
    @OnClick(R2.id.editor)
    public void onViewClicked1() {
        if (editor.getText().toString().equals("编辑")) {
            editor.setText(R.string.cancel);
            setCheckMode(true);
        } else {
            editor.setText(R.string.editor);
            setCheckMode(false);
        }
        mAdapter.notifyDataSetChanged();
    }
    @OnClick(R2.id.check_all)
    public void onViewClicked2() {
        if (mCheckAllView.isChecked()) {
            mAdapter.checkAll();
        } else {
            mAdapter.uncheckAll();
        }
        setupCheckModeLayout(false);
    }
    @OnClick(R2.id.del_button)
    public void onViewClicked3() {
        deleteMessage(mAdapter.getCheckedIds());
    }
    @OnClick(R2.id.read_button)
    public void onViewClicked4() {
        new CheckAlarmInfoTask2(true).execute(mAdapter.getCheckedIds());
    }


//    @OnClick({R.id.goback, R.id.editor,R.id.check_all, R.id.del_button, R.id.read_button})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.goback:
//                finish();
//                break;
//            case R.id.editor:
//                if (editor.getText().toString().equals("编辑")) {
//                    editor.setText(R.string.cancel);
//                    setCheckMode(true);
//                } else {
//                    editor.setText(R.string.editor);
//                    setCheckMode(false);
//                }
//                mAdapter.notifyDataSetChanged();
//                break;
//            case R.id.check_mode_top:
//                mCheckAllView.toggle();
//                break;
//            case R.id.check_all:
//                if (mCheckAllView.isChecked()) {
//                    mAdapter.checkAll();
//                } else {
//                    mAdapter.uncheckAll();
//                }
//                setupCheckModeLayout(false);
//                break;
//            case R.id.del_button:
//                deleteMessage(mAdapter.getCheckedIds());
//                break;
//            case R.id.read_button:
//                new CheckAlarmInfoTask2(true).execute(mAdapter.getCheckedIds());
//                break;
//        }
//    }

    public void setCheckMode(boolean checkMode) {
        if (mCheckMode != checkMode) {
            mCheckMode = checkMode;
//            mCheckModeTopLayout.setVisibility(mCheckMode ? View.VISIBLE : View.GONE);
//            mCheckModeTopDivider.setVisibility(mCheckMode ? View.VISIBLE : View.GONE);
            mCheckModeBottomLayout.setVisibility(mCheckMode ? View.VISIBLE : View.GONE);
            mCheckModeBottomDivider.setVisibility(mCheckMode ? View.VISIBLE : View.GONE);

            if (mCheckMode)
                setupCheckModeLayout(true);
            mAdapter.setCheckMode(mCheckMode);
        }
    }

    private void setupCheckModeLayout(boolean init) {
        if (mCheckMode) {
            List<String> ids = new ArrayList<String>();
            boolean checkAll = false;

            if (!init) {
                ids.addAll(mAdapter.getCheckedIds());
                checkAll = mAdapter.isCheckAll();
            }

            mCheckAllView.setChecked(checkAll);

            if (ids.size() == 0) {
                mDeleteButton.setText(R.string.delete);
                mDeleteButton.setEnabled(false);

                mReadButton.setEnabled(true);
            } else {
                mDeleteButton.setText(getString(R.string.delete) + '（' + ids.size() + '）');
                mDeleteButton.setEnabled(true);

                mReadButton.setEnabled(true);
            }
        }
    }
    /**
     * 获取事件消息任务
     */
    private class GetAlarmMessageTask extends AsyncTask<String, Void, List<EZAlarmInfo>> {
        private boolean mHeaderOrFooter;
        private int mErrorCode = 100000;// ErrorCode.ERROR_WEB_NO_ERROR;

        public GetAlarmMessageTask(boolean headerOrFooter) {
            mHeaderOrFooter = headerOrFooter;
        }

        @Override
        protected List<EZAlarmInfo> doInBackground(String... params) {

            if (!ConnectionDetector.isNetworkAvailable(MessageActivity.this)) {
                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
                return null;
            }

            List<EZAlarmInfo> result = null;
            Calendar mStartTime;
            Calendar mEndTime;
            mStartTime = Calendar.getInstance();
            mStartTime.set(Calendar.AM_PM, 0);
            mStartTime.set(mStartTime.get(Calendar.YEAR), mStartTime.get(Calendar.MONTH),
                    mStartTime.get(Calendar.DAY_OF_MONTH)-2, 0, 0, 0);
            mEndTime = Calendar.getInstance();
            mEndTime.set(Calendar.AM_PM, 0);
            mEndTime.set(mEndTime.get(Calendar.YEAR), mEndTime.get(Calendar.MONTH),
                    mEndTime.get(Calendar.DAY_OF_MONTH), 23, 59, 59);

            int pageSize = 10;
            int pageStart = 0;
            if (mHeaderOrFooter) {
                pageStart = 0;
            } else {
                pageStart = mAdapter.getCount() / pageSize;
            }

            try {
                result = GCApplication.getOpenSDK().getAlarmList(mCaremaId, pageStart, pageSize, mStartTime,
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
//            mMessageListView.onRefreshComplete();
            int pageSize = 10;

            if (result == null) {
                editor.setVisibility(View.GONE);
                return;
            }

            msg_refresh_layout.setRefreshing(false);

            if (mAdapter.getCount() == 0 && result.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                mNoMoreView.setVisibility(View.GONE);
                msg_refresh_layout.setVisibility(View.GONE);
                editor.setVisibility(View.GONE);
                // show no message ui
            } else if (result.size() < pageSize) {
                emptyView.setVisibility(View.INVISIBLE);
                mNoMoreView.setVisibility(View.VISIBLE);
                msg_refresh_layout.setVisibility(View.VISIBLE);
                editor.setVisibility(View.VISIBLE);
//                mMessageListView.setFooterRefreshEnabled(false);
//                mMessageListView.getRefreshableView().addFooterView(mNoMoreView);
            } else if (mHeaderOrFooter) {
                emptyView.setVisibility(View.INVISIBLE);
                mNoMoreView.setVisibility(View.VISIBLE);
                msg_refresh_layout.setVisibility(View.VISIBLE);
                editor.setVisibility(View.VISIBLE);
//                mMessageListView.setFooterRefreshEnabled(true);
//                mMessageListView.getRefreshableView().removeFooterView(mNoMoreView);
            }

            if (result != null && result.size() > 0) {
                emptyView.setVisibility(View.INVISIBLE);
                mNoMoreView.setVisibility(View.VISIBLE);
                msg_refresh_layout.setVisibility(View.VISIBLE);
                editor.setVisibility(View.VISIBLE);
                if (mMessageList != null && mMessageList.size() > 0)
                    mMessageList.clear();
                mMessageList.addAll(result);
                mAdapter.setList(mMessageList);
//                setupCheckModeLayout(false);
                mAdapter.notifyDataSetChanged();
//                mLastLoadTime = System.currentTimeMillis();
            }

            if (mMessageList.size() > 0) {
                emptyView.setVisibility(View.INVISIBLE);
                mNoMoreView.setVisibility(View.VISIBLE);
                editor.setVisibility(View.VISIBLE);
//                setNoMessageLayoutVisibility(false);
            }
        }

//        protected void onError(int errorCode) {
//            switch (errorCode) {
//                case ERROR_WEB_NO_DATA:
//                    if (mMessageList.size() == 0) {
//                        setRefreshLayoutVisibility(false);
//                        setNoMessageLayoutVisibility(true);
//                        mMessageListView.getRefreshableView().removeFooterView(mNoMoreView);
//                    } else {
//                        setRefreshLayoutVisibility(false);
//                        mMessageListView.setFooterRefreshEnabled(false);
//                        mMessageListView.getRefreshableView().addFooterView(mNoMoreView);
//                    }
//                    break;
//                default:
//                    showError(getErrorTip(R.string.get_message_fail_service_exception, errorCode));
//                    break;
//            }
//        }

//        private void showError(CharSequence text) {
//            if (mHeaderOrFooter && mMessageList.size() == 0) {
//                mRefreshTipView.setText(text);
//                setRefreshLayoutVisibility(true);
//            } else {
//                showToast(text);
//            }
//        }
    }

    /**
     * 删除事件消息任务
     */
    private class DeleteAlarmMessageTask extends AsyncTask<Object, Void, Object> {

        private Dialog mWaitDialog;
        private int mErrorCode = ERROR_WEB_NO_ERROR;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWaitDialog = new WaitDialog(MessageActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
            mWaitDialog.setCancelable(false);
            mWaitDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            if (!ConnectionDetector.isNetworkAvailable(MessageActivity.this)) {
                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
                return null;
            }

            List<String> deleteIds = new ArrayList<String>();
            if (params[0] instanceof EZAlarmInfo) {
                // 单个删除
                EZAlarmInfo info = (EZAlarmInfo) params[0];
                if (TextUtils.isEmpty(info.getAlarmId())) {
                } else {
                    deleteIds.add(info.getAlarmId());
                }

            } else if (params[0] instanceof List<?>) {
                // 批量删除
                List<String> ids = (List<String>) params[0];
                if (ids != null && ids.size() > 0)
                    deleteIds.addAll(ids);
            }
            try {
                GCApplication.getOpenSDK().deleteAlarm(deleteIds);
            } catch (BaseException e) {
                e.printStackTrace();

                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                mErrorCode = errorInfo.errorCode;
                LogUtil.debugLog(TAG, errorInfo.toString());
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            mWaitDialog.dismiss();

            if (result != null) {
                if (result instanceof EZAlarmInfo) {
                    EZAlarmInfo info = (EZAlarmInfo) result;
                    //mAlarmLogInfoManager.deleteAlarmLogList(info);
                    //mj if (mDataType == Constant.MESSAGE_INNER_PUSH)
                    if (mDataType == Constant.MESSAGE_LIST)
                        mMessageList.remove(info);
                } else if (result instanceof List<?>) {
                    List<String> ids = (List<String>) result;
                    for (String id : ids) {
                        deleteAlarmFromList(id);
                    }
                }

                mAdapter.setList(mMessageList);
                mAdapter.notifyDataSetChanged();
            }


        }

        private void deleteAlarmFromList(String alarmId) {
            if (mMessageList != null && mMessageList.size() > 0) {
                for (int i = 0; i < mMessageList.size(); i++) {
                    EZAlarmInfo info = mMessageList.get(i);
                    if (info.getAlarmId().equals(alarmId)) {
                        mMessageList.remove(info);
                    }
                }
            }
        }

    }

    /**
     * 检查事件消息是否已读
     */
    private class CheckAlarmInfoTask2 extends AsyncTask<Object, Void, Boolean> {

        private Dialog mWaitDialog;
        private boolean mCheckAll;
        private int mErrorCode = ERROR_WEB_NO_ERROR;
        private EZAlarmInfo info;

        public CheckAlarmInfoTask2(boolean checkAll) {
            mCheckAll = checkAll;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCheckAll) {
                mWaitDialog = new WaitDialog(MessageActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                mWaitDialog.setCancelable(false);
                mWaitDialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            if (!ConnectionDetector.isNetworkAvailable(MessageActivity.this)) {
                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
                return false;
            }

            List<String> alarmIds = new ArrayList<String>();
            if (params[0] instanceof EZAlarmInfo) {
                // 单个标记
                EZAlarmInfo info = (EZAlarmInfo) params[0];
                if (TextUtils.isEmpty(info.getAlarmId())) {
                } else {
                    alarmIds.add(info.getAlarmId());
                }

            } else if (params[0] instanceof List<?>) {
                // 批量标记
                List<String> ids = (List<String>) params[0];
                if (ids != null && ids.size() > 0)
                    alarmIds.addAll(ids);
            }

            try {
                GCApplication.getOpenSDK().setAlarmStatus(alarmIds, EZConstants.EZAlarmStatus.EZAlarmStatusRead);
                for (String alarmId : alarmIds) {
                    setAlarmRead(alarmId);
                }
                return true;
            } catch (BaseException e) {
                e.printStackTrace();

                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                mErrorCode = errorInfo.errorCode;
                LogUtil.debugLog("EM", errorInfo.toString());

                return false;
            }
//            try {
//                if (mCheckAll) {
//                    mMessageCtrl.setAllAlarmInfoChecked();
//                } else {
//                    // 单独已读
//                    info = params[0];
//                    if (TextUtils.isEmpty(info.getAlarmLogId()))
//                        mMessageCtrl.setAlarmInfoChecked(info.getDeviceSerial(), info.getChannelNo(),
//                                info.getAlarmType(), info.getAlarmStartTime());
//                    else
//                        mMessageCtrl.setAlarmInfoChecked(info.getAlarmLogId());
//                    info.setCheckState(1);
//                }
//                return true;

//            } catch (VideoGoNetSDKException e) {
//                mErrorCode = e.getErrorCode();
//                return false;
//            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (mCheckAll)
                mWaitDialog.dismiss();

            if (result) {
//                setupCheckModeLayout(false);
                mAdapter.notifyDataSetChanged();
//                showToast(getText(R.string.ez_alarm_message_check_success));
//                mCheckModeButton.setChecked(false);
            }


        }

        private void setAlarmRead(String alarmId) {
            if (mMessageList != null && mMessageList.size() > 0) {
                for (EZAlarmInfo info : mMessageList) {
                    if (info.getAlarmId().equals(alarmId)) {
                        info.setIsRead(1);
                    }
                }
            }
        }

    }

    @Override
    public void verifyCodeError() {

    }
}
