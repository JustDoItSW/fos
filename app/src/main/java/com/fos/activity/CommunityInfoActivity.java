package com.fos.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fos.R;
import com.fos.entity.Community;
import com.fos.util.MyGridViewAdapter;

public class CommunityInfoActivity extends AppCompatActivity {

    private Intent intent;
    private Community community;
    private TextView userName,content,date,supportCount,browseCount,evaluateCount,shareCount;
    private Button sendEvaluate;
    private EditText evaluateContent;
    private RelativeLayout  exit_communityInfo;
    private GridView gridViewInfo;
    private ListView list_allEvaluate;
    private MyGridViewAdapter myGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);
        init();
        initGridView();
    }

    private void init(){
        intent = getIntent();
        community = (Community)intent.getSerializableExtra("info");

        exit_communityInfo = (RelativeLayout)findViewById(R.id.exit_communityInfo);
        sendEvaluate = (Button)findViewById(R.id.sendEvaluate);
        evaluateContent = (EditText)findViewById(R.id.evaluateContent);
        userName =  (TextView)findViewById(R.id.communityInfo_userName);
        content = (TextView)findViewById(R.id.comInfoContext);
        date = (TextView)findViewById(R.id.communityInfo_date);

        supportCount =  (TextView)findViewById(R.id.countInfo_support);
        browseCount =  (TextView)findViewById(R.id.countInfo_browse);
        evaluateCount =  (TextView)findViewById(R.id.countInfo_evaluate);
        shareCount =  (TextView)findViewById(R.id.countInfo_share);

        gridViewInfo =  (GridView)findViewById(R.id.gridViewInfo);
        list_allEvaluate =  (ListView) findViewById(R.id.list_allEvaluate);

        userName.setText(community.getUserInfo().getUserName()+"");
        date.setText(community.getTime()+"");
        content.setText(community.getContent()+"");

        supportCount.setText(community.getSupport()+"");
        browseCount.setText(community.getBrowse()+"");
        evaluateCount.setText(community.getEvaluate());
        shareCount.setText("999+");


        exit_communityInfo.setOnClickListener(onClickListener);
        sendEvaluate.setOnClickListener(onClickListener);

    }

    private void initGridView() {
        if (!community.getPicture().equals("")) {
            ViewGroup.LayoutParams para = gridViewInfo.getLayoutParams();
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int imageCount = getImageCount(community.getPicture());
            if (imageCount == 1) {
                para.height = (dm.widthPixels - 9) / 2;
                gridViewInfo.setNumColumns(1);
            }
            if (imageCount == 2) {
                para.height = (dm.widthPixels - 9) / 2;
                gridViewInfo.setNumColumns(2);
            }
            if (imageCount >= 3) {
                gridViewInfo.setNumColumns(3);
                para.height = (int) (Math.ceil(imageCount / 3f)) * ((dm.widthPixels - 12) / 3);
            }

            myGridViewAdapter = new MyGridViewAdapter(CommunityInfoActivity.this, R.layout.layout_gridview, getAllImageUri(community.getPicture()));
            gridViewInfo.setAdapter(myGridViewAdapter);
        }
    }

    private  String[] getAllImageUri(String str){
        String[] arr = str.split(";");
        return arr;
    }

    public int getImageCount(String str){
        String[] arr = str.split(";");
        return arr.length;

    }

    View.OnClickListener onClickListener  =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.exit_communityInfo:
                    finish();
                    break;
                case R.id.sendEvaluate:
                    break;

                    default:
                        break;

            }
        }
    };
}
