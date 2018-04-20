package com.fos.service;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: TODO
 * @date 2018/4/20 10:33
 */

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.fos.fragment.ControlFragment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientSocket {
    public Socket socket;
    private String serverAddr ;
    private int REDIRECTED_SERVERPORT;
    private BufferedReader bufferedReader;
    private static ClientSocket clientSocket;
    private PrintWriter out;
    public ClientSocket(String ip, int port){
        serverAddr = ip;
        REDIRECTED_SERVERPORT = port;
        Log.e("info",serverAddr+"   "+REDIRECTED_SERVERPORT);
        connectServer();
    }
//    public static ClientSocket newInstance() {
//        if(clientSocket  ==  null)
//            clientSocket = new ClientSocket();
//        return clientSocket;
//    }



    public void connectServer() {
        new Thread(){
            @Override
            public void run() {
                try {
                    Log.e("info","开始连接");
                    //    socket = new Socket(serverAddr, REDIRECTED_SERVERPORT);
                    socket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(serverAddr, REDIRECTED_SERVERPORT);
                    socket.connect(socketAddress,5000);
                    Log.e("info","连接成功");
                    receiveMessage();
                }catch (Exception e){
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 0x002;
                    ControlFragment.handler.sendMessage(msg);
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
                    Message msg = new Message();
                    msg.what = 0x002;
                    ControlFragment.handler.sendMessage(msg);
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
                    if(bufferedReader!=null)
                        bufferedReader.close();
                    if(socket!=null)
                        socket.close();
                    Log.e("info","断开连接");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

