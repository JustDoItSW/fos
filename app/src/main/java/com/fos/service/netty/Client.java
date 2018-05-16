package com.fos.service.netty;

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
                        simpleClient = new SimpleClient("47.106.161.42", 8000);
                        if(str.equals("")){
                            simpleClient.clientSendMessage(str);
                        }
                    }
                }).start();
            }else{
                if(str.equals("")){
                    simpleClient.clientSendMessage(str);
                }
            }

    }
}
