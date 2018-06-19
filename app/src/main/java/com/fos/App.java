package com.fos;

import android.app.Application;

import org.litepal.LitePalApplication;

import cn.jpush.android.api.JPushInterface;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: TODO
 * @date 2018/6/17 1:00
 */

public class App extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
