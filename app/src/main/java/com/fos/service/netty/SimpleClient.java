package com.fos.service.netty;


import android.widget.Toast;

import com.fos.util.LogUtil;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: netty 客户端代替NIO
 * @date 2018/5/12 20:10
 */

public class SimpleClient {

    protected final HashedWheelTimer timer=new HashedWheelTimer();
    private final ConnectorIdleStateTrigger idleStateTrigger=new ConnectorIdleStateTrigger();
    private String host;
    private int port;
    public Channel channel;
    private EventLoopGroup group;
    public Bootstrap bootstrap;

    public SimpleClient(String host,int port) throws Exception {
        this.host=host;
        this.port=port;
        this.run();
    }

    private void run() throws Exception {
        group=new NioEventLoopGroup();
        try{
            bootstrap=new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO));
            final ConnectionWatchdog watchdog=new ConnectionWatchdog(bootstrap,timer,port,host,true) {
                @Override
                protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {

                }

                @Override
                public ChannelHandler[] handlers() {
                    return new ChannelHandler[]{
                            this,
                            new IdleStateHandler(20,40,10, TimeUnit.SECONDS),
                            idleStateTrigger,
                            new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()),
                            new StringDecoder(Charset.forName("UTF-8")),
                            new StringEncoder(Charset.forName("UTF-8")),
                            new SimpleClientHandler()
                    };
                }
            };
            ChannelFuture future;
            synchronized (bootstrap){
                bootstrap.handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(watchdog.handlers());
                    }
                });
                future=bootstrap.connect(host,port);
                LogUtil.i("链接成功");
            }
            channel=future.sync().channel();
        }catch(Throwable t){
            throw new Exception("connects to fails",t);
        }
    }

    /**
     *  提供给外界访问的接口
     * @param str 待发送数据
     */
    public void clientSendMessage(String str) {
        try {
            if(ConnectionWatchdog.channel!=null){
                channel=ConnectionWatchdog.channel;
            }
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
