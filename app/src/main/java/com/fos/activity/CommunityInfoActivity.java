package com.fos.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;
import com.fos.entity.Community;
import com.fos.entity.Evaluate;
import com.fos.entity.UserInfo;
import com.fos.service.netty.Client;
import com.fos.util.BitmapSetting;
import com.fos.util.InfomationAnalysis;
import com.fos.util.MyEvaluateAdapter;
import com.fos.util.MyGridViewAdapter;
import com.fos.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class CommunityInfoActivity extends AppCompatActivity {

    private Intent intent;
    private ImageView image_comInfoIcon;
    private Community community;
    private TextView userName,content,date,supportCount,browseCount,evaluateCount,shareCount,noneEvaluate;
    private Button sendEvaluate;
    private EditText evaluateContent;
    private RelativeLayout  exit_communityInfo;
    private GridView gridViewInfo;
    private ListView list_allEvaluate;
    private MyGridViewAdapter myGridViewAdapter;
    private MyEvaluateAdapter  myEvaluateAdapter;
    private List<Evaluate> map ;
    private Evaluate[] evaluates;
    public static Handler handler;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);
        init();
        initGridView();
        initListView();
    }

    private void init(){
        intent = getIntent();
        community = (Community)intent.getSerializableExtra("Community");
        userInfo = (UserInfo)intent.getSerializableExtra("UserInfo") ;

        exit_communityInfo = (RelativeLayout)findViewById(R.id.exit_communityInfo);
        image_comInfoIcon = (ImageView)findViewById(R.id.image_comInfoIcon);
        sendEvaluate = (Button)findViewById(R.id.sendEvaluate);
        evaluateContent = (EditText)findViewById(R.id.evaluateContent);
        userName =  (TextView)findViewById(R.id.communityInfo_userName);
        content = (TextView)findViewById(R.id.comInfoContext);
        date = (TextView)findViewById(R.id.communityInfo_date);
        noneEvaluate=(TextView)findViewById(R.id.noneEvaluate);

        supportCount =  (TextView)findViewById(R.id.countInfo_support);
        browseCount =  (TextView)findViewById(R.id.countInfo_browse);
        evaluateCount =  (TextView)findViewById(R.id.countInfo_evaluate);
        shareCount =  (TextView)findViewById(R.id.countInfo_share);

        gridViewInfo =  (GridView)findViewById(R.id.gridViewInfo);
        list_allEvaluate =  (ListView) findViewById(R.id.list_allEvaluate);

        userName.setText(community.getUserInfo().getUserName()+"");
        date.setText(TimeUtils.dateBefore(community.getTime()));
        content.setText(community.getContent()+"");

        supportCount.setText(community.getSupport()+"");
        browseCount.setText(community.getBrowse()+"");
        evaluateCount.setText(community.getEvaluate()+"");
        shareCount.setText("999+");
        Glide.with(CommunityInfoActivity.this)
                .load(community.getUserInfo().getUserHeadImage())
                .transform(new BitmapSetting(CommunityInfoActivity.this))
                .priority(Priority.HIGH)
                .into(image_comInfoIcon);
        image_comInfoIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);

        supportCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()) {
                    v.setSelected(false);
                    community.setType(5);
                    Client.getClient(InfomationAnalysis.BeanToCommunity(community));
                    supportCount.setText((Integer.parseInt(supportCount.getText().toString()) - 1) + "");
                }else{
                    v.setSelected(true);
                    community.setType(4);
                    Client.getClient(InfomationAnalysis.BeanToCommunity(community));
                    supportCount.setText((Integer.parseInt(supportCount.getText().toString()) + 1) + "");
                }
            }
        });


        exit_communityInfo.setOnClickListener(onClickListener);
        sendEvaluate.setOnClickListener(onClickListener);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle  bundle = msg.getData();
                String info  = bundle.getString("info");
                evaluates  = InfomationAnalysis.JsonToEvaluate(info);
                evaluateContent.setText("");
                evaluateContent.setEnabled(true);
                sendEvaluate.setEnabled(true);
                if(evaluates[0].getType() ==  1){
                    Toast.makeText(CommunityInfoActivity.this,"评论失败！",Toast.LENGTH_SHORT).show();
                }else if(evaluates[0].getType() ==  0){
                    Toast.makeText(CommunityInfoActivity.this, "评论成功！", Toast.LENGTH_SHORT).show();
                    refreshListView(evaluates);
                }else{
                    refreshListView(evaluates);
                }
            }
        };
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

    private void initListView(){
            map = new ArrayList<Evaluate>();
            myEvaluateAdapter = new MyEvaluateAdapter(CommunityInfoActivity.this,R.layout.layout_evaluate,map);
            list_allEvaluate.setAdapter(myEvaluateAdapter);
            if(community.getEvaluate()==0){
                noneEvaluate.setVisibility(View.VISIBLE);
                list_allEvaluate.setVisibility(View.INVISIBLE);
            }else{
                Evaluate evaluate = new Evaluate();
                evaluate.setCommunityID(community.getId());
                evaluate.setType(1);
                Client.getClient(InfomationAnalysis.BeanToEvaluate(evaluate));
            }
    }

    private void refreshListView(Evaluate[] evaluates){
        map.clear();
        if(evaluates.length>0){
            noneEvaluate.setVisibility(View.GONE);
            list_allEvaluate.setVisibility(View.VISIBLE);
        }
        for(int i=0;i<evaluates.length;i++){
            map.add(evaluates[i]);
        }
        myEvaluateAdapter.notifyDataSetChanged();
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
                    if(evaluateContent.getText().toString().equals(""))
                        Toast.makeText(CommunityInfoActivity.this,"评论内容不能为空！",Toast.LENGTH_SHORT).show();
                    else {
                        evaluateContent.setEnabled(false);
                        sendEvaluate.setEnabled(false);
                        Evaluate evaluate = new Evaluate();
                        evaluate.setUserInfo(userInfo);
                        evaluate.setContent(evaluateContent.getText().toString());
                        evaluate.setDate(TimeUtils.getCurrentTime());
                        evaluate.setCommunityID(community.getId());
                        evaluate.setType(0);
                        Client.getClient(InfomationAnalysis.BeanToEvaluate(evaluate));
                    }
                    break;
                    default:
                        break;

            }
        }
    };
}
