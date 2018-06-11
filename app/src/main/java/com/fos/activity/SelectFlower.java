package com.fos.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fos.R;
import com.fos.dao.FlowerDao;
import com.fos.entity.Flower;
import com.fos.entity.ServiceFlower;
import com.fos.myView.PopupWindowFactory;
import com.fos.service.netty.Client;
import com.fos.util.AudioRecoderUtils;
import com.fos.util.InfomationAnalysis;
import com.fos.util.MyListViewAdapter;
import com.fos.util.TimeUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.resource.Resource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SelectFlower extends AppCompatActivity {

    private RelativeLayout exit_selectFlower;
    private ListView listView;
    private LinearLayout layout_notFind_select;
    private TextView text_notFind_select,mTextView;
    private EditText edit_search_select;
    private FlowerDao flowerDao;
    private ImageView delSearch_select,mImageView;
    private List<Flower> data;//数据源
    private Map<String,Object> item;//数据项;
    private MyListViewAdapter myListViewAdapter;
    public static Handler handler;
    private Button btn_voice;
    private PopupWindowFactory  popupWindowFactory;
    private LinearLayout l1;
    private SpeechRecognizer mIat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIat=SpeechRecognizer.createRecognizer(SelectFlower.this,mInitListener);
        SpeechUtility.createUtility( this, SpeechConstant.APPID+"=5b1cb818"); //+SpeechConstant.FORCE_LOGIN+"=true"
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_flower);
        init();
      //  requestPermissions();
        initData();
        initListView();
    }

    public void init(){
        flowerDao  = FlowerDao.getInstance();
        l1 = (LinearLayout)findViewById(R.id.l1) ;
        exit_selectFlower = (RelativeLayout)findViewById(R.id.exit_selectFlower);
        data = new ArrayList<Flower>();//存放数据
        listView = (ListView)findViewById(R.id.list_flowerSelect);
        layout_notFind_select  =(LinearLayout)findViewById(R.id.layout_notFind_select);
        text_notFind_select = (TextView)findViewById(R.id.text_notFind_select);
        delSearch_select =(ImageView)findViewById(R.id.delSearch_select);
        edit_search_select = (EditText)findViewById(R.id.edit_search_select);
        btn_voice =findViewById(R.id.btn_voice);
        btn_voice.setOnClickListener(OnClickListener);
        l1 = (LinearLayout)findViewById(R.id.l1);
        View view = View.inflate(SelectFlower.this, R.layout.layout_microphone, null);
        popupWindowFactory = new PopupWindowFactory(this,view);
        mImageView = (ImageView) view.findViewById(R.id.iv_recording_icon);
        mTextView = (TextView) view.findViewById(R.id.tv_recording_time);


        exit_selectFlower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edit_search_select.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String flowerName = edit_search_select.getText().toString();
                if (!flowerName.equals("")) {
                    if (actionId== EditorInfo.IME_ACTION_SEARCH) {
                        ServiceFlower serviceFlower  =  new ServiceFlower();
                        serviceFlower.setFlowerName(flowerName);
                        Client.getClient("search"+flowerName);
                    }
                }
                return false;
            }
        });
        edit_search_select.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    delSearch_select.setVisibility(View.GONE);
                    updateData();
                    myListViewAdapter.notifyDataSetChanged();
                }
                else{
                    delSearch_select.setVisibility(View.VISIBLE);
                    if(Client.isExist()){
                        ServiceFlower serviceFlower  =  new ServiceFlower();
                        serviceFlower.setFlowerName(edit_search_select.getText().toString());
                        Client.getClient("search"+edit_search_select.getText().toString());
                    }else{
                        searchFlower(edit_search_select.getText().toString());
                    }
                }
            }
        });
        delSearch_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_search_select.setText("");
                updateData();
                myListViewAdapter.notifyDataSetChanged();
            }
        });


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String str = bundle.getString("info");
                if(msg.what == 0x003){
                    searchFlower(edit_search_select.getText().toString());
                    Log.e("info","植物不存在");
                }else {
                    Flower[] flower = InfomationAnalysis.jsonToFlower(str);
                    flowerDao.insertFlower(flower);
                    updateData();
                    searchFlower(edit_search_select.getText().toString());
                }
            }
        };


    }

    private void  initData(){
        Flower[]  flowers = flowerDao.getAllFlower();
        if(flowers!=null) {
            for (int i = 0; i < flowers.length; i++) {
                data.add(flowers[i]);
            }
        }
    }
    private void initListView(){
        myListViewAdapter  =  new MyListViewAdapter(SelectFlower.this,R.layout.layout_flowerlist,data);
        listView.setAdapter(myListViewAdapter);
        listView.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 更新数据源
     */
    private void  updateData(){
        data.clear();
        Flower[] flowers = flowerDao.getAllFlower();
        listView.setVisibility(View.VISIBLE);
        layout_notFind_select.setVisibility(View.GONE);
        if(flowers!=null) {
            for (int i = 0; i < flowers.length; i++) {
                data.add(flowers[i]);
            }
        }
    }
    /**
     * 搜索植物
     * @param str
     */
    private void searchFlower(String str){
        data.clear();
        Flower[] flowers  = flowerDao.searchFlower(str);
        if(flowers!=null){
            listView.setVisibility(View.VISIBLE);
            layout_notFind_select.setVisibility(View.GONE);
            for(int i= 0;i < flowers.length;i++){
                data.add(flowers[i]);
            }
        }
        else{
            listView.setVisibility(View.GONE);
            layout_notFind_select.setVisibility(View.VISIBLE);
        }
        myListViewAdapter.notifyDataSetChanged();
    }



    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e("info","选中的花名为:"+data.get(position).getFlowerName()+"");
            Bundle  bundle = new Bundle();
            bundle.putString("flowerName",data.get(position).getFlowerName());
            bundle.putString("flowerImage",data.get(position).getFlowerImage());
            bundle.putString("flowerInfo",data.get(position).getFlowerInfo());
            bundle.putString("light",data.get(position).getFlowerLux());
            bundle.putString("hum",data.get(position).getFlowerSoilHum());
            bundle.putString("temp",data.get(position).getFlowerTemp());
            bundle.putBoolean("isSelect",true);
            Intent intent = new Intent(SelectFlower.this,FlowerInfo.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
    /**
     * 录音的点击事件
     */
    View.OnClickListener OnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(edit_search_select.getText().toString().length()!=0)
                edit_search_select.setText("");
            setParam();
            popupWindowFactory.showAtLocation(l1,Gravity.CENTER, 0, 0);
            int ret =mIat.startListening(mRecognizerListener);
            if(ret!= ErrorCode.SUCCESS){
                Log.e("big11","识别失败，错误码："+ret);
            }
        }
    };

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
            mIat=SpeechRecognizer.createRecognizer(this,null);
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
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("result11文字", results.getResultString());
            printResult(results);
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            mImageView.getDrawable().setLevel((int) (3000 + 6000 * volume / 100));
            //mTextView.setText(TimeUtils.long2String(time));
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
        edit_search_select.append(text);
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
