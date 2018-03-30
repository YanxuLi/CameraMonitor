package com.media.dingping.cameramonitor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/13 0013.
 */

public class TimeChangeUtil {

    public TimeChangeUtil(){}

    public static String longToStrDate(long dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(Long.valueOf(dateDate));
        return dateString;
    }

    public static Calendar parseTimeToCalendar(String strTime) {
        if(strTime == null) {
            return null;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;

            try {
                date = sdf.parse(strTime);
            } catch (ParseException var4) {
                var4.printStackTrace();
            }

            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(date);
            return timeCalendar;
        }
    }

    public static String calendar2String(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(calendar.getTime());
        return dateStr;
    }

    public static String date2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        return dateStr;
    }
}
