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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private Button btn_ledOpen,btn_ledClose,btn_login, btn_cancellation,btn_watering,btn_noWatering,btn_query;
    private TextView text_temp,text_hum,text_soilHum,text_waterHigh,text_lux;
    private WaveView waveView;
    private EditText ip,port;
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
        btn_cancellation = (Button)view.findViewById(R.id.btn_cancellation);
        btn_ledOpen = (Button)view.findViewById(R.id.btn_ledOpen);
        btn_ledClose = (Button)view.findViewById(R.id.btn_ledClose);
        btn_login = (Button)view.findViewById(R.id.btn_login);
        btn_watering = (Button)view.findViewById(R.id.btn_watering);
        btn_noWatering = (Button)view.findViewById(R.id.btn_noWatering);
        btn_query = (Button)view.findViewById(R.id.btn_query);

        text_temp = (TextView)view.findViewById(R.id.text_temp);
        text_hum = (TextView)view.findViewById(R.id.text_hum);
        text_soilHum = (TextView)view.findViewById(R.id.text_soilHum);
        text_waterHigh = (TextView)view.findViewById(R.id.text_waterHigh);
        text_lux = (TextView)view.findViewById(R.id.text_lux);

        ip =  (EditText)view.findViewById(R.id.ip);
        port =  (EditText)view.findViewById(R.id.port);

        ip.setText("192.168.191.1");
        port.setText("8000");


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket ==null){
                    MainActivity.clientSocket  = new ClientSocket(ip.getText().toString(),Integer.parseInt(port.getText().toString()));
                    btn_login.setEnabled(false);
                    btn_cancellation.setEnabled(true);
                }
            }
        });

        btn_cancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null){
                    MainActivity.clientSocket.closeClient();
                    MainActivity.clientSocket  = null;
                    btn_cancellation.setEnabled(false);
                    btn_login.setEnabled(true);
                }
            }
        });

        btn_ledOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null){
                    MainActivity.clientSocket.clientSendMessage("c");
                }
            }
        });

        btn_ledClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null){
                    MainActivity.clientSocket.clientSendMessage("x");
                }
            }
        });

        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null){
                    MainActivity.clientSocket.clientSendMessage("o");
                }
            }
        });

        btn_watering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null){
                    MainActivity.clientSocket.clientSendMessage("h");
                }
            }
        });

        btn_noWatering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null){
                    MainActivity.clientSocket.clientSendMessage("5");
                }
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    Log.e("info", "control收到");
                    Bundle bundle = msg.getData();
                    String str = bundle.getString("info");
                    Infomation infomation = InfomationAnalysis.jsonToBean(str);
                    text_temp.setText("温度 ：" + infomation.getTemperature() + "℃");
                    text_hum.setText("湿度 ：" + infomation.getHumidity() + "%");
                    text_waterHigh.setText("水位 ：" + infomation.getWaterHigh() + "cm");
                    text_soilHum.setText("土湿 ：" + infomation.getSoilHumidity() + "%");
                    text_lux.setText("光强 ：" + infomation.getLux() + "cd");
                    DataFragment.myLineChart_TAH.repaintView(Integer.parseInt(infomation.getTemperature()),infomation.getDate(),Color.rgb(199, 232, 245));
                    DataFragment.myLineChart_light.repaintView(Integer.parseInt(infomation.getLux()),infomation.getDate(),Color.rgb(199, 232, 245));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };


    }
}
