package com.media.dingping.cameramonitor.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.view.model.ShowDataInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lu on 2017/5/24.
 */

public class ListViewAdapter extends BaseAdapter {

    List<Cameras.DatasBean> mList=new ArrayList<>();
    LayoutInflater mInflater=null;
    Context mContext;

    public ListViewAdapter(Context context){
        this.mContext=context;
        this.mInflater = LayoutInflater.from(context);
    }

    public List<Cameras.DatasBean> getmList() {
        return mList;
    }

    public void setmList(List<Cameras.DatasBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }
        public ListViewAdapter(Context context,List<Cameras.DatasBean> mList) {
        this.mList = mList;
        this.mContext=context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder viewHolder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            mInflater=LayoutInflater.from(parent.getContext());
            convertView=mInflater.inflate(R.layout.listview_item,null);

            viewHolder = new ViewHolder();
            viewHolder.number= (TextView) convertView.findViewById(R.id.number);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

        viewHolder.number.setText(" "+(position+1));
        viewHolder.name.setText(mList.get(position).getTerminal_ID());
        return convertView;
    }

     class ViewHolder{
        private TextView number;
        private TextView name;
    }
}
