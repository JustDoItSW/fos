package com.fos.service.netty;


import android.widget.Toast;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: netty 客户端代替NIO
 * @date 2018/5/12 20:10
 */

public class SimpleClient {
    private String host;
    private int port;
    public Channel channel;
    private EventLoopGroup group;

    public SimpleClient(String host,int port){
        this.host=host;
        this.port=port;
        this.run();
    }

    private void run() {
        group=new NioEventLoopGroup();
        try{
            Bootstrap bootstrap=new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new SimpleClientInitializer());
            channel=bootstrap.connect(host,port).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  提供给外界访问的接口
     * @param str 待发送数据
     */
    public void clientSendMessage(String str) {
        try {
            channel.writeAndFlush(str + "\r\n");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * 关闭资源
     */
    public void close() {
        group.shutdownGracefully();
    }
}
