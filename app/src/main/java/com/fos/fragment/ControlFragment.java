package com.fos.fragment;

import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.demo.sdk.Controller;
import com.demo.sdk.Enums;
import com.demo.sdk.Module;
import com.demo.sdk.Player;
import com.demo.sdk.Scanner;
import com.fos.R;
import com.fos.activity.MainActivity;
import com.fos.activity.RecordControlActivity;
import com.fos.entity.Infomation;
import com.fos.entity.UserMessage;
import com.fos.service.netty.Client;
import com.fos.tensorflow.Classifier;
import com.fos.tensorflow.TensorFlowImageClassifier;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LogUtil;
import com.fos.util.RemoteTunnel;
import com.github.onlynight.waveview.WaveView;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.mingle.widget.LoadingView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.fos.activity.RecordControlActivity.parseIatResult;


/**
 * Created by Apersonalive（丁起柠） on 2018/3/28 23 42.
 * Project_name TianShow
 * Package_name dqn.demo.com.tianshow.MyFragment
 * Email 745267209@QQ.com
 */
public class ControlFragment extends Fragment implements TextToSpeech.OnInitListener {

    // Classifier
    private Bitmap bitmap;
    private Classifier classifier;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";
    private static final String MODEL_FILE = "file:///android_asset/optimized_mobilenet_plant_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/plant_labels.txt";

    private LoadingView video_connecting_layout;
    public static Handler  handler;
    private static ControlFragment controlFragment;
    private BottomSheetBehavior bottomSheetBehavior;
    private View view;
    private TextView nowState,hintDisease;
    public static ImageView fab_light,fab_heating,fab_nut,fab_watering,fab_ctrl,fab_wind;

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
    private Thread thread;
    private String recordContent = "";
    private com.fos.myView.WaveView mWaveView;
    private RelativeLayout btn_record,recordControl,r1;
    private ImageView img_record,redPoint;
    private SpeechRecognizer mIat;
    private TextToSpeech tts;
    private Boolean isDisease =false;

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
        _videoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                recordControl();
                return false;
            }
        });
        _deviceId = "brexco.2.us.ytong.rakwireless.com";
        _devicePsk = "admin";
        _fps = 20;
        _scanner.scanAll();
    }
    private void init(){
        classifier =
                TensorFlowImageClassifier.create(
                        getContext().getAssets(),
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        IMAGE_MEAN,
                        IMAGE_STD,
                        INPUT_NAME,
                        OUTPUT_NAME);

        mIat=SpeechRecognizer.createRecognizer(getContext(),mInitListener);
        SpeechUtility.createUtility( getContext(), SpeechConstant.APPID+"=5b1cb818"); //+SpeechConstant.FORCE_LOGIN+"=true"
        fab_light =  (ImageView)view.findViewById(R.id.fab_light) ;
        fab_heating =  (ImageView)view.findViewById(R.id.fab_heating) ;
        fab_watering =  (ImageView)view.findViewById(R.id.fab_watering) ;
        fab_nut =  (ImageView)view.findViewById(R.id.fab_nut) ;
        fab_ctrl =  (ImageView)view.findViewById(R.id.fab_ctrl) ;
        fab_wind =  (ImageView)view.findViewById(R.id.fab_wind) ;
        nowState = (TextView)view.findViewById(R.id.nowState);
        hintDisease = (TextView)view.findViewById(R.id.hintDisease);
        redPoint = (ImageView)view.findViewById(R.id.point_red);
        mWaveView = (com.fos.myView.WaveView) view.findViewById(R.id.wave_view);
        recordControl = (RelativeLayout)view.findViewById(R.id.recordControl);
        r1 = (RelativeLayout)view.findViewById(R.id.r1);
        btn_record = (RelativeLayout)view.findViewById(R.id.btn_record);
        img_record = (ImageView)view.findViewById(R.id.img_record) ;
        tts = new TextToSpeech(getContext(),this );

        progress_light = (WaveView)view.findViewById(R.id.progress_light) ;
        progress_heating = (WaveView)view.findViewById(R.id.progress_heating) ;
        progress_hum = (WaveView)view.findViewById(R.id.progress_hum) ;
        progress_nutrition = (WaveView)view.findViewById(R.id.progress_nutrition) ;
        objectAnimator = ObjectAnimator.ofFloat(fab_wind,"rotation",0,360);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setDuration(500);

        progress_water = (NumberProgressBar)view.findViewById(R.id.progress_water);
        progress_nut = (NumberProgressBar)view.findViewById(R.id.progress_nut);

        r1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                recordControl();
                return false;
            }
        });
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
                        nowState.setText("当前智能模式：打开");
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

                        if(msg.what == 0x003){
                            String str = bundle.getString("info");
                        Log.e("Ctrl收到：", str);
                        Infomation infomation = InfomationAnalysis.jsonToData(str);

                        progress_water.setProgress(((2000-100*Integer.parseInt(infomation.getWaterHigh()))/20));
                        progress_nut.setProgress(((3000-100*Integer.parseInt(infomation.getWaterHigh()))/30));
                        }
                        else if(msg.what == 0x004){
                            ((ImageView)msg.obj).getDrawable().setLevel(msg.arg1);
                        }else if(msg.what == 0x005){
                            hintDisease.setText("植物生病啦~点击查看详情");
                            hintDisease.setVisibility(View.VISIBLE);
                            redPoint.setVisibility(View.GONE);
                        }else if(msg.what == 0x006){
                            hintDisease.setVisibility(View.GONE);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

            }
        };
        handler.sendEmptyMessage(0x005);
        isDisease = true;
        hintDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintDisease.setText("你的植物可能得了:" + "叶片枯黄病");
            }
        });
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
                    if(isDisease)
                        redPoint.setVisibility(View.VISIBLE);
                    v.setSelected(false);
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    redPoint.setVisibility(View.GONE);
                    v.setSelected(true);
                }
            }

        });
        relativeLayout.setOnClickListener(null);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED ||bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    if(isDisease)
                        redPoint.setVisibility(View.VISIBLE);
                    imageView.setSelected(false);
                }else{
                    redPoint.setVisibility(View.GONE);
                    imageView.setSelected(true);
                }

            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED ||bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    if(isDisease)
                        redPoint.setVisibility(View.VISIBLE);
                    imageView.setSelected(false);
                }else{
                    redPoint.setVisibility(View.GONE);
                    imageView.setSelected(true);
                }
            }
        });
    }

    private void setDrawableLevel(final ImageView imageView, final int start, final int end){

        try {
            if(thread!=null) {
                thread.interrupt();
            }
        }catch (Exception  e){e.printStackTrace();}
        thread =  new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    int sLocation = start;
                    while (true) {
                        if (sLocation > end) {
                            sLocation -= 20;
                        } else if (sLocation < end) {
                            sLocation += 20;
                        }
                        else{
                            Log.e("info","操作完成");
                            break;}

                        Message message = Message.obtain();
                        message.what = 0x004;
                        message.arg1 = sLocation;
                        message.obj = imageView;
                        handler.sendMessage(message);
                        sleep(1);
                    }
                }catch (Exception e){}
            }
        };
        thread.start();
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
                                setDrawableLevel(fab_light,10000,0);
                                nowState.setText("当前灯光状态：关闭");
                                fab_light.setSelected(false);
                            } else {
                                Client.getClient("b");
                                setDrawableLevel(fab_light,0,10000);
                                nowState.setText("当前灯光状态：打开");
                                fab_light.setSelected(true);
                            }
                            break;
                        case R.id.fab_watering:
                            if (fab_watering.isSelected()) {
                                Client.getClient("1");
                                setDrawableLevel(fab_watering,10000,0);
                                nowState.setText("当前浇水状态：关闭");
                                fab_watering.setSelected(false);
                            } else {
                                Client.getClient("m");
                                setDrawableLevel(fab_watering,0,10000);
                                nowState.setText("当前浇水状态：打开");
                                fab_watering.setSelected(true);
                            }
                            break;
                        case R.id.fab_heating:
                            if (fab_heating.isSelected()) {
                                Client.getClient("s");
                                setDrawableLevel(fab_heating,10000,0);
                                nowState.setText("当前加热状态：关闭");
                                fab_heating.setSelected(false);
                            } else {
                                Client.getClient("p");
                                setDrawableLevel(fab_heating,0,10000);
                                nowState.setText("当前加热状态：打开");
                                fab_heating.setSelected(true);
                            }
                            break;
                        case R.id.fab_nut:
                            if (fab_nut.isSelected()) {
                                Client.getClient("y");
                                setDrawableLevel(fab_nut,10000,0);
                                nowState.setText("当前施肥状态：关闭");
                                fab_nut.setSelected(false);
                            } else {
                                Client.getClient("v");
                                setDrawableLevel(fab_nut,0,10000);
                                nowState.setText("当前施肥状态：打开");
                                DataFragment.handler.sendEmptyMessage(0x006);
                                fab_nut.setSelected(true);
                            }
                            break;
                        case R.id.fab_wind:
                            if (fab_wind.isSelected()) {
                                Client.getClient("8");
                                objectAnimator.pause();
                               // setDrawableLevel(fab_wind,10000,0);
                                nowState.setText("当前通风状态：关闭");
                                fab_wind.setSelected(false);
                            } else {
                                Client.getClient("5");
                                objectAnimator.start();
                             //   setDrawableLevel(fab_wind,0,10000);
                                nowState.setText("当前通风状态：打开");
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
        _player.setAudioOutput(false);
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
            /**
             * 计时器，定时对植物拍照
             */
            Timer timer=new Timer();
            TimerTask timerTask=new TimerTask() {
                @Override
                public void run() {
                    Bitmap bm= Bitmap.createScaledBitmap(_player.takePhoto(), 224, 224, false);
                    if(bm!=null)
                        Log.e("11111111111",bm.getWidth()+"       "+bm.getHeight()+"");
                    takePhotoEventListener(bm);
                }
            };
            timer.schedule(timerTask,10*1*1000);
//            .compress(Bitmap.CompressFormat.JPEG, 100, null)
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
     * 监听接口，对摄像头拍摄的bitmap进行植物病害识别
     * @param bitmap
     */
    private void takePhotoEventListener(Bitmap bitmap) {

        Log.e("info","开始病理识别");
        final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
        final String title = results.get(0).getTitle();
        final float confidence = results.get(0).getConfidence();
        Log.e("info",results.get(0).getTitle()+results.get(0).getConfidence());
        if(results.size()>0 && confidence>=10) {
            if(!"健康".equals(title.substring(title.length()-2))) {

                handler.sendEmptyMessage(0x005);
                isDisease = true;
                hintDisease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hintDisease.setText("你的植物可能得了:" + title);
                    }
                });
            }else{
                isDisease = false;
                handler.sendEmptyMessage(0x006);
            }
        }

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

    private void recordControl(){
        if(recordContent!=null&&recordContent.length()!=0)
            recordContent="";
        recordControl.setVisibility(View.VISIBLE);
        btn_record.setSelected(true);
        img_record.setSelected(true);
        mWaveView.start();
        setParam();
        int ret =mIat.startListening(mRecognizerListener);
        if(ret!= ErrorCode.SUCCESS){
            Log.e("big11","识别失败，错误码："+ret);
        }
    }
    public void setParam(){
        if(mIat==null)
            mIat= SpeechRecognizer.createRecognizer(getContext(),null);
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
    }
    private void printResult(RecognizerResult results) {
        String text = parseIatResult(results.getResultString());
        // 自动填写地址
        Log.e("111",text);
        recordContent += text;
    }
    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if(error.getErrorCode() == 14002) {
                Log.e("11",error.getPlainDescription(true)+"\n请确认是否已开通翻译功能" );
            } else {
                Log.e("11error",error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
            recordControl.setVisibility(View.GONE);
            btn_record.setSelected(false);
            img_record.setSelected(false);
            mWaveView.stop();
            mIat.cancel();
            if (!"".equals(recordContent)) {
                String str = recordContent;
              //  addData(new UserMessage(str));
                recordSendToService(str);
                recordContent = "";
            }
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("result11文字", results.getResultString());
            printResult(results);
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("11", "SpeechRecognizer init() code = " + code);
        }
    };
    public void recordSendToService(String str){
        switch (str) {
            case "开灯":
                Client.getClient("b");
                tts.speak("当前灯光状态:打开。",TextToSpeech.QUEUE_FLUSH,null);
                setDrawableLevel(fab_light,0,10000);
                nowState.setText("当前灯光状态：打开");
                fab_light.setSelected(true);
                break;
            case "关灯":
                Client.getClient("e");
                tts.speak("当前灯光状态:关闭。",TextToSpeech.QUEUE_FLUSH,null);
                setDrawableLevel(fab_light,10000,0);
                nowState.setText("当前灯光状态：关闭");
                fab_light.setSelected(false);
                break;
            case "浇水":
                Client.getClient("m");
                tts.speak("当前浇水状态:打开。",TextToSpeech.QUEUE_FLUSH,null);
                setDrawableLevel(fab_watering,0,10000);
                nowState.setText("当前浇水状态：打开");
                fab_watering.setSelected(true);
                break;
            case "停止浇水":
                Client.getClient("1");
                tts.speak("当前浇水状态:停止。",TextToSpeech.QUEUE_FLUSH,null);
                setDrawableLevel(fab_watering,10000,0);
                nowState.setText("当前浇水状态：关闭");
                fab_watering.setSelected(false);
                break;
            case "加热":
                Client.getClient("p");
                tts.speak("当前加热状态:打开。",TextToSpeech.QUEUE_FLUSH,null);
                setDrawableLevel(fab_heating,0,10000);
                nowState.setText("当前加热状态：打开");
                fab_heating.setSelected(true);
                break;
            case "停止加热":
                Client.getClient("s");
                tts.speak("当前加热状态:停止。",TextToSpeech.QUEUE_FLUSH,null);
                setDrawableLevel(fab_heating,10000,0);
                nowState.setText("当前加热状态：关闭");
                fab_heating.setSelected(false);
                break;
            case "施肥":
                Client.getClient("v");
                tts.speak("当前施肥状态:打开。",TextToSpeech.QUEUE_FLUSH,null);
                setDrawableLevel(fab_nut,0,10000);
                nowState.setText("当前施肥状态：打开");
                DataFragment.handler.sendEmptyMessage(0x006);
                fab_nut.setSelected(true);
                break;
            case "停止施肥":
                Client.getClient("y");
                tts.speak("当前施肥状态:停止.",TextToSpeech.QUEUE_FLUSH,null);
                setDrawableLevel(fab_nut,10000,0);
                nowState.setText("当前施肥状态：关闭");
                fab_nut.setSelected(false);
                break;
            case "通风":
                Client.getClient("5");
                tts.speak("当前通风状态:打开。",TextToSpeech.QUEUE_FLUSH,null);
                objectAnimator.start();
                //   setDrawableLevel(fab_wind,0,10000);
                nowState.setText("当前通风状态：打开");
                fab_wind.setSelected(true);
                break;
            case "停止通风":
                Client.getClient("8");
                tts.speak("当前通风状态:关闭。",TextToSpeech.QUEUE_FLUSH,null);
                objectAnimator.pause();
                // setDrawableLevel(fab_wind,10000,0);
                nowState.setText("当前通风状态：关闭");
                fab_wind.setSelected(false);
                break;
            case "打开智能模式":
                Client.getClient("smart" + MainActivity.flower.getFlowerName());
                tts.speak("当前智能模式:打开。",TextToSpeech.QUEUE_FLUSH,null);
                nowState.setText("当前智能模式：打开");
                fab_ctrl.setSelected(true);
                isSmart = true;
                break;
            case "关闭智能模式":
                Client.getClient("smart");
                tts.speak("当前智能模式:关闭。",TextToSpeech.QUEUE_FLUSH,null);
                nowState.setText("当前智能模式：关闭");
                fab_ctrl.setSelected(false);
                isSmart = false;
                break;
            default:
                if (str.length() >= 3 && str.substring(0, 2).equals("查询")){
                    tts.speak("正在为您查询",TextToSpeech.QUEUE_FLUSH,null);
                    nowState.setText("正在为您查询" + str.substring(2) + "。");
                    Client.getClient("search" + str.substring(2));
                }
                else if("你好".equals(str)){
                    tts.speak("你好",TextToSpeech.QUEUE_FLUSH,null);

                }else if("傻逼".equals(str.substring(str.length()-2))||"吃屎".equals(str.substring(str.length()-2))){
                    tts.speak("是的，我"+str,TextToSpeech.QUEUE_FLUSH,null);

                }else {
                    tts.speak("对不起，我不知道你在说什么。", TextToSpeech.QUEUE_FLUSH, null);
                }
        }
    }

    @Override
    public void onInit(int status) {
        // 判断是否转化成功
        if (status == TextToSpeech.SUCCESS){
            //默认设定语言为中文，原生的android貌似不支持中文。
            int result = tts.setLanguage(Locale.CHINESE);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                //   Toast.makeText(MainActivity.this, R.string.notAvailable, Toast.LENGTH_SHORT).show();
            }else{
                //不支持中文就将语言设置为英文
                tts.setLanguage(Locale.US);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if( null != mIat ){
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }
}
