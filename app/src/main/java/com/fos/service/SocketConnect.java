package com.fos.service;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.fos.fragment.ControlFragment;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: 保留Socket TCP传输
 * @date 2018/4/18 15:32
 */

public class SocketConnect {
    public Socket socket;
    private String serverAddr ;
    private int REDIRECTED_SERVERPORT;
    private BufferedReader bufferedReader;
    private static SocketConnect clientPhone;
    private PrintWriter out;
    public SocketConnect(String ip, int port){
        serverAddr = ip;
        REDIRECTED_SERVERPORT = port;
        Log.e("info",serverAddr+"   "+REDIRECTED_SERVERPORT);
        connectServer();
    }


//    public static SocketConnect newInstance() {
//        if(clientPhone  ==  null)
//            clientPhone = new SocketConnect();
//        return clientPhone;
//    }



    public void connectServer() {
        new Thread(){
            @Override
            public void run() {
                try {
                    Log.e("info","开始连接");
                    socket = new Socket(serverAddr, REDIRECTED_SERVERPORT);
                    Log.e("info","连接成功");
                    receiveMessage();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void clientSendMessage(String str){
        try {
            out =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            Log.e("info","发送成功");
            out.println(str);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(){
        new Thread(){
            @Override
            public void run() {
                    try{
                        //接收服务器消息
                        String str;
                        bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        Log.e("info1","准备接收消息");
                        while((str=bufferedReader.readLine())!=null){
                            Log.e("info1",str);
                            Message msg = new Message();
                            msg.what = 0x001;
                            Bundle bundle = new Bundle();
                            bundle.putString("info", str);
                            msg.setData(bundle);
                            ControlFragment.handler.sendMessage(msg);
                            Log.e("info","control发送");

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
        }.start();
    }
    public void close(){
        new Thread(){
            @Override
            public void run() {
                try{
                    if(out!=null)
                        out.close();
                    bufferedReader.close();
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
