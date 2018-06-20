package com.fos.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fan on 2016/6/23.
 */
public class TimeUtils {


    /**
     * 返回当前时间的格式为 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }

    public static  String dateDiff(String endTime) {
        String strTime = null;
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = sd.format(curDate);
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(str).getTime()
                    - sd.parse(endTime).getTime();
            day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            if (day >= 1) {
                strTime = day + "天" + hour + "时";
            } else {
                if (hour >= 1) {
                    strTime = hour + "时" + min + "分";

                } else {
                    if (min >= 1) {
                        strTime =  min + "分" + sec + "秒";
                    } else {
                        strTime =  sec + "秒";
                    }
                }
            }

            return strTime;
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return "";

    }

    public static  String dateBefore(String endTime) {
        String strTime = null;
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = sd.format(curDate);
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(str).getTime()
                    - sd.parse(endTime).getTime();
            day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            if (day >= 1) {
                strTime = day + "天前" ;
            } else {
                if (hour >= 1) {
                    strTime = hour + "时前";

                } else {
                    if (min >= 1) {
                        strTime =  min + "分前";
                    } else {
                        if(sec>=1)
                        strTime =  sec + "秒前";
                        else
                            strTime  = "刚刚";
                    }
                }
            }
            return strTime;
        } catch (ParseException e) {
             e.printStackTrace();
        }
        return "";

    }


}
