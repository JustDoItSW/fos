package com.fos.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fos.R;
import com.fos.dao.FlowerDao;
import com.fos.entity.Flower;
import com.fos.entity.ServiceMessage;
import com.fos.entity.UserMessage;
import com.fos.fragment.ControlFragment;
import com.fos.myView.WaveView;
import com.fos.service.netty.Client;
import com.fos.util.InfomationAnalysis;
import com.fos.util.MyRecordAdapter;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class RecordControlActivity extends AppCompatActivity {

    private Flower flower;
    private Intent intent;
    public static Handler handler;
    private String recordContent = "";
    private WaveView mWaveView;
    private RelativeLayout btn_record,exit_record;
    private ImageView img_record;
    private ListView list_record;
    private List<Object> data;
    private MyRecordAdapter myRecordAdapter;
    private SpeechRecognizer mIat;

    private FlowerDao flowerDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIat=SpeechRecognizer.createRecognizer(RecordControlActivity.this,mInitListener);
        SpeechUtility.createUtility( this, SpeechConstant.APPID+"=5b1cb818"); //+SpeechConstant.FORCE_LOGIN+"=true"

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_control);
        allScreen();
        init();
        initListView();
    }

    private void allScreen(){
        if(Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //设置让应用主题内容占据状态栏和导航栏
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏和导航栏颜色为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }
    private void init(){
        flowerDao= FlowerDao.getInstance();
        intent = getIntent();
        flower = (Flower)intent.getSerializableExtra("Flower");
        mWaveView = (WaveView) findViewById(R.id.wave_view);
        btn_record = (RelativeLayout)findViewById(R.id.btn_record);
        exit_record = (RelativeLayout)findViewById(R.id.exit_record);
        img_record = (ImageView)findViewById(R.id.img_record) ;
        list_record = (ListView)findViewById(R.id.list_record);
        btn_record.setOnClickListener(onClickListener);
        exit_record.setOnClickListener(onClickListener);


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x003){
                    addData(new ServiceMessage("您查询的植物不存在"));
                }else{
                    addData(new ServiceMessage("已为您找的以下植物："));
                    Bundle bundle =  msg.getData();
                    String info = bundle.getString("info");
                    Flower[] flowers  = InfomationAnalysis.jsonToFlower(info);
                    flowerDao.insertFlower(flowers);
                    addData(flowers);
                }
            }
        };
    }
    private void initListView(){
        data = new ArrayList<>();
        myRecordAdapter =  new MyRecordAdapter(RecordControlActivity.this,data);
        list_record.setDividerHeight(3);
        list_record.setAdapter(myRecordAdapter);
    }

    private void addData(Object o){
        data.add(o);
        myRecordAdapter.notifyDataSetChanged();
    }
    private void recordSendToService(String str){
        switch (str) {
            case "开灯":
                Client.getClient("b");
                addData(new ServiceMessage("当前灯光状态:打开。"));
                if (ControlFragment.fab_light != null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "关灯":
                Client.getClient("e");
                addData(new ServiceMessage("当前灯光状态:关闭。"));
                if (ControlFragment.fab_light != null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "浇水":
                Client.getClient("m");
                addData(new ServiceMessage("当前浇水状态:开始。"));
                if (ControlFragment.fab_light != null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "停止浇水":
                Client.getClient("1");
                addData(new ServiceMessage("当前浇水状态:停止。"));
                if (ControlFragment.fab_light != null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "加热":
                Client.getClient("p");
                addData(new ServiceMessage("当前加热状态:开始。"));
                if (ControlFragment.fab_light != null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "停止加热":
                Client.getClient("s");
                addData(new ServiceMessage("当前加热状态:停止。"));
                if (ControlFragment.fab_light != null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "施肥":
                Client.getClient("v");
                addData(new ServiceMessage("当前施肥状态:开始。"));
                if (ControlFragment.fab_light != null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "停止施肥":
                Client.getClient("y");
                addData(new ServiceMessage("当前施肥状态:停止."));
                if (ControlFragment.fab_light != null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "开启智能模式":
                Client.getClient("smart" + flower.getFlowerName());
                addData(new ServiceMessage("当前智能模式:开启。"));
                if (ControlFragment.fab_light != null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            case "关闭智能模式":
                Client.getClient("smart");
                addData(new ServiceMessage("当前智能模式:关闭。"));
                if (ControlFragment.fab_light != null)
                    ControlFragment.fab_light.setSelected(false);
                break;
            default:
                if (str.length() >= 3 && str.substring(0, 2).equals("查询")){
                    addData(new ServiceMessage("正在为您查询" + str.substring(2) + "。"));
                    Client.getClient("search" + str.substring(2));
                }
                else if("你好".equals(str)){
                    addData(new ServiceMessage("你好。"));
                }else
                    addData(new ServiceMessage("对不起，我不知道你在说什么。"));
        }
    }
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btn_record:
                    if(recordContent!=null&&recordContent.length()!=0)
                        recordContent="";
                    controlRecord();
                    setParam();
                    int ret =mIat.startListening(mRecognizerListener);
                    if(ret!= ErrorCode.SUCCESS){
                        Log.e("big11","识别失败，错误码："+ret);
                    }
                    break;
                case R.id.exit_record:
                    finish();
                    break;
                    default:
                        break;
            }

        }
    };

    private void controlRecord(){
        if(btn_record.isSelected()) {
            btn_record.setSelected(false);
            img_record.setSelected(false);
            mWaveView.stop();

            mIat.cancel();
        }else {
            btn_record.setSelected(true);
            img_record.setSelected(true);
            mWaveView.start();

        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("11", "SpeechRecognizer init() code = " + code);
        }
    };

    /**
     * 参数设置
     * @return
     */
    public void setParam(){
        if(mIat==null)
            mIat= SpeechRecognizer.createRecognizer(this,null);
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
    }

    /**
     * 听写监听器。
     */
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
                controlRecord();
                if (!"".equals(recordContent)) {
                    String str = recordContent;
                    addData(new UserMessage(str));
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

    //回调结果：
    private void printResult(RecognizerResult results) {
        String text = parseIatResult(results.getResultString());
        // 自动填写地址
        Log.e("111",text);
        recordContent += text;
    }

    /**
     *  解析数据用
     * @param json
     * @return 解析好的数据
     */
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( null != mIat ){
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }

}
