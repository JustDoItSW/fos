package com.fos.util;

import android.util.Log;

/**
 * Author: 曾勇胜
 * Date: 2018/4/9 10:26
 * Email: 592813685@qq.com
 * Description: 日志工具类
 **/
public final class LogUtil {

    private static boolean isPrint=true;

    private LogUtil(){}

    public static void e(String tag,String info){
        if (isPrint==true){
            Log.e(tag, info);
        }
    }

    public static void i(String tag,String info){
        if (isPrint==true){
            Log.i(tag, info);
        }
    }

    /**
     * 个人专用cwxiong
     * @param info Logcat提示信息
     */
    public static void i(String info){
        if(isPrint==true){
            Log.i("DING", info);
        }
    }

    public static void v(String tag,String info){
        if (isPrint==true){
            Log.v(tag, info);
        }
    }

    public static void w(String tag,String info){
        if (isPrint==true){
            Log.w(tag, info);
        }
    }

    public static void d(String tag,String info){
        if (isPrint==true){
            Log.d(tag, info);
        }
    }
}
