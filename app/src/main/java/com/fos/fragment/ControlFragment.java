package com.fos.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fos.R;
import com.fos.activity.MainActivity;
import com.fos.entity.Infomation;
import com.fos.service.ClientSocket;
import com.fos.util.InfomationAnalysis;
import com.github.onlynight.waveview.WaveView;


/**
 * Created by Apersonalive（丁起柠） on 2018/3/28 23 42.
 * Project_name TianShow
 * Package_name dqn.demo.com.tianshow.MyFragment
 * Email 745267209@QQ.com
 */
public class ControlFragment extends Fragment {
    private Switch loginControl;
    private TextView text_temp,text_hum,text_soilHum,text_waterHigh,text_lux;
    private ImageView refresh,light,watering,nutrition,heating;
    private WaveView waveView;
    private EditText ip,port;
    private Animation animation;
    private LinearInterpolator lin;
    public static Handler  handler;
    private static ControlFragment controlFragment;
    private View view;
    public static ControlFragment newInstance(){
        if(controlFragment == null )
            controlFragment = new ControlFragment();
        return controlFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_control,null,false);
        init();
        waveView = (WaveView)view.findViewById(R.id.waveView1);
        waveView.start();
        return view;
    }

    private void init(){
        loginControl = (Switch)view.findViewById(R.id.loginControl);

        text_temp = (TextView)view.findViewById(R.id.text_temp);
        text_hum = (TextView)view.findViewById(R.id.text_hum);
        text_soilHum = (TextView)view.findViewById(R.id.text_soilHum);
        text_waterHigh = (TextView)view.findViewById(R.id.text_waterHigh);
        text_lux = (TextView)view.findViewById(R.id.text_lux);

        light = (ImageView)view.findViewById(R.id.light);
        watering = (ImageView)view.findViewById(R.id.watering);
        refresh = (ImageView)view.findViewById(R.id.refresh);
        heating = (ImageView)view.findViewById(R.id.heating);
        nutrition = (ImageView)view.findViewById(R.id.nutrition);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.refresh_aniamtion);
        lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.clientSocket != null) {
                        MainActivity.clientSocket.clientSendMessage("i");
                        refresh.startAnimation(animation);
                }

            }
        });
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null)
                    if(v.isSelected()) {
                        MainActivity.clientSocket.clientSendMessage("e");
                        v.setSelected(false);
                    }
                    else {
                        MainActivity.clientSocket.clientSendMessage("b");
                        v.setSelected(true);
                    }
            }
        });

        watering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null)
                    if(v.isSelected()) {
                        MainActivity.clientSocket.clientSendMessage("5");
                        v.setSelected(false);
                    }
                    else {
                        MainActivity.clientSocket.clientSendMessage("m");
                        v.setSelected(true);
                    }
            }
        });

        nutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null)
                    if(v.isSelected()) {
                        MainActivity.clientSocket.clientSendMessage("y");
                        v.setSelected(false);
                    }
                    else {
                        MainActivity.clientSocket.clientSendMessage("v");
                        v.setSelected(true);
                    }
            }
        });

        heating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null)
                    if(v.isSelected()) {
                        MainActivity.clientSocket.clientSendMessage("s");
                        v.setSelected(false);
                    }
                    else {
                        MainActivity.clientSocket.clientSendMessage("p");
                        v.setSelected(true);
                    }

            }
        });

        ip =  (EditText)view.findViewById(R.id.ip);
        port =  (EditText)view.findViewById(R.id.port);

        ip.setText("192.168.191.1");
        port.setText("8000");
        loginControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        if(MainActivity.clientSocket ==null){
                            MainActivity.clientSocket  = new ClientSocket(ip.getText().toString(),Integer.parseInt(port.getText().toString()));
                        }
                    else
                        if(MainActivity.clientSocket !=null){
                            MainActivity.clientSocket.closeClient();
                            MainActivity.clientSocket  = null;
                        }
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    refresh.clearAnimation();
                    Bundle bundle = msg.getData();
                    String str = bundle.getString("info");
                    Log.e("info", str);
                    Infomation infomation = InfomationAnalysis.jsonToBean(str);
                    text_temp.setText("温度 ：" + infomation.getTemperature() + "℃");
                    text_hum.setText("湿度 ：" + infomation.getHumidity() + "%");
                    text_waterHigh.setText("水位 ：" + infomation.getWaterHigh() + "cm");
                    text_soilHum.setText("土湿 ：" + infomation.getSoilHumidity() + "%");
                    text_lux.setText("光强 ：" + infomation.getLux() + "l");
                    HumFragment.myLineChart.repaintView(Integer.parseInt(infomation.getHumidity()),infomation.getDate().toString(),Color.rgb(199, 232, 245));
                    LuxFragment.myLineChart.repaintView(Integer.parseInt(infomation.getLux()),infomation.getDate().toString(),Color.rgb(246, 235, 188));
                    SoilHumFragment.myLineChart.repaintView(Integer.parseInt(infomation.getSoilHumidity()),infomation.getDate().toString(),Color.rgb(199, 232, 245));
                    TempFragment.myLineChart.repaintView(Integer.parseInt(infomation.getTemperature()),infomation.getDate().toString(),Color.rgb(255, 150, 150));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };


    }
}
