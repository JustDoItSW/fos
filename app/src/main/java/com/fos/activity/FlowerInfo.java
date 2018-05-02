package com.fos.activity;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fos.R;
import com.fos.util.LoadImageUtil;

import org.w3c.dom.Text;

public class FlowerInfo extends AppCompatActivity {

    private RelativeLayout exit_flowerInfo;
    private ImageView image_flowerInfo;
    private Button btn_finish;
    private Intent intent;
    private TextView flowerName_info,table_flowerInfo;
    public static boolean isSelect ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_info);
        init();
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
        getIntentData();
    }

    public void getIntentData(){
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
    }

}
