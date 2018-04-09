package com.fos.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Apersonalive（丁起柠） on 2018/4/6 23 49.
 * Project_name TianShow
 * Package_name dqn.demo.com.tianshow.MyService
 * Email 745267209@QQ.com
 */

public class MainService extends Service {

    private final IBinder iBinder = new LocalBinder();
    public  Thread infomationThread;
    public  class LocalBinder extends Binder {
        public MainService getService() {
            return MainService.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        init();
    }

    private void init(){

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!infomationThread.isAlive()){
            infomationThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    /**
     * 暂停计时
     */
    public void onPause(){

    }

    /**
     * 继续计时
     */
    public void onRecover() {
    }
}
