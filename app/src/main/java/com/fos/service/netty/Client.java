package com.fos.service.netty;

import android.util.Log;

import com.fos.activity.LoginActivity;
import com.fos.util.InfomationAnalysis;


/**
 * Created by Apersonalive on 2018/5/16.
 */

public class Client {
    private static SimpleClient  simpleClient;

    public static boolean isExist(){
        if(simpleClient!=null)
            return true;
        return false;
    }
    public static void getClient(final String str){
            if(simpleClient == null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       // simpleClient = new SimpleClient("192.168.23.1", 8000);

                        try {
                            simpleClient = new SimpleClient("47.106.161.42", 8000);
                            Log.e("info","成功连接服务器！");

                            if(!str.equals("")||simpleClient != null){
                                simpleClient.clientSendMessage(str);
                                Log.e("info","成功发送"+str);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }else{
                if(!str.equals("")&&simpleClient!=null){
                    simpleClient.clientSendMessage(str);
                    Log.e("info","成功发送"+str);
                }
            }

    }
}
