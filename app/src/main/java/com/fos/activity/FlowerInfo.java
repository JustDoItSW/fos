package com.fos.activity;

import android.content.Intent;
import android.media.Image;
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

import com.fos.R;
import com.fos.entity.Flower;
import com.fos.fragment.DataFragment;
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
    private Flower flower;
    private com.fos.entity.FlowerInfo flowerInfo;
    private String[] strData = {"土壤","光照","浇水","施肥"};
    private List<Map<String,String>> data;
    public static boolean isSelect ;
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
                MainActivity.editor.putString("flowerName",flower.getFlowerName().toString());
                MainActivity.editor.putString("light",flower.getFlowerLux().toString());
                MainActivity.editor.putString("hum",flower.getFlowerSoilHum().toString());
                MainActivity.editor.putString("temp",flower.getFlowerTemp().toString());
                MainActivity.editor.putString("image",flower.getFlowerImage().toString());
                //   MainActivity.editor.putString("nut",data.get(position).getFlowerN());
                MainActivity.editor.commit();

                MainActivity.flower.setFlowerName(flower.getFlowerName().toString());
                /**
                 * 这里选择了植物的花名，是否选择植物标识置为true
                 */
                MainActivity.isSelectedFlower=true;

                MainActivity.flower.setFlowerLux(flower.getFlowerLux().toString());
                MainActivity.flower.setFlowerSoilHum(flower.getFlowerSoilHum().toString());
                MainActivity.flower.setFlowerTemp(flower.getFlowerTemp().toString());
                MainActivity.flower.setFlowerImage(flower.getFlowerImage().toString());


                Message message = new Message();
                message.what = 0x004;
                message.setData(new Bundle());
                DataFragment.handler.sendMessage(message);
                finish();

            }
        });

    }
    private void getIntentData(){
        intent = getIntent();
        if(intent.getExtras().getBoolean("isSelect")) {
            table_flowerInfo.setText("您将要添加的植物是");
            btn_finish.setVisibility(View.VISIBLE);
        }else {
            table_flowerInfo.setText(intent.getExtras().getString("flowerName"));
            btn_finish.setVisibility(View.GONE);
        }
        flower = new Flower();
        flower.setFlowerInfo(intent.getExtras().getString("flowerInfo"));
        flower.setFlowerTemp(intent.getExtras().getString("temp"));
        flower.setFlowerLux(intent.getExtras().getString("light"));
        flower.setFlowerSoilHum(intent.getExtras().getString("hum"));
        flower.setFlowerName(intent.getExtras().getString("flowerName"));
        flower.setFlowerImage(intent.getExtras().getString("flowerImage"));
        flowerName_info.setText(flower.getFlowerName().toString());
        LoadImageUtil.onLoadImage(image_flowerInfo, flower.getFlowerImage().toString());
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
