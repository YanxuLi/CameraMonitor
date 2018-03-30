package com.media.dingping.cameramonitor.message;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.media.dingping.cameramonitor.R;
import com.media.dingping.cameramonitor.bean.AlarmType;
import com.media.dingping.cameramonitor.playback.EZUtils;
import com.media.dingping.cameramonitor.playback.VerifyCodeInput;
import com.videogo.openapi.bean.EZAlarmInfo;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class MessageListAdapter2 extends BaseAdapter implements View.OnClickListener, OnCreateContextMenuListener,
        OnCheckedChangeListener {

    /** 删除菜单 */
    public static final int MENU_DEL_ID = Menu.FIRST + 1;



    private class ViewHolder {
        CheckBox check;
        TextView timeText;
        ImageView image;
        TextView fromTip;
        TextView from;
        TextView type;
        ViewGroup layout;
        ImageView unread;
    }

    private final DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final String[] mWeekdayNames = DateFormatSymbols.getInstance(Locale.getDefault()).getWeekdays();

    private Context mContext;
    private List<Object> mObjects;
    private Map<String, Boolean> mCheckStates = new HashMap<String, Boolean>();

    private Calendar mLastDate;
    private String mDeviceSerial;
    private String mDevideVerifyCode;

    /** 监听对象 */
    private OnClickListener mOnClickListener;

    private boolean mCheckMode;
    private boolean mNoMenu;
    private VerifyCodeInput.VerifyCodeErrorListener mMyVerifyCodeErrorListener;

    private MessageListAdapter2(Context context, String deviceSerial, String devideVerifyCode) {
        mContext = context;
        mDeviceSerial = deviceSerial;
        mDevideVerifyCode = devideVerifyCode;
    }

    public MessageListAdapter2(Context context, List<? extends Object> list, String deviceSerial, String devideVerifyCode,
                               VerifyCodeInput.VerifyCodeErrorListener verifyCodeErrorListener) {
        this(context, deviceSerial,devideVerifyCode);
        mDeviceSerial = deviceSerial;
        mDevideVerifyCode = devideVerifyCode;
        mMyVerifyCodeErrorListener=verifyCodeErrorListener;
        setList(list);
    }

    public void setList(List<? extends Object> list) {
        if (list == null) {
            return;
        }
        List<Object> objects = new ArrayList<Object>();

        Map<String, Boolean> preCheckStates = mCheckStates;
        mCheckStates = new HashMap<String, Boolean>();

        mLastDate = null;
        Calendar tempDate = Calendar.getInstance();
        try {
            for (Object item : list) {
                String id = ((EZAlarmInfo) item).getAlarmId();
                String time = ((EZAlarmInfo) item).getAlarmStartTime();

                try {
                tempDate.setTime(mDateFormat.parse(time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (mLastDate == null || !isSameDate(mLastDate, tempDate)) {
                    mLastDate = (Calendar) tempDate.clone();
                    objects.add(mLastDate);
                }
                objects.add(item);

                Boolean check = preCheckStates.get(id);
                if (check != null && check)
                    mCheckStates.put(id, true);
            }
        } catch (Exception e) {
        }

        mObjects = objects;
    }

    private boolean isSameDate(Calendar firstDate, Calendar secondDate) {
        return (firstDate.get(Calendar.DAY_OF_YEAR) == secondDate.get(Calendar.DAY_OF_YEAR) && firstDate
                .get(Calendar.YEAR) == secondDate.get(Calendar.YEAR));
    }

    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    public void setNoMenu(boolean noMenu) {
        mNoMenu = noMenu;
    }

    public void setCheckMode(boolean checkMode) {
        if (mCheckMode != checkMode) {
            mCheckMode = checkMode;
            if (!checkMode) {
                uncheckAll();
            }
        }
    }

    public boolean isCheckAll() {
        for (Object item : mObjects) {
            String id = null;
            if (item instanceof EZAlarmInfo)
                id = ((EZAlarmInfo) item).getAlarmId();
            if (id != null) {
                Boolean check = mCheckStates.get(id);
                if (check == null || !check)
                    return false;
            }
        }
        return true;
    }

    public void checkAll() {
        for (Object item : mObjects) {
            String id = null;
            if (item instanceof EZAlarmInfo)
                id = ((EZAlarmInfo) item).getAlarmId();
            if (id != null)
                mCheckStates.put(id, true);
        }
        notifyDataSetChanged();
    }

    public void uncheckAll() {
        mCheckStates.clear();
        notifyDataSetChanged();
    }

    public List<String> getCheckedIds() {
        List<String> ids = new ArrayList<String>();
        Set<Entry<String, Boolean>> entries = mCheckStates.entrySet();
        for (Entry<String, Boolean> entry : entries) {
            if (entry.getValue() != null && entry.getValue())
                ids.add(entry.getKey());
        }
        return ids;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (mObjects.get(position) instanceof Calendar) ? 0 : 1;
    }

    @Override
    public Object getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            int viewType = getItemViewType(position);
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.message_listview_item, parent, false);
                // 获取控件对象
                viewHolder.check = (CheckBox) convertView.findViewById(R.id.message_check);
                viewHolder.timeText = (TextView) convertView.findViewById(R.id.message_time);
                viewHolder.layout = (ViewGroup) convertView.findViewById(R.id.message_layout);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.message_image);
                viewHolder.fromTip = (TextView) convertView.findViewById(R.id.message_from_tip);
                viewHolder.from = (TextView) convertView.findViewById(R.id.message_from);
                viewHolder.type = (TextView) convertView.findViewById(R.id.message_type);
                viewHolder.unread = (ImageView) convertView.findViewById(R.id.message_unread);

                // 点击弹出菜单的监听
                viewHolder.layout.setOnCreateContextMenuListener(this);
                // 内容区域的点击响应
                viewHolder.layout.setOnClickListener(this);
                viewHolder.check.setOnClickListener(this);
                viewHolder.check.setOnCheckedChangeListener(this);
                // 设置控件集到convertView
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.layout.setTag(R.id.tag_key_position, position);
            viewHolder.check.setTag(R.id.tag_key_position, position);
            viewHolder.check.setVisibility(mCheckMode ? View.VISIBLE : View.GONE);

            Log.i("messageAdapter", "getView: --------------------" + position);
            Object item = getItem(position);

            if (item instanceof EZAlarmInfo) {
                EZAlarmInfo alarmLogInfo = (EZAlarmInfo) item;

                if (mCheckMode) {
                    Boolean checked = mCheckStates.get(alarmLogInfo.getAlarmId());
                    viewHolder.check.setChecked(checked == null ? false : checked);
                }

                AlarmType alarmType = AlarmType.BODY_ALARM;

                viewHolder.type.setText(mContext.getString(alarmType.getTextResId()));
                // 消息来源
                viewHolder.from.setText(alarmLogInfo.getAlarmName());
                // 消息时间
                if (alarmLogInfo.getAlarmStartTime() != null)
                    viewHolder.timeText.setText(alarmLogInfo.getAlarmStartTime().split(" ")[1]);
                else
                    viewHolder.timeText.setText(null);
                // 消息查看状态
                viewHolder.unread.setVisibility(alarmLogInfo.getIsRead() == 0 ? View.VISIBLE : View.INVISIBLE);
                // 消息图片
                EZUtils.loadImage(mContext, viewHolder.image, alarmLogInfo.getAlarmPicUrl(), mDeviceSerial, mDevideVerifyCode, mMyVerifyCodeErrorListener);
            }
        return convertView;
    }


    @Override
    public void onClick(View v) {
        int position;
        int i = v.getId();
        if (i == R.id.message_layout) {
            position = (Integer) v.getTag(R.id.tag_key_position);
            Log.i("messageAdapter", "onClick: -------------position" + position);
            if (mCheckMode) {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.message_check);
                checkBox.toggle();
                if (mOnClickListener != null)
                    mOnClickListener.onCheckClick(MessageListAdapter2.this, checkBox, position, checkBox.isChecked());
            } else {
                if (mOnClickListener != null) {
                    Log.i("messageAdapter", "onClick: -------------position" + position);
                    mOnClickListener.onItemClick(MessageListAdapter2.this, v, position);
                }
            }

        } else if (i == R.id.message_check) {
            position = (Integer) v.getTag(R.id.tag_key_position);
            boolean check = ((CheckBox) v).isChecked();

            if (mOnClickListener != null)
                mOnClickListener.onCheckClick(MessageListAdapter2.this, v, position, check);

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (!mCheckMode && !mNoMenu) {
            menu.add(Menu.NONE, MENU_DEL_ID, Menu.NONE, mContext.getString(R.string.delete));

            int position = (Integer) v.getTag(R.id.tag_key_position);
            if (mOnClickListener != null)
                mOnClickListener.onItemLongClick(MessageListAdapter2.this, v, position);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (Integer) buttonView.getTag(R.id.tag_key_position);
        Object item = getItem(position);
        if (item instanceof EZAlarmInfo)
            mCheckStates.put(((EZAlarmInfo) item).getAlarmId(), isChecked);
    }

    public interface OnClickListener {

        public void onCheckClick(BaseAdapter adapter, View view, int position, boolean checked);

        public void onItemLongClick(BaseAdapter adapter, View view, int position);

        public void onItemClick(BaseAdapter adapter, View view, int position);
    }
}