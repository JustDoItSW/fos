package com.fos.fragment;

import android.app.KeyguardManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.sdk.Controller;
import com.demo.sdk.Enums;
import com.demo.sdk.Module;
import com.demo.sdk.Player;
import com.fos.R;
import com.fos.activity.MainActivity;
import com.fos.entity.Infomation;
import com.fos.service.Client_phone;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LogUtil;
import com.fos.util.RemoteTunnel;
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

    private KeyguardManager mKeyguardManager = null;
    private KeyguardManager.KeyguardLock mKeyguardLock = null;
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;
    public static String _deviceId="";
    public static String _deviceIp="";
    public static String _devicePsk="";
    public static  int _decoderType=0;
    public static  int _videoScreen=1;
    public static int _devicePort=554;
    public static int _fps=20;
    private LinearLayout _videoConnectLayout;
    private TextView _videoConnecttingText;
    private ImageView _videoConnectingImg;
    private AnimationDrawable _loadingAnimation;
    private com.demo.sdk.DisplayView _videoView;
    private RemoteTunnel _remoteTunnel1=null;
    private static Module _module;
    private Player _player;
    private Controller _controller;
    private Enums.Pipe _pipe =Enums.Pipe.MJPEG_PRIMARY;
    private Thread _trafficThread;
    private long _lastTraffic;
    private boolean _getTraffic = false;
    private boolean _stopTraffic = false;
    private int _connectTime=0;

    public static ControlFragment newInstance(){
        if(controlFragment == null )
            controlFragment = new ControlFragment();
        return controlFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.i("MING","ThreradID="+Thread.currentThread().getName());
        view = inflater.inflate(R.layout.fragment_control,null,false);
        init();
        waveView = (WaveView)view.findViewById(R.id.waveView1);
        waveView.start();
        _startPlay();
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

        _videoConnectLayout=view.findViewById(R.id.video_connecting_layout);
        _videoConnecttingText=view.findViewById(R.id.video_connecting_text);
        _videoConnectingImg=view.findViewById(R.id.video_connecting_img);
        _videoView=view.findViewById(R.id.video_view);
        _videoView.setFullScreen(true);
        _loadingAnimation = (AnimationDrawable)_videoConnectingImg.getBackground();
        _deviceId = "brexco.2.us.ytong.rakwireless.com";
        _deviceIp = "127.0.0.1";
        _devicePsk = "admin";
        _fps = 20;


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.clientPhone != null) {
                        MainActivity.clientPhone.clientSendMessage("i");
                        refresh.startAnimation(animation);
                }

            }
        });
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientPhone !=null)
                    if(v.isSelected()) {
                        MainActivity.clientPhone.clientSendMessage("e");
                        v.setSelected(false);
                    }
                    else {
                        MainActivity.clientPhone.clientSendMessage("b");
                        v.setSelected(true);
                    }
            }
        });

        watering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientPhone !=null)
                    if(v.isSelected()) {
                        MainActivity.clientPhone.clientSendMessage("5");
                        v.setSelected(false);
                    }
                    else {
                        MainActivity.clientPhone.clientSendMessage("m");
                        v.setSelected(true);
                    }
            }
        });

        nutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientPhone !=null)
                    if(v.isSelected()) {
                        MainActivity.clientPhone.clientSendMessage("y");
                        v.setSelected(false);
                    }
                    else {
                        MainActivity.clientPhone.clientSendMessage("v");
                        v.setSelected(true);
                    }
            }
        });

        heating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.clientPhone !=null)
                    if(v.isSelected()) {
                        MainActivity.clientPhone.clientSendMessage("s");
                        v.setSelected(false);
                    }
                    else {
                        MainActivity.clientPhone.clientSendMessage("p");
                        v.setSelected(true);
                    }

            }
        });

        ip =  (EditText)view.findViewById(R.id.ip);
        port =  (EditText)view.findViewById(R.id.port);

        ip.setText("192.168.23.1");
        port.setText("8000");
        loginControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        if(MainActivity.clientPhone ==null){
                            MainActivity.clientPhone = new Client_phone(ip.getText().toString(),Integer.parseInt(port.getText().toString()));
                        }
                    else
                        if(MainActivity.clientPhone !=null){
                            MainActivity.clientPhone.close();
                            MainActivity.clientPhone = null;
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

    void _startPlay(){
        _videoConnectLayout.setVisibility(View.VISIBLE);
        if(_deviceIp.equals("127.0.0.1")){
            if(_remoteTunnel1==null)
                _remoteTunnel1=new RemoteTunnel(getActivity().getApplicationContext());
            _remoteTunnel1.openTunnel(1, 5555, 554, _deviceId);
            LogUtil.i("MING","into ResultListener");
            _remoteTunnel1.setOnResultListener(new RemoteTunnel.OnResultListener() {
                @Override
                public void onResult(int id, String result) {
                    // TODO Auto-generated method stub
                    Log.i("MING", "onResult: return="+result);
                    if (result.equals("CONNECT_TIMEOUT") ||
                            result.equals("NTCS_CLOSED") ||
                            result.equals("NTCS_UNKNOWN") ||
                            result.equals("FAILED")) {
                        Toast.makeText(getContext(), result,Toast.LENGTH_SHORT).show();
                        if (_remoteTunnel1 != null) {
                            _remoteTunnel1.closeTunnels();
                            _remoteTunnel1 = null;
                        }
                        Stop();
                        Log.i("MING", "onResult: RemoteTunnel  connect failed");
                    } else {
                        Log.i("MING", "onResult: into PlayVideo()");
                        _devicePort=5555;
                        PlayVideo();
                    }
                }
            });
        }
    }

    /**
     * Play Video
     */
    public void PlayVideo()
    {
        _connectTime=0;
        if (_module == null)
            _module = new Module(getContext());
        else
            _module.setContext(getContext());
        _module.setLogLevel(Enums.LogLevel.VERBOSE);
        _module.setUsername("admin");
        _module.setPassword(_devicePsk);
        _module.setPlayerPort(_devicePort);
        _module.setModuleIp(_deviceIp);
        _controller = _module.getController();
        _player = _module.getPlayer();
        _player.setTimeout(20000);
        _player.setOnTimeoutListener(new Player.OnTimeoutListener()
        {
            @Override
            public void onTimeout() {
                // TODO Auto-generated method stub
            }
        });
        if (_videoScreen==1)
            _player.setDisplayView(getContext(), _videoView, null, _decoderType);
        _player.setOnStateChangedListener(new Player.OnStateChangedListener()
        {
            @Override
            public void onStateChanged(Enums.State state) {
                updateState(state);
            }
        });
        _player.setOnVideoSizeChangedListener(new Player.OnVideoSizeChangedListener()
        {
            @Override
            public void onVideoSizeChanged(int width, int height)
            {}

            @Override
            public void onVideoScaledSizeChanged(int arg0, int arg1)
            {
                // TODO Auto-generated method stub
            }
        });

        if (_player.getState() == Enums.State.IDLE)
        {
            if(_deviceIp.equals("127.0.0.1")){
                try {
                    _player.setImageSize(1920,1080);
                    if(_deviceId.equals("www.sunnyoptical.com")) {
                        String url="rtsp://"+_deviceIp+"/live1.sdp";//为了兼容一个特殊的模块
                        _player.playUrl(url,Enums.Transport.TCP);
                    }
                    else{
                        Log.i("MING", "PlayVideo: play TCP");
                        _player.play(_pipe, Enums.Transport.TCP);
                    }
                }
                catch (Exception e){
                    Log.e("====>","psk error");
                }
            }
        }
        else
        {
            if(_player!=null)
                _player.stop();
        }
        updateState(_player.getState());
        final int id = android.os.Process.myUid();
        _lastTraffic = TrafficStats.getUidRxBytes(id);

        _trafficThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;; ) {
                    if (_stopTraffic) {
                        break;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //检测到断开进行重连
                            if(_player!=null){
                                Log.e("Reconnect...","");
                                if(_player.getState()== Enums.State.IDLE){
                                    _videoConnectLayout.setVisibility(View.VISIBLE);
                                    _player.stop();
                                    if(_deviceIp.equals("127.0.0.1")){
                                        if(_deviceId.equals("www.sunnyoptical.com")) {
                                            String url="rtsp://"+_deviceIp+"/live1.sdp";
                                            _player.playUrl(url,Enums.Transport.TCP);
                                        }
                                        else{
                                            Log.i("MING", "PlayVideo: break and ReConnet");
                                            _player.play(_pipe, Enums.Transport.TCP);
                                        }
                                    }
                                }
                            }
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {}
                    if(_player.getState()!= Enums.State.PLAYING){
                        _connectTime++;
                        if(_connectTime>30){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Stop();
                                }
                            });
                        }
                        Log.i("MING","connect timeout "+_connectTime);
                    }
                }
            }
        });
        _trafficThread.start();
    }

    private void updateState(Enums.State state) {
        switch (state) {
            case IDLE:
                break;
            case PREPARING:
                break;
            case PLAYING:
                _getTraffic = true;
                _videoConnectLayout.setVisibility(View.GONE);
            case STOPPED:
                _getTraffic = false;
                break;
        }
    }

    /**
     * Stop
     */
    void Stop(){
        _stopTraffic = true;
        if(_player!=null)
            _player.stop();
        if (_remoteTunnel1 != null) {
            _remoteTunnel1.closeTunnels();
            _remoteTunnel1 = null;
        }
    }

}
