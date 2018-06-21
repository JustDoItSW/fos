package com.fos.service.netty;

import android.os.Bundle;
import android.os.Message;

import com.fos.R;
import com.fos.activity.CameraActivity;
import com.fos.activity.CommunityActivity;
import com.fos.activity.CommunityInfoActivity;
import com.fos.activity.CreateCommunityActivity;
import com.fos.activity.FlowerInfo;
import com.fos.activity.LoginActivity;
import com.fos.activity.RecordControlActivity;
import com.fos.activity.SelectFlower;
import com.fos.activity.UserInfoActivity;
import com.fos.entity.Community;
import com.fos.entity.UserInfo;
import com.fos.fragment.ControlFragment;
import com.fos.fragment.DataFragment;
import com.fos.fragment.FlowerFragment;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LogUtil;
import io.netty.channel.ChannelHandler.Sharable;
import java.util.Date;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: TODO
 * @date 2018/5/12 20:16
 */

@Sharable
public class SimpleClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
//		super.channelActive(ctx);
        LogUtil.i("激活时间是："+new Date());
        LogUtil.i("HeartBeatClientHandler channelActive");
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
//		super.channelInactive(ctx);
        LogUtil.i("停止时间是："+new Date());
        LogUtil.i("HeartBeatClientHandler channelInactive");
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        LogUtil.i(msg);
        if(msg.equals("Heartbeat")){
            ctx.writeAndFlush("has read message from server");
        }else {
            recvHandler(msg);
        }
    }

    /**
     *  把接收到的消息转发给ControlFragment.handler
     * @param str 接收到的消息
     */
    public void recvHandler(String str){
        LogUtil.i("MING",str);
        Message msg = new Message();
        Message msg2 = new Message();
        Message msg3 = new Message();
        Message msg4 = new Message();
        msg.what = 0x001;
        msg2.what = 0x001;
        msg3.what = 0x001;
        msg4.what = 0x001;
        Bundle bundle = new Bundle();
        bundle.putString("info", str);
        msg.setData(bundle);
        msg2.setData(bundle);
        msg3.setData(bundle);
        msg4.setData(bundle);
            try {
                String className = InfomationAnalysis.judgeInfo(str);
                if (className.equals("error")) {
                    msg.what = 0x003;
                    msg2.what = 0x003;
                    msg3.what = 0x003;
                    msg4.what = 0x003;
                    if(FlowerFragment.handler!=null)
                        FlowerFragment.handler.sendMessage(msg);
                    if(SelectFlower.handler!=null)
                        SelectFlower.handler.sendMessage(msg2);
                    if(RecordControlActivity.handler!=null)
                        RecordControlActivity.handler.sendMessage(msg3);
                    if(CameraActivity.handler!=null)
                        CameraActivity.handler.sendMessage(msg4);
                } else if (className.equals("Data")) {
                    msg.what = 0x003;
                    msg2.what = 0x003;
                    if(ControlFragment.handler!=null)
                        ControlFragment.handler.sendMessage(msg);
                    if(DataFragment.handler!=null)
                        DataFragment.handler.sendMessage(msg2);
                } else if(className.equals("Community")) {
                    int type = InfomationAnalysis.jsonToCommunity(str)[0].getType();
                    if(type == 0) {
                        msg.what =0x000;
                        CreateCommunityActivity.handler.sendMessage(msg);//发布成功
                    }else  if(type == 1){
                         msg.what =0x001;
                        CreateCommunityActivity.handler.sendMessage(msg);//发布失败
                    }else if(type == 2){
                        CommunityActivity.handler.sendMessage(msg);//获得动态
                    }
                }else if(className.equals("Evaluate")) {
                    CommunityInfoActivity.handler.sendMessage(msg);
                }else if (className.equals("Flower")) {
                    if(FlowerFragment.handler!=null)
                        FlowerFragment.handler.sendMessage(msg);
                    if(SelectFlower.handler!=null)
                        SelectFlower.handler.sendMessage(msg2);
                    if(RecordControlActivity.handler!=null)
                        RecordControlActivity.handler.sendMessage(msg3);
                    if(CameraActivity.handler!=null)
                        CameraActivity.handler.sendMessage(msg4);
                } else if (className.equals("UserInfo")) {
                    int type = InfomationAnalysis.jsonToUserInfo(str).getType();
                    if(type==0){
                        LoginActivity.handler.sendMessage(msg);//注册成功
                    }else if(type==1){
                        msg.what=0x002;
                        LoginActivity.handler.sendMessage(msg);//登录成功
                    }else if(type == 2){
                        msg.what=0x003;
                        LoginActivity.handler.sendMessage(msg);//登录失败
                    }else if(type == 3){
                        UserInfoActivity.handler.sendMessage(msg);//头像上传成功
                    }else if(type == 4){
                        msg.what =  0x002;
                        UserInfoActivity.handler.sendMessage(msg);//失败
                    }else if(type  == 7){
                        FlowerInfo.handler.sendEmptyMessage(0x001);
                    }else if(type == 8){
                        FlowerInfo.handler.sendEmptyMessage(0x002);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}
