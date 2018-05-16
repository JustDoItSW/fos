package com.fos.service.netty;

import android.os.Bundle;
import android.os.Message;

import com.fos.activity.LoginActivity;
import com.fos.activity.SelectFlower;
import com.fos.fragment.ControlFragment;
import com.fos.fragment.DataFragment;
import com.fos.fragment.FlowerFragment;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LogUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: TODO
 * @date 2018/5/12 20:16
 */

public class SimpleClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        LogUtil.i("Netty",msg);
        recvHandler(msg);
    }

    /**
     *  把接收到的消息转发给ControlFragment.handler
     * @param str 接收到的消息
     */
    public void recvHandler(String str){
        LogUtil.i("MING",str);
        Message msg = new Message();
        Message msg2 =  new Message();
        msg.what = 0x001;
        msg2.what = 0x001;
        Bundle bundle = new Bundle();
        bundle.putString("info", str);
        msg.setData(bundle);
        msg2.setData(bundle);
        if(str.equals("")){
            /**
             * 登录成功
             */
            msg.what = 0x002;
            LoginActivity.handler.sendMessage(msg);
        }else if(str.equals("")){
            msg.what = 0x003;
            LoginActivity.handler.sendMessage(msg);
        }else {
            try {
                String className = InfomationAnalysis.judgeInfo(str);
                if (className.equals("error")) {
                    msg.what = 0x003;
                    FlowerFragment.handler.sendMessage(msg);
                    msg2.what = 0x003;
                    SelectFlower.handler.sendMessage(msg2);
                } else if (className.equals("Data")) {
                    ControlFragment.handler.sendMessage(msg);
                    DataFragment.handler.sendMessage(msg2);
                } else if (className.equals("Flower")) {
                    FlowerFragment.handler.sendMessage(msg);
                    SelectFlower.handler.sendMessage(msg2);
                } else if (className.equals("UserInfo")) {
                    LoginActivity.handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
