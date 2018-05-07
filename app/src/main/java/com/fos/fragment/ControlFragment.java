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
import android.support.v7.app.AlertDialog;
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
import com.demo.sdk.Scanner;
import com.fos.R;
import com.fos.activity.MainActivity;
import com.fos.entity.Infomation;
import com.fos.service.Client_phone;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LogUtil;
import com.fos.util.RemoteTunnel;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.net.InetAddress;
import java.util.Map;


/**
 * Created by Apersonalive（丁起柠） on 2018/3/28 23 42.
 * Project_name TianShow
 * Package_name dqn.demo.com.tianshow.MyFragment
 * Email 745267209@QQ.com
 */
public class ControlFragment extends Fragment {

    private TextView autoOrMan;
    private ShimmerTextView shimmerTextView;
    private Shimmer  shimmer;
    public static Handler  handler;
    private static ControlFragment controlFragment;
    private View view;
    private FloatingActionButton fab_light,fab_heating,fab_nut,fab_watering,fab_ctrl;
    private KeyguardManager mKeyguardManager = null;
    private KeyguardManager.KeyguardLock mKeyguardLock = null;
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;
    private String _deviceId="";
    private String _deviceIp="";
    private String _devicePsk="";
    private  int _decoderType=0;
    private  int _videoScreen=1;
    private int _devicePort=554;
    private int _fps=20;
    private LinearLayout _videoConnectLayout;
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
    private Scanner _scanner;

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
        initInternet();
        initVideo();
        init();
        return view;
    }

    private void initInternet(){
        /**
         * 扫描device IP,获得局域网IP或远程IP
         */
        _scanner=new Scanner(getContext());
        _scanner.setOnScanOverListener(new Scanner.OnScanOverListener() {
            @Override
            public void onResult(Map<InetAddress, String> data, InetAddress inetAddress) {
                LogUtil.i("ip.size()= "+data.entrySet().size()+" ip set= "+data.entrySet().toString());
                if(data.size()!=0){
                    for(Map.Entry<InetAddress,String> entry:data.entrySet()){
                        _deviceIp=entry.getKey().getHostAddress();
                    }
                    LogUtil.i("onResult: wlan deviceIP= "+_deviceIp);
                    _startPlay();
                }else{
                    _deviceIp="127.0.0.1";
                    LogUtil.i("onResult: remote deviceIP= "+_deviceIp);
                    _startPlay();
                }
            }
        });
    }

    private void initVideo(){
        _videoConnectLayout=view.findViewById(R.id.video_connecting_layout);
        _videoView=view.findViewById(R.id.video_view);
        _videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.menu_tab.setVisibility(MainActivity.menu_tab.getVisibility()== View.GONE?View.VISIBLE:View.GONE);
            }
        });
        _videoView.setFullScreen(true);
        _deviceId = "brexco.2.us.ytong.rakwireless.com";
        _devicePsk = "admin";
        _fps = 20;
        _scanner.scanAll();
    }
    private void init(){
        autoOrMan = (TextView)view.findViewById(R.id.autoOrMan);
        fab_light =  (FloatingActionButton)view.findViewById(R.id.fab_light) ;
        fab_heating =  (FloatingActionButton)view.findViewById(R.id.fab_heating) ;
        fab_watering =  (FloatingActionButton)view.findViewById(R.id.fab_watering) ;
        fab_nut =  (FloatingActionButton)view.findViewById(R.id.fab_nut) ;
        fab_ctrl =  (FloatingActionButton)view.findViewById(R.id.fab_ctrl) ;
        fab_light.setOnClickListener(onClickListener);
        fab_heating.setOnClickListener(onClickListener);
        fab_watering.setOnClickListener(onClickListener);
        fab_nut.setOnClickListener(onClickListener);
        fab_ctrl.setOnClickListener(onClickListener);
        shimmerTextView=view.findViewById(R.id.video_connecting_text);
        toggleAnimation(shimmerTextView);
        autoOrMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.Client_phone != null) {
                    if(autoOrMan.getText().toString().equals("自动")){
                        MainActivity.Client_phone.clientSendMessage("man"+MainActivity.flower.getFlowerName());
                        autoOrMan.setText("手动");
                    }else{
                         MainActivity.Client_phone.clientSendMessage("smart"+MainActivity.flower.getFlowerName());
                        autoOrMan.setText("自动");
                    }
                }
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                        Bundle bundle = msg.getData();
                        String str = bundle.getString("info");
                        Log.e("info", str);
                        Infomation infomation = InfomationAnalysis.jsonToData(str);
                        fab_light.setTitle("光强:"+infomation.getLux());
                        fab_heating.setTitle("温度:"+infomation.getTemperature());
                        fab_watering.setTitle("土壤湿度:"+infomation.getSoilHumidity());
                      //  fab_nut.setTitle("肥力:"+);
                        HumFragment.myLineChart.repaintView(Integer.parseInt(infomation.getHumidity()), infomation.getDate().toString(), Color.rgb(199, 232, 245));
                        LuxFragment.myLineChart.repaintView(Integer.parseInt(infomation.getLux()), infomation.getDate().toString(), Color.rgb(246, 235, 188));
                        SoilHumFragment.myLineChart.repaintView(Integer.parseInt(infomation.getSoilHumidity()), infomation.getDate().toString(), Color.rgb(199, 232, 245));
                        TempFragment.myLineChart.repaintView(Integer.parseInt(infomation.getTemperature()), infomation.getDate().toString(), Color.rgb(255, 150, 150));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    View.OnClickListener onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(MainActivity.Client_phone !=null){
                switch (v.getId()){
                    case  R.id.fab_light:
                        if(fab_light.isSelected()) {
                            MainActivity.Client_phone.clientSendMessage("e");
                            fab_light.setSelected(false);
                        }
                        else {
                            MainActivity.Client_phone.clientSendMessage("b");
                            fab_light.setSelected(true);
                        }
                        break;
                    case R.id.fab_watering:
                        if(fab_watering.isSelected()) {
                            MainActivity.Client_phone.clientSendMessage("5");
                            fab_watering.setSelected(false);
                        }
                        else {
                            MainActivity.Client_phone.clientSendMessage("m");
                            fab_watering.setSelected(true);
                        }
                        break;
                    case R.id.fab_heating:
                        if(fab_heating.isSelected()) {
                            MainActivity.Client_phone.clientSendMessage("s");
                            fab_heating.setSelected(false);
                        }
                        else {
                            MainActivity.Client_phone.clientSendMessage("p");
                            fab_heating.setSelected(true);
                        }
                        break;
                    case R.id.fab_nut:
                        if(fab_nut.isSelected()) {
                            MainActivity.Client_phone.clientSendMessage("y");
                            fab_nut.setSelected(false);
                        }
                        else {
                            MainActivity.Client_phone.clientSendMessage("v");
                            fab_nut.setSelected(true);
                        }
                        break;
                    case R.id.fab_ctrl:
                        if(fab_ctrl.isSelected()){
                            MainActivity.Client_phone.clientSendMessage("smart");
                            fab_ctrl.setSelected(false);
                        }else{
                            MainActivity.Client_phone.clientSendMessage("smart"+MainActivity.flower.getFlowerName());
                            fab_ctrl.setSelected(true);
                        }
                    default:
                        break;
                }
            }else{
                Toast.makeText(getActivity(),"请先登录服务器！",Toast.LENGTH_LONG);
            }

        }
    };
    public void toggleAnimation(ShimmerTextView target) {
        if (shimmer != null && shimmer.isAnimating()) {
            shimmer.cancel();
        } else {
            shimmer = new Shimmer();
            shimmer.setDuration(3000);
            shimmer.start(target);
        }
    }

    /**
     * 连接远程的服务器，服务器端口554，映射客户端端口5555
     */
    void _startPlay(){
        _videoConnectLayout.setVisibility(View.VISIBLE);
        if(_deviceIp.equals("127.0.0.1")){
            LogUtil.i("remote");
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
        }else{
            LogUtil.i("wlan");
            _devicePort=554;
            PlayVideo();
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
                LogUtil.i("remote");
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
            }else{
                LogUtil.i("wlan");
                try{
                    _player.setImageSize(1280,720);
                    if(_deviceId.equals("www.sunnyoptical.com")){
                        String url="rtsp://"+_deviceIp+"/live1.sdp";
                        _player.playUrl(url,Enums.Transport.UDP);
                    }
                    else{
                        _player.play(_pipe,Enums.Transport.UDP);
                    }
                }catch (Exception e){
                    LogUtil.i("wlan error");
                }
            }
        }
        else
        {
            if(_player!=null)
                _player.stop();
        }
        /**
         * 检测到断开进行重连
         */
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

                            if(_player!=null){
                               LogUtil.i("Reconnect...Please wait!");
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
                                    }else{
                                        if(_deviceId.equals("www.sunnyoptical.com")) {
                                            String url="rtsp://"+_deviceIp+"/live1.sdp";
                                            _player.playUrl(url,Enums.Transport.UDP);
                                        }
                                        else{
                                            _player.play(_pipe,Enums.Transport.UDP);
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

    /**
     * 更新视屏播放的状态
     * @param state
     */
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
     * 关闭资源处理
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
