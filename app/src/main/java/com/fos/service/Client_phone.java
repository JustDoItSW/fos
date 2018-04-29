package com.fos.service;

import android.os.Bundle;
import android.os.Message;

import com.fos.entity.Flower;
import com.fos.entity.Infomation;
import com.fos.fragment.ControlFragment;
import com.fos.fragment.FlowerFragment;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LogUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用NIO 无阻塞式TCP传输方式
 */
public class Client_phone {
    public Client_phone client_phone=null;
    private SocketChannel client;
    private Selector selctor = getSelector();
    /**
     * 缓存消息链表
     */
    private List<Object> messageQueue = new LinkedList<>();
    /**
     * 线程池
     */
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 5, 200, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(20));
    private boolean isClose = false;
    private volatile boolean run = true;
    private String ip;
    private int port;

    /**
     *
     * @return 打开选择器
     */
    public Selector getSelector() {
        try {
            return Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.i("MING"," 打开Selector失败");
        return null;
    }

    /**
     *  把服务器和选择器绑定
     * @param recIP 服务器地址
     * @param recPORT 服务器端口
     */
    public Client_phone(String recIP,int recPORT) {
        this.ip=recIP;
        this.port=recPORT;

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    client = SocketChannel.open();
                    client.configureBlocking(false);
                    LogUtil.i("MING"," ip="+ip+"  port="+port);
                    client.connect(new InetSocketAddress(ip, port));
                    client.register(selctor, SelectionKey.OP_CONNECT);
                    LogUtil.i("MING"," 注册选择器");
                } catch (IOException e) {
                    LogUtil.i("MING"," 异常");
                    e.printStackTrace();
                    isClose = true;
                    return;
                }
                while (run) {
                    try {
                        if (selctor.select(20) == 0) {
//                            LogUtil.i("MING"," 无通道可用");
                            continue;
                        }
                        LogUtil.i("MING"," 有通道可用");
                        Iterator<SelectionKey> iterator = selctor.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            LogUtil.i("MING"," 进入迭代器");
                            SelectionKey selectionKey = iterator.next();
                            iterator.remove();
                            if (selectionKey.isConnectable()) {
                                LogUtil.i("MING"," 可连接状态");
                                SocketChannel sc = (SocketChannel) selectionKey.channel();
                                sc.finishConnect();
                                sc.register(selctor, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                                LogUtil.i("MING"," 绑定socket");
                            } else if (selectionKey.isWritable()) {
                                selectionKey.interestOps(SelectionKey.OP_READ);
                                SocketChannel writeSocketChannel = (SocketChannel) selectionKey.channel();
                                Object requestMessage = null;
                                LogUtil.i("MING"," 可写入状态");
                                while (messageQueue.size() > 0) {
                                    requestMessage = messageQueue.remove(0);
                                    LogUtil.i("MING","  write data 2 "+requestMessage);
                                    threadPool.execute(new WriteClientSocketHandler(writeSocketChannel, requestMessage));
                                }
                            } else if (selectionKey.isReadable()) {
                                SocketChannel readSocketChannel = (SocketChannel) selectionKey.channel();
                                ByteBuffer tmp = ByteBuffer.allocate(1024);
                                LogUtil.i("MING","read data begin:");
                                int len = -1;
                                byte[] data = new byte[0];
                                if ((len = readSocketChannel.read(tmp)) > 0) {
                                    data = Arrays.copyOf(data, data.length + len);
                                    System.arraycopy(tmp.array(), 0, data, data.length - len, len);
                                    tmp.rewind();
                                }
                                if (data.length > 0) {
                                    String receiveData = new String(data,"UTF-8");
                                    LogUtil.i("MING","  read data: " + receiveData);
                                    recvHandler(receiveData);
                                }
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        close();
                    }
                }
            }
        });
    }

    /**
     * 发送数据给服务器
     */
    private class WriteClientSocketHandler implements Runnable {
        SocketChannel client;
        Object respnoseMessage;

        WriteClientSocketHandler(SocketChannel client, Object respnoseMessage) {
            this.client = client;
            this.respnoseMessage = respnoseMessage;
        }

        @Override
        public void run() {
            byte[] responseByteData = null;
            String logResponseString = "";
            if (respnoseMessage instanceof byte[]) {
                responseByteData = (byte[]) respnoseMessage;
                logResponseString = new String(responseByteData);
                LogUtil.i("MING","  write data 3 byte "+logResponseString);
            } else if (respnoseMessage instanceof String) {
                logResponseString = (String) respnoseMessage;
                responseByteData = logResponseString.getBytes();
                LogUtil.i("MING","  write data 3 String "+logResponseString);
            }
            if (responseByteData == null || responseByteData.length == 0) {
                LogUtil.i("MING","发送数据为空");
                return;
            }
            try {
                client.write(ByteBuffer.wrap(responseByteData));
                LogUtil.i("MING","  write data 4 finished "+ByteBuffer.wrap(responseByteData));
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }
    }

    /**
     *  判断是否断开
     * @return
     */
    public boolean isClose() {
        return isClose;
    }

    /**
     * 关闭资源
     */
    public void close() {
        try {
            run = false;
            isClose = true;
            selctor.close();
            client.close();
            threadPool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  提供给外界访问的接口
     * @param data 待发送数据
     */
    public void clientSendMessage(String data) {
        try {
            if (client.isOpen()) {
                client.register(selctor, SelectionKey.OP_WRITE);
            }
        } catch (ClosedChannelException e1) {
            e1.printStackTrace();
        }
        messageQueue.add(data);
        LogUtil.i("MING","data:"+data+"  write data 1 messageQueue.size()="+messageQueue.size());
//        try {
//            Thread.sleep(40);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    /**
     *  把接收到的消息转发给ControlFragment.handler
     * @param str 接收到的消息
     */
    public void recvHandler(String str){
        LogUtil.i("MING",str);
        Message msg = new Message();
        msg.what = 0x001;
        Bundle bundle = new Bundle();
        bundle.putString("info", str);
        msg.setData(bundle);
        try {
            if(InfomationAnalysis.judgeInfo(str) == null){
                ControlFragment.handler.sendMessage(msg);
            }else if(InfomationAnalysis.judgeInfo(str).equals("error")){
                msg.what =  0x003;
                FlowerFragment.handler.sendMessage(msg);
            }else {
                FlowerFragment.handler.sendMessage(msg);
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
        LogUtil.i("MING", "control发送");
    }
}
