package com.fos.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;
import com.fos.entity.Flower;
import com.fos.entity.UserInfo;
import com.fos.fragment.DataFragment;
import com.fos.service.netty.Client;
import com.fos.util.BitmapSetting;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LoadImageUtil;
import com.fos.util.TimeUtils;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowerInfo extends AppCompatActivity {

    public static final String PREFERENCE_NAME = "SaveContent";

    //Preferece机制的操作模式
    public static int MODE = MODE_PRIVATE;
    public static SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private RelativeLayout exit_flowerInfo;
    private ImageView image_flowerInfo;
    private Button btn_finish;
    private Intent intent;
    private TextView flowerName_info,table_flowerInfo;
    private ListView list_flowerInfo;
    private SimpleAdapter simpleAdapter;
    private Flower flower;
    private com.fos.entity.FlowerInfo flowerInfo;
    private String[] strData = {"土壤","光照","浇水","施肥"};
    private List<Map<String,String>> data;
    public static boolean isSelect ;
    private UserInfo userInfo;
    public static Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_info);
        init();
        initData();
        initListView();
    }

    private void init(){
        image_flowerInfo  = (ImageView)findViewById(R.id.image_flowerInfo);
        table_flowerInfo = (TextView)findViewById(R.id.table_flowerInfo);
        flowerName_info = (TextView)findViewById(R.id.flowerName_info);
        exit_flowerInfo = (RelativeLayout)findViewById(R.id.exit_flowerInfo);
        exit_flowerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_finish = (Button)findViewById(R.id.btn_finish);
        list_flowerInfo = (ListView)findViewById(R.id.list_flowerInfo) ;

        getIntentData();
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = TimeUtils.getCurrentTime();
                    sharedPreferences = getSharedPreferences(PREFERENCE_NAME,MODE);
                    editor = sharedPreferences.edit();
                    editor.putString("flowerName", flower.getFlowerName().toString());
                    editor.putString("light", flower.getFlowerLux().toString());
                    editor.putString("hum", flower.getFlowerSoilHum().toString());
                    editor.putString("temp", flower.getFlowerTemp().toString());
                    editor.putString("image", flower.getFlowerImage().toString());
                    editor.putString("date", date);
                    /**
                     * 这里选择了植物的花名，是否选择植物标识置为true
                     */
                    editor.commit();
                  MainActivity.isSelectedFlower=true;
                  MainActivity.flower = flower;
//                MainActivity.flower.setFlowerName(flower.getFlowerName().toString());
//                MainActivity.flower.setFlowerLux(flower.getFlowerLux().toString());
//                MainActivity.flower.setFlowerSoilHum(flower.getFlowerSoilHum().toString());
//                MainActivity.flower.setFlowerTemp(flower.getFlowerTemp().toString());
//                MainActivity.flower.setFlowerImage(flower.getFlowerImage().toString());
                 MainActivity.browseDate =  date;
                 if(DataFragment.handler!=null)
                DataFragment.handler.sendEmptyMessage(0x004);
                userInfo.setFlowerName(flower.getFlowerName()+"");
                userInfo.setType(6);
                Client.getClient(InfomationAnalysis.BeantoUserInfo(userInfo));
                btn_finish.setEnabled(false);;

            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0x001){
                    Toast.makeText(FlowerInfo.this,"植物选择成功！",Toast.LENGTH_SHORT).show();
                        Intent intent  =  new Intent(FlowerInfo.this,MainActivity.class);
                        intent.putExtra("UserInfo",userInfo);
                        startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(FlowerInfo.this, "植物选择失败，请重试！", Toast.LENGTH_SHORT).show();
                    btn_finish.setEnabled(true);
                }
            }
        };

    }
    private void getIntentData(){
        intent = getIntent();
        flower = (Flower)intent.getSerializableExtra("Flower");
        if(intent.getBooleanExtra("isSelect",false)) {
            table_flowerInfo.setText("您将要添加的植物是");
            btn_finish.setVisibility(View.VISIBLE);
            userInfo = (UserInfo)intent.getSerializableExtra("UserInfo");
        }else {
            table_flowerInfo.setText(flower.getFlowerName());
            btn_finish.setVisibility(View.GONE);
        }

        flowerName_info.setText(flower.getFlowerName().toString());
        Glide.with(FlowerInfo.this)
                .load(flower.getFlowerImage().toString())
                .transform(new BitmapSetting(FlowerInfo.this))
                .priority(Priority.HIGH)
                .into(image_flowerInfo);
        image_flowerInfo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        flowerInfo = InfomationAnalysis.jsonToFlowerInfo(flower.getFlowerInfo().toString());

    }

    private void initData(){
        String []strData2 = {flowerInfo.getSoil(),flowerInfo.getLight(),flowerInfo.getWater(),flowerInfo.getFertilize()};
        data = new ArrayList<>();
        for(int i = 0;i<strData2.length;i++){
            Map<String ,String> item = new HashMap<>();
            item.put("type",strData[i]);
            item.put("der",strData2[i]);
            data.add(item);
        }
    }
    private void initListView(){
        simpleAdapter = new SimpleAdapter(FlowerInfo.this,data,R.layout.layout_flowerinfo,new String[]{"type","der"},new int[]{R.id.flower_type,R.id.flower_der});
        list_flowerInfo.setAdapter(simpleAdapter);
    }

}
