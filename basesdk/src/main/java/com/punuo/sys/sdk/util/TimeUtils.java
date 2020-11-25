package com.punuo.sys.sdk.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class TimeUtils {

    /**
     * 仿qq或微信的时间显示
     * 时间比较
     * date 当前时间
     * strTime 获取的时间
     */
    public static String getTimes(String date, String strTime) {
        // TODO Auto-generated method stub
        String intIime = "";
        long i = -1;//获取相差的天数
        long i1 = -1;//获取相差的小时
        long i2 = -1;//获取相差的分
        long i3 = -1;//获取相差的
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ParsePosition pos = new ParsePosition(0);
            ParsePosition pos1 = new ParsePosition(0);
            Date dt1 = formatter.parse(date, pos);
            Date dt2 = formatter.parse(strTime, pos1);
            long l = dt1.getTime() - dt2.getTime();

            i = l / (1000 * 60 * 60 * 24);//获取的如果是0，表示是当天的，如果>0的话是以前发的
            if (0 == i) {//今天发的
                i1 = l / (1000 * 60 * 60);
                if (0 == i1) {//xx分之前发的
                    i2 = l / (1000 * 60);
                    if (0 == i2) {//xx秒之前发的
                        i3 = l / (1000);
                        intIime = i3 + "秒钟以前";
                    } else {
                        intIime = i2 + "分钟以前";
                    }
                } else {
                    intIime = i1 + "小时以前";//xx小时之前发的
                }
            } else {//以前发的
                intIime = i + "天以前";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intIime;
    }

    public static Date parseDateToMills(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    public static String formatMills(long mills) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date(mills);
        return format.format(date);
    }

    public static int calAgeMonth(String dateStr) {
        return calAgeMonth(parseDateToMills(dateStr));
    }

    public static int calAgeMonth(Date birthDay) {
        if (birthDay == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        if (calendar.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int dayOfMonthNow = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(birthDay);
        int yearBirth = calendar.get(Calendar.YEAR);
        int monthBirth = calendar.get(Calendar.MONTH);
        int dayOfMonthBirth = calendar.get(Calendar.DAY_OF_MONTH);
        int detDay = dayOfMonthNow - dayOfMonthBirth;
        int detMonth = monthNow - monthBirth;
        int detYear = yearNow - yearBirth;
        detDay = detDay <= 0 ? 1 : 0;
        return Math.abs(detYear * 12 + detMonth) + detDay;
    }

    public static String formatAge(int totalMonth) {
        int year = totalMonth / 12;
        int month = totalMonth % 12;
        StringBuilder builder = new StringBuilder();
        if (year != 0) {
            builder.append(year)
                    .append("年");
        }
        if (month != 0) {
            builder.append(month)
                    .append("个月");
        }
        return builder.toString();
    }
}
