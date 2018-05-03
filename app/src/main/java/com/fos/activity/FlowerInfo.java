package com.fos.activity;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fos.R;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LoadImageUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowerInfo extends AppCompatActivity {

    private RelativeLayout exit_flowerInfo;
    private ImageView image_flowerInfo;
    private Button btn_finish;
    private Intent intent;
    private TextView flowerName_info,table_flowerInfo;
    private ListView list_flowerInfo;
    private SimpleAdapter simpleAdapter;
    private com.fos.entity.FlowerInfo flowerInfo;
    private String[] strData = {"土壤","光照","浇水","施肥"};
    private List<Map<String,String>> data;
    public static boolean isSelect ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_info);
        init();
        getIntentData();
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

    }
    private void getIntentData(){
        intent = getIntent();
        if(intent.getExtras().getBoolean("isSelect")) {
            table_flowerInfo.setText("您将要添加的植物是");
            btn_finish.setVisibility(View.VISIBLE);
        }else {
            table_flowerInfo.setText("植物信息");
            btn_finish.setVisibility(View.GONE);
        }
        flowerName_info.setText(intent.getExtras().getString("flowerName"));
        LoadImageUtil.onLoadImage(image_flowerInfo, intent.getExtras().getString("flowerImage"));
        flowerInfo = InfomationAnalysis.jsonToFlowerInfo(intent.getExtras().getString("flowerInfo"));

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
