package com.media.dingping.cameramonitor.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.media.dingping.cameramonitor.PlayRecordActivity;
import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.setting.SettingActivity;
import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.message.MessageActivity;

import java.util.List;

/**
 * Created by Administrator on 2017/6/6 0006.
 */
public class CameraAdapter extends BaseAdapter {

    private List<Cameras.DatasBean> mDatas;
    private Context mContext;
    private int currPosition = 1;

    private OnClickListener mListener = null;

    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

    public int getCurrPosition() {
        return currPosition;
    }

    public void setCurrPosition(int currPosition) {
        if (currPosition > getCount()) {
            this.currPosition = 1;
        } else {
            this.currPosition = currPosition;
        }
    }

    public CameraAdapter(Cameras cameras, Context mContext) {
        this.mContext = mContext;
        this.mDatas = cameras.getDatas();
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mDatas != null ? mDatas.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mDatas == null || mDatas.size() == 0) {
            return null;
        }
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item2, parent, false);
            holder = new ViewHolder();
            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.setting = (ImageView) convertView.findViewById(R.id.ibt_setting);
            holder.playBack = (ImageView) convertView.findViewById(R.id.ibt_playback);
            holder.message= (ImageView) convertView.findViewById(R.id.ibt_message);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.setting.setOnClickListener(mOnClickListener);
        holder.playBack.setOnClickListener(mOnClickListener);
        holder.message.setOnClickListener(mOnClickListener);
        holder.setting.setTag(position);
        holder.playBack.setTag(position);
        holder.message.setTag(position);
        holder.name.setText(mDatas.get(position).getName());
        holder.number.setText(position + 1 + "");
        return convertView;
    }

    public void setCameras(Cameras cameras) {
        this.mDatas = cameras.getDatas();
        currPosition = 1;
        notifyDataSetChanged();
    }

    public Cameras.DatasBean getNextCameraIdToPosition() {
        if (mDatas == null || mDatas.size() == 0) {
            return null;
        }
        return mDatas.get(currPosition - 1);
    }

    public Cameras.DatasBean getClickCameraIdToPosition(int position) {
        if (mDatas == null || mDatas.size() == 0 || position == 0) {
            return null;
        }
        return mDatas.get(position - 1);
    }

    public Cameras.DatasBean getcurrCameraId() {
        if (mDatas == null || mDatas.size() == 0 || currPosition == 0) {
            return null;
        }
        return mDatas.get(currPosition - 1);
    }

    static class ViewHolder {
        private TextView number;
        private TextView name;
        private ImageView setting;
        private ImageView playBack;
        private ImageView message;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = (Integer) v.getTag();
                int i = v.getId();
                if (i == R.id.ibt_setting) {
                    Intent intent1 = new Intent(mContext, SettingActivity.class);
                    intent1.putExtra("cameraId", mDatas.get(position).getCaremaID());
                    mContext.startActivity(intent1);

                } else if (i == R.id.ibt_playback) {
                    Intent intent2 = new Intent(mContext, PlayRecordActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("cameraInfo", mDatas.get(position));
                    intent2.putExtras(bundle);
                    mContext.startActivity(intent2);

                } else if (i == R.id.ibt_message) {
                    Intent intent3 = new Intent(mContext, MessageActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("cameraInfo", mDatas.get(position));
                    intent3.putExtra("cameraId", mDatas.get(position).getCaremaID());
                    intent3.putExtra("devideVerifyCode", mDatas.get(position).getPlayVerifyCode());
                    intent3.putExtras(bundle1);
                    mContext.startActivity(intent3);

                }
            }
        }
    };


    public interface OnClickListener {

        public void onPlayClick(BaseAdapter adapter, View view, int position);

        public void onAlarmListClick(BaseAdapter adapter, View view, int position);

        public void onRemotePlayBackClick(BaseAdapter adapter, View view, int position);

        public void onSetDeviceClick(BaseAdapter adapter, View view, int position);

        public void onDeviceDefenceClick(BaseAdapter adapter, View view, int position);
    }

}
