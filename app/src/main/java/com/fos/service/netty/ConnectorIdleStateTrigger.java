package com.fos.service.netty;

import com.fos.util.LogUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.channel.ChannelHandler.Sharable;
/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: TODO
 * @date 2018/5/31 14:52
 */

@Sharable
public class ConnectorIdleStateTrigger extends SimpleChannelInboundHandler<String> {

    private static final ByteBuf HEARTBEAT_SEQUENCE= Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // TODO Auto-generated method stub
        if(evt instanceof IdleStateEvent){
            IdleState state=((IdleStateEvent) evt).state();
            if(state==IdleState.WRITER_IDLE){
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
            }
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext arg0, String arg1) throws Exception {
        // TODO Auto-generated method stub
        LogUtil.i("客户端重连"+arg1);

    }
}
