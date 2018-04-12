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
import android.widget.CompoundButton;
import android.widget.EditText;
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
    private Button btn_query;
    private Switch ledControl,wateringControl,heatingControl,loginControl;
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
        btn_query = (Button)view.findViewById(R.id.btn_query);


        loginControl = (Switch)view.findViewById(R.id.loginControl);
        ledControl = (Switch)view.findViewById(R.id.ledControl);
        wateringControl = (Switch)view.findViewById(R.id.wateringControl);
        heatingControl = (Switch)view.findViewById(R.id.heatingControl);

        text_temp = (TextView)view.findViewById(R.id.text_temp);
        text_hum = (TextView)view.findViewById(R.id.text_hum);
        text_soilHum = (TextView)view.findViewById(R.id.text_soilHum);
        text_waterHigh = (TextView)view.findViewById(R.id.text_waterHigh);
        text_lux = (TextView)view.findViewById(R.id.text_lux);

        ip =  (EditText)view.findViewById(R.id.ip);
        port =  (EditText)view.findViewById(R.id.port);

        ip.setText("192.168.191.1");
        port.setText("8000");

        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientSocket !=null){
                    MainActivity.clientSocket.clientSendMessage("i");
                }
            }
        });
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
        wateringControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(MainActivity.clientSocket !=null)
                    if(isChecked)
                        MainActivity.clientSocket.clientSendMessage("m");
                    else
                        MainActivity.clientSocket.clientSendMessage("5");
            }
        });
        ledControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(MainActivity.clientSocket !=null)
                    if(isChecked)
                        MainActivity.clientSocket.clientSendMessage("b");
                    else
                        MainActivity.clientSocket.clientSendMessage("e");
            }
        });
        heatingControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(MainActivity.clientSocket !=null)
                    if(isChecked)
                        MainActivity.clientSocket.clientSendMessage("p");
                    else
                        MainActivity.clientSocket.clientSendMessage("s");
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
                    Log.e("info", str);
                    Infomation infomation = InfomationAnalysis.jsonToBean(str);
                    text_temp.setText("温度 ：" + infomation.getTemperature() + "℃");
                    text_hum.setText("湿度 ：" + infomation.getHumidity() + "%");
                    text_waterHigh.setText("水位 ：" + infomation.getWaterHigh() + "cm");
                    text_soilHum.setText("土湿 ：" + infomation.getSoilHumidity() + "%");
                    text_lux.setText("光强 ：" + infomation.getLux() + "l");
                    DataFragment.myLineChart_hum.repaintView(Integer.parseInt(infomation.getHumidity()),infomation.getDate().toString(),Color.rgb(199, 232, 245));
                    DataFragment.myLineChart_lux.repaintView(Integer.parseInt(infomation.getLux()),infomation.getDate().toString(),Color.rgb(246, 235, 188));
                    DataFragment.myLineChart_soilHum.repaintView(Integer.parseInt(infomation.getSoilHumidity()),infomation.getDate().toString(),Color.rgb(199, 232, 245));
                    DataFragment.myLineChart_temp.repaintView(Integer.parseInt(infomation.getTemperature()),infomation.getDate().toString(),Color.rgb(255, 150, 150));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };


    }
}
