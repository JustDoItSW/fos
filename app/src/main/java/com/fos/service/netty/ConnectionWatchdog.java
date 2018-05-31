package com.fos.service.netty;

import com.fos.util.LogUtil;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: TODO
 * @date 2018/5/31 14:45
 */
@Sharable
public abstract class ConnectionWatchdog extends SimpleChannelInboundHandler<String> implements TimerTask,ChannelHandlerHolder {
    private final Bootstrap bootstrap;
    private final Timer timer;
    private final int port;
    private final String host;
    private volatile boolean reconnect=true;
    private int attempts;

    public static Channel channel=null;

    public ConnectionWatchdog(Bootstrap bootstrap,Timer timer,int port,String host,boolean reconnect){
        this.bootstrap=bootstrap;
        this.timer=timer;
        this.port=port;
        this.host=host;
        this.reconnect=reconnect;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
//		super.channelActive(ctx);
        LogUtil.i("当前来链路已经激活了，重连尝试次数重新置为0");
        attempts=0;
        ctx.fireChannelActive();
    }



    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
//		super.channelInactive(ctx);
        LogUtil.i("链路关闭");
        if(reconnect){
            LogUtil.i("链路关闭,将进行重连");
            if(attempts<12){
                attempts++;
            }
            int timeout= 2 << attempts;
            timer.newTimeout(this,timeout, TimeUnit.MILLISECONDS);
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void run(Timeout arg0) throws Exception {
        // TODO Auto-generated method stub
        ChannelFuture future;
        synchronized(bootstrap){
            bootstrap.handler(new ChannelInitializer<Channel>(){

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    // TODO Auto-generated method stub
                    ch.pipeline().addLast(handlers());
                }
            });
            future=bootstrap.connect(host,port);
        }
        channel=future.channel();
        future.addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                // TODO Auto-generated method stub
                boolean succeed=f.isSuccess();

                if(!succeed){
                    LogUtil.i("重连失败");
                    f.channel().pipeline().fireChannelInactive();
                }else{
                    LogUtil.i("重连成功");
                }
            }
        });

    }
}
