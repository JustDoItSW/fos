package com.fos.service.netty;

import io.netty.channel.ChannelHandler;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: TODO
 * @date 2018/5/31 14:46
 */

public interface ChannelHandlerHolder {
    ChannelHandler[] handlers();
}
