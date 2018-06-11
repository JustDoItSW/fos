package com.fos.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.fos.R;
import com.fos.entity.Flower;
import com.fos.fragment.ControlFragment;
import com.fos.service.netty.Client;

public class RecordControlActivity extends AppCompatActivity {

    private Flower flower;
    private Intent intent;
    public static Handler handler;
    private String recordContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_control);
    }

    private void init(){
        intent = getIntent();
        flower = (Flower)intent.getSerializableExtra("Flower");
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x003){
                    Toast.makeText(RecordControlActivity.this,"您查询的植物："+recordContent.substring(2)+"不存在",Toast.LENGTH_SHORT).show();
                }else{

                }
            }
        };
    }

    private void recordSendToSevice(String str){
        recordContent = str;
        switch (str){
            case "开灯":
                Client.getClient("b");
              //  nowState.setText("当前灯光状态：打开");
                if(ControlFragment.fab_light!=null)
                 ControlFragment.fab_light.setSelected(false);
                break;
            case "关灯":
                Client.getClient("e");
            //    nowState.setText("当前灯光状态：关闭");
                if(ControlFragment.fab_light!=null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "浇水":
                Client.getClient("m");
              //  nowState.setText("当前浇水状态：打开");
                if(ControlFragment.fab_light!=null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "停止浇水":
                Client.getClient("1");
            //    nowState.setText("当前浇水状态：关闭");
                if(ControlFragment.fab_light!=null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "加热":
                Client.getClient("p");
             //   nowState.setText("当前加热状态：开启");
                if(ControlFragment.fab_light!=null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "停止加热":
                Client.getClient("s");
            //    nowState.setText("当前加热状态：关闭");
                if(ControlFragment.fab_light!=null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "施肥":
                Client.getClient("v");
             //   nowState.setText("当前施肥状态：开启");
                if(ControlFragment.fab_light!=null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "停止施肥":
                Client.getClient("y");
             //   nowState.setText("当前施肥状态：关闭");
                if(ControlFragment.fab_light!=null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "开启智能模式":
                Client.getClient("smart"+flower.getFlowerName());
             //   nowState.setText("当前智能模式 ：开启");
                if(ControlFragment.fab_light!=null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "关闭智能模式":
                Client.getClient("smart");
             //   nowState.setText("当前智能模式：关闭");
                if(ControlFragment.fab_light!=null)
                    ControlFragment.fab_light.setSelected(false);
                break;
                default:
                    if(str.substring(0,2).equals("查询"))
                        Client.getClient("search"+str.substring(2));
        }
    }


}
