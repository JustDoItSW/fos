package com.fos.fragment;

import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.demo.sdk.Controller;
import com.demo.sdk.Enums;
import com.demo.sdk.Module;
import com.demo.sdk.Player;
import com.demo.sdk.Scanner;
import com.fos.R;
import com.fos.activity.LoginActivity;
import com.fos.activity.MainActivity;
import com.fos.entity.Infomation;
import com.fos.service.Client_phone;
import com.fos.service.netty.Client;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LogUtil;
import com.fos.util.RemoteTunnel;
import com.github.onlynight.waveview.WaveView;
import com.mingle.widget.LoadingView;
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

    private LoadingView video_connecting_layout;
    public static Handler  handler;
    private static ControlFragment controlFragment;
    private BottomSheetBehavior bottomSheetBehavior;
    private View view;
   // private FloatingActionButton fab_light,fab_heating,fab_nut,fab_watering,fab_ctrl;
    private TextView nowState;
    private ImageView fab_light,fab_heating,fab_nut,fab_watering,fab_ctrl,fab_wind;
    private WaveView progress_light,progress_heating,progress_hum,progress_nutrition;
    private NumberProgressBar progress_water,progress_nut;
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
    private boolean isSmart  = false;
    private ObjectAnimator objectAnimator;

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
        initBottomSheet();
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
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
//        ViewGroup.LayoutParams para = _videoView.getLayoutParams();
//        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
//        para.width  = dm.heightPixels;
//        para.height = dm.widthPixels;
//        _videoView.setLayoutParams(para);
//        _videoView.setFullScreen(true);
//        ObjectAnimator objectAnimator  = ObjectAnimator.ofFloat(_videoView,"rotation",0,90);
//        objectAnimator.setDuration(1);
//        _videoView.setPivotX(dm.heightPixels/2);
//        _videoView.setPivotY(dm.widthPixels/2);
//        objectAnimator.start();
        _deviceId = "brexco.2.us.ytong.rakwireless.com";
        _devicePsk = "admin";
        _fps = 20;
        _scanner.scanAll();
    }
    private void init(){
//        fab_light =  (FloatingActionButton)view.findViewById(R.id.fab_light) ;
//        fab_heating =  (FloatingActionButton)view.findViewById(R.id.fab_heating) ;
//        fab_watering =  (FloatingActionButton)view.findViewById(R.id.fab_watering) ;
//        fab_nut =  (FloatingActionButton)view.findViewById(R.id.fab_nut) ;
//        fab_ctrl =  (FloatingActionButton)view.findViewById(R.id.fab_ctrl) ;
        fab_light =  (ImageView)view.findViewById(R.id.fab_light) ;
        fab_heating =  (ImageView)view.findViewById(R.id.fab_heating) ;
        fab_watering =  (ImageView)view.findViewById(R.id.fab_watering) ;
        fab_nut =  (ImageView)view.findViewById(R.id.fab_nut) ;
        fab_ctrl =  (ImageView)view.findViewById(R.id.fab_ctrl) ;
        fab_wind =  (ImageView)view.findViewById(R.id.fab_wind) ;
        nowState = (TextView)view.findViewById(R.id.nowState);
        progress_light = (WaveView)view.findViewById(R.id.progress_light) ;
        progress_heating = (WaveView)view.findViewById(R.id.progress_heating) ;
        progress_hum = (WaveView)view.findViewById(R.id.progress_hum) ;
        progress_nutrition = (WaveView)view.findViewById(R.id.progress_nutrition) ;
//        progress_light.start();
//        progress_heating.start();
//        progress_hum.start();
//        progress_nutrition.start();
        progress_water = (NumberProgressBar)view.findViewById(R.id.progress_water);
        progress_nut = (NumberProgressBar)view.findViewById(R.id.progress_nut);

        fab_light.setOnClickListener(onClickListener);
        fab_heating.setOnClickListener(onClickListener);
        fab_watering.setOnClickListener(onClickListener);
        fab_nut.setOnClickListener(onClickListener);
        fab_wind.setOnClickListener(onClickListener);
        fab_ctrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.isSelectedFlower) {
                    if (fab_ctrl.isSelected()) {
                        Client.getClient("smart");
                        nowState.setText("当前智能模式：关闭");
                        fab_ctrl.setSelected(false);
                        isSmart = false;
                    } else {
                        Client.getClient("smart" + MainActivity.flower.getFlowerName());
                        nowState.setText("当前智能模式：开启");
                        fab_ctrl.setSelected(true);
                        isSmart = true;
                    }
                }else{
                    Toast.makeText(getActivity(), "你还没有选择自己的植物哦,请选择你的植物", Toast.LENGTH_SHORT).show();
                }
            }
        });
        video_connecting_layout=view.findViewById(R.id.video_connecting_text);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                        Bundle bundle = msg.getData();
                        String str = bundle.getString("info");
                        Log.e("Ctrl收到：", str);
                        Infomation infomation = InfomationAnalysis.jsonToData(str);
//                        fab_light.setTitle("光强:"+infomation.getLux()+"lux");
//                        fab_heating.setTitle("温度:"+infomation.getTemperature()+"°");
//                        fab_watering.setTitle("土壤湿度:"+Integer.parseInt(infomation.getSoilHumidity())/10+"%");
                      //  fab_nut.setTitle("肥力:"+);

                    progress_water.setProgress(((3000-100*Integer.parseInt(infomation.getWaterHigh()))/30));
                        progress_nut.setProgress(((3000-100*Integer.parseInt(infomation.getWaterHigh()))/30));

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    public void initBottomSheet(){
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.rl_test));
        RelativeLayout relativeLayout = (RelativeLayout)view.findViewById(R.id.rl_test);
        final ImageView imageView = (ImageView)view.findViewById(R.id.openBottomSheet);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    v.setSelected(false);
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    v.setSelected(true);
                }
            }

        });
        relativeLayout.setOnClickListener(null);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED ||bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    imageView.setSelected(false);
                }else{
                    imageView.setSelected(true);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED ||bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    imageView.setSelected(false);
                }else{
                    imageView.setSelected(true);
                }
            }
        });
    }
    View.OnClickListener onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /**
             * 选择植物后才可以进行操作
             *
             */
            if(MainActivity.isSelectedFlower) {
                if (!isSmart) {
                    switch (v.getId()) {
                        case R.id.fab_light:
                            if (fab_light.isSelected()) {
                                Client.getClient("e");
                                nowState.setText("当前灯光状态：关闭");
                                fab_light.setSelected(false);
                            } else {
                                Client.getClient("b");
                                nowState.setText("当前灯光状态：开启");
                                fab_light.setSelected(true);
                            }
                            break;
                        case R.id.fab_watering:
                            if (fab_watering.isSelected()) {
                                Client.getClient("1");
                                nowState.setText("当前浇水状态：关闭");
                                fab_watering.setSelected(false);
                            } else {
                                Client.getClient("m");
                                nowState.setText("当前浇水状态：开启");
                                fab_watering.setSelected(true);
                            }
                            break;
                        case R.id.fab_heating:
                            if (fab_heating.isSelected()) {
                                Client.getClient("s");
                                nowState.setText("当前加热状态：关闭");
                                fab_heating.setSelected(false);
                            } else {
                                Client.getClient("p");
                                nowState.setText("当前加热状态：开启");
                                fab_heating.setSelected(true);
                            }
                            break;
                        case R.id.fab_nut:
                            if (fab_nut.isSelected()) {
                                Client.getClient("y");
                                nowState.setText("当前施肥状态：关闭");
                                fab_nut.setSelected(false);
                            } else {
                                Client.getClient("v");
                                nowState.setText("当前施肥状态：开启");
                                fab_nut.setSelected(true);
                            }
                            break;
                        case R.id.fab_wind:
                            if (fab_wind.isSelected()) {
                                Client.getClient("8");
                                nowState.setText("当前通风状态：关闭");
                                fab_wind.setSelected(false);
                            } else {
                                Client.getClient("5");
                                nowState.setText("当前通风状态：开启");
                                fab_wind.setSelected(true);
                            }
                            break;
                        default:
                            break;
                    }
                }
                else
                    Toast.makeText(getActivity(), "智能模式下指令没用哟", Toast.LENGTH_SHORT).show();

            } else{
                Toast.makeText(getActivity(), "你还没有选择自己的植物哦,请选择你的植物", Toast.LENGTH_SHORT).show();
            }
        }
    };


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
                        Log.i("MING", result);
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
