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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Button btn_record;
    private AudioRecoderUtils audioRecoderUtils;
    private PopupWindowFactory  popupWindowFactory;
    private LinearLayout l1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        btn_record  =(Button)findViewById(R.id.btn_record);
        audioRecoderUtils = new AudioRecoderUtils();
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
        btn_record.setOnTouchListener(onTouchListener);

        audioRecoderUtils.setOnAudioStatusUpdateListener(onAudioStatusUpdateListener);

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

    private void requestPermissions() {
        //判断是否开启摄像头权限
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                ) {
            btn_record.setOnTouchListener(onTouchListener);

            //判断是否开启语音权限
        } else {
            //请求获取摄像头权限
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 66);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 66) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED) ) {
                btn_record.setOnTouchListener(onTouchListener);
            } else {
                Toast.makeText(this, "已拒绝权限！", Toast.LENGTH_SHORT).show();
            }
        }
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

    View.OnTouchListener  onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:

                    popupWindowFactory.showAtLocation(l1, Gravity.CENTER, 0, 0);
                    btn_record.setText("松开结束");
                    audioRecoderUtils.startRecord();
                    break;

                case MotionEvent.ACTION_UP:

                   // audioRecoderUtils.stopRecord();        //结束录音（保存录音文件）
                    audioRecoderUtils.cancelRecord();    //取消录音（不保存录音文件）
                    popupWindowFactory.dismiss();
                    btn_record.setText("按住说话");
                    break;
            }
            return true;
        }
    };

    AudioRecoderUtils.OnAudioStatusUpdateListener onAudioStatusUpdateListener = new AudioRecoderUtils.OnAudioStatusUpdateListener() {
        @Override
        public void onUpdate(double db, long time) {
            mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
            mTextView.setText(TimeUtils.long2String(time));
        }

        @Override
        public void onStop(String filePath) {
            mTextView.setText(TimeUtils.long2String(0));
        }
    };

}
