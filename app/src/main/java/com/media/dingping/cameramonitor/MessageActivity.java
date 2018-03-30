package com.media.dingping.cameramonitor;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageButton;
import android.widget.ListView;

import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.bean.EZAlarmInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.media.dingping.cameramonitor.GCApplication.mDeviceSerial;

/**
 * Created by Administrator on 2017/8/29 0029.
 */

public class MessageActivity extends Activity {

    MessageListAdapter mAdapter = null;
    List<EZAlarmInfo>  mMessageList=new ArrayList<EZAlarmInfo>();
    @BindView(R2.id.goback)
    ImageButton goback;
    @BindView(R2.id.message_listview)
    ListView messageListview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        ButterKnife.bind(this);
        new GetAlarmMessageTask(true).execute();
        mAdapter=new MessageListAdapter(this,mMessageList,"");
        messageListview.setAdapter(mAdapter);

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
            if (mHeaderOrFooter) {
            } else {
            }

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
                    mStartTime.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            mEndTime = Calendar.getInstance();
            mEndTime.set(Calendar.AM_PM, 0);
            mEndTime.set(mEndTime.get(Calendar.YEAR), mEndTime.get(Calendar.MONTH),
                    mEndTime.get(Calendar.DAY_OF_MONTH), 23, 59, 59);

            int pageSize = 10;
            int pageStart = 0;
            if(mHeaderOrFooter) {
                pageStart = 0;
            } else {
                pageStart = mAdapter.getCount() / pageSize;
            }

            try {
                result = GCApplication.getOpenSDK().getAlarmList(mDeviceSerial, pageStart, pageSize,mStartTime,
                        mEndTime);
            }
            catch (BaseException e) {
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

            if (result == null){
//                if (mErrorCode != ERROR_WEB_NO_ERROR)
//                    onError(mErrorCode);
                return;
            }
//            if (mHeaderOrFooter && (mErrorCode == ERROR_WEB_NO_ERROR || mErrorCode == ERROR_WEB_NO_DATA)) {
//                CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
//                for (LoadingLayout layout : mMessageListView.getLoadingLayoutProxy(true, false).getLayouts()) {
//                    ((PullToRefreshHeader) layout).setLastRefreshTime(":" + dateText);
//                }
//                mMessageListView.setFooterRefreshEnabled(true);
//                mMessageListView.getRefreshableView().removeFooterView(mNoMoreView);
////                mAdapter.();
//                mMessageList.clear();
//            }

            if(mAdapter.getCount() == 0 && result.size() == 0) {
                // show no message ui
            } else if (result.size() < pageSize) {
//                mMessageListView.setFooterRefreshEnabled(false);
//                mMessageListView.getRefreshableView().addFooterView(mNoMoreView);
            } else if (mHeaderOrFooter) {
//                mMessageListView.setFooterRefreshEnabled(true);
//                mMessageListView.getRefreshableView().removeFooterView(mNoMoreView);
            }

            if (result != null && result.size() > 0) {
                mMessageList.addAll(result);
                mAdapter.setList(mMessageList);
//                setupCheckModeLayout(false);
                mAdapter.notifyDataSetChanged();

//                mLastLoadTime = System.currentTimeMillis();
            } else {
//                mErrorCode = ERROR_WEB_NO_DATA;
            }
//            sendUIMessage(mUIHandler, UI_MSG_LIST_CHANGE, 0, 0);

            if (mMessageList.size() > 0) {
//                setNoMessageLayoutVisibility(false);
            }

//            if (mErrorCode != ERROR_WEB_NO_ERROR)
//                onError(mErrorCode);
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


    @OnClick(R2.id.goback)
    public void onClick() {
    }
}
