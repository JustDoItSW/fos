package com.fos.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fos.R;
import com.fos.entity.Community;
import com.fos.entity.UserInfo;
import com.fos.service.netty.Client;
import com.fos.util.InfomationAnalysis;
import com.fos.util.MyCommunityListAdapter;
import com.fos.util.MyGridViewAdapter;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityActivity extends AppCompatActivity {


    private Button createCom;
    private RefreshLayout refreshLayout;
    private GridLayout grid_image;
    private RelativeLayout exit_community;
    private ListView listView;
    private List<Community> maps = new ArrayList<>();;
    private MyCommunityListAdapter myCommunityListAdapter;
    public static  Handler handler;
    private Intent intent;
    public static UserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        init();
        initData(null);
        initListView();
        Community community  = new Community();
        community.setType(2);
        Client.getClient(InfomationAnalysis.BeanToCommunity(community));

    }

    private void init(){
        intent = getIntent();
        userInfo = (UserInfo)intent.getSerializableExtra("UserInfo");
        createCom = (Button)findViewById(R.id.createCom);
        exit_community = (RelativeLayout)findViewById(R.id.exit_community);
        listView = (ListView)findViewById(R.id.allCommunity);
        refreshLayout  = (RefreshLayout)findViewById(R.id.refreshLayout);
        MaterialHeader materialHeader = new MaterialHeader(this);
        WaterDropHeader waterDropHeader = new WaterDropHeader(this);

        createCom.setOnClickListener(onClickListener);
        exit_community.setOnClickListener(onClickListener);

        materialHeader.setShowBezierWave(false);
        materialHeader.setColorSchemeColors(Color.rgb(44,153,233));
        waterDropHeader.setPrimaryColors(Color.rgb(44,153,233));

        refreshLayout.setRefreshHeader(materialHeader);
        refreshLayout.setDisableContentWhenRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                Community community  = new Community();
                community.setType(2);
                Client.getClient(InfomationAnalysis.BeanToCommunity(community));
                refreshlayout.finishRefresh(1000);
                myCommunityListAdapter.notifyDataSetChanged();

            }
        });


        handler =  new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle =msg.getData();
                initData(bundle.getString("info"));
            }
        };
    }

    private void initData(String data){
        maps.clear();
        if(data!=null){
            Community[] communities = InfomationAnalysis.jsonToCommunity(data);
            for(int  i = 0;i<communities.length;i++){
                maps.add(communities[i]);
            }
        }
        if(myCommunityListAdapter!=null)
        myCommunityListAdapter.notifyDataSetChanged();
    }

    private void initListView(){
        myCommunityListAdapter = new MyCommunityListAdapter(CommunityActivity.this,R.layout.layout_communitylist,maps);
        listView.setAdapter(myCommunityListAdapter);
        listView.setOnItemClickListener(onItemClickListener);
    }




    View.OnClickListener  onClickListener  =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.exit_community:
                    finish();
                    break;
                case  R.id.new_community:
                    break;
                case R.id.createCom:
                    Intent intent = new Intent(CommunityActivity.this,CreateCommunityActivity.class);
                    intent.putExtra("UserInfo",userInfo);
                    startActivity(intent);
                    default:
                        break;
            }
        }
    };

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(CommunityActivity.this, CommunityInfoActivity.class);
            intent.putExtra("Community",maps.get(position));
            intent.putExtra("UserInfo",userInfo);
            startActivity(intent);
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        Community community  = new Community();
        community.setType(2);
        Client.getClient(InfomationAnalysis.BeanToCommunity(community));
    }
}
