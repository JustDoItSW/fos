package com.fos.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fos.R;
import com.fos.entity.Community;
import com.fos.util.InfomationAnalysis;
import com.fos.util.MyCommunityListAdapter;
import com.fos.util.MyGridViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityActivity extends AppCompatActivity {


    private Button createCom;
    private GridLayout grid_image;
    private RelativeLayout exit_community;
    private ListView listView;
    private List<Map<String ,Object>> maps = new ArrayList<>();;
    private Map<String ,Object>  item;
    private MyCommunityListAdapter myCommunityListAdapter;
    public Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        init();
        initData(null);
        initListView();
    }

    private void init(){
        createCom = (Button)findViewById(R.id.createCom);
        exit_community = (RelativeLayout)findViewById(R.id.exit_community);
        listView = (ListView)findViewById(R.id.allCommunity);

        createCom.setOnClickListener(onClickListener);
        exit_community.setOnClickListener(onClickListener);


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
//        if(data!=null){
//            Community[] communities = InfomationAnalysis.jsonToCommunity(data);
//            for(int  i = 0;i<communities.length;i++){
//                item = new HashMap<String, Object>();
//                item.put("userName",InfomationAnalysis.jsonToUserInfo(communities[i].getUserInfo()).getUserName());
//                item.put("data",communities[i].getContent());
//                item.put("picture",communities[i].getPicture());
//                item.put("count_support",communities[i].getSupport());
//                item.put("count_browse",communities[i].getBrowse());
//                item.put("count_share","999+");
//                item.put("count_evaluate",communities[i].getEvaluate());
//                maps.add(item);
//            }
//        }


        item = new HashMap<String, Object>();
        item.put("userName","曾勇胜");
        item.put("data","2018-5-20 13:14");
        item.put("picture","https://avatar.csdn.net/B/C/1/3_sunsteam.jpg;https://avatar.csdn.net/B/C/1/3_sunsteam.jpg;https://avatar.csdn.net/B/C/1/3_sunsteam.jpg;https://avatar.csdn.net/B/C/1/3_sunsteam.jpg;https://avatar.csdn.net/B/C/1/3_sunsteam.jpg;https://avatar.csdn.net/B/C/1/3_sunsteam.jpg");
        item.put("comContext","今天是个快乐的一天。");
        item.put("count_support","0");
        item.put("count_browse","0");
        item.put("count_share","0");
        item.put("count_evaluate","0");
        maps.add(item);



        item = new HashMap<String, Object>();
        item.put("userName","Apersonalive");
        item.put("data","2018-5-20 13:14");
        item.put("picture","http://img0.imgtn.bdimg.com/it/u=1494557966,4236915692&fm=27&gp=0.jpg;http://img0.imgtn.bdimg.com/it/u=1494557966,4236915692&fm=27&gp=0.jpg");
        item.put("comContext","这是内容。");
        item.put("count_support","0");
        item.put("count_browse","0");
        item.put("count_share","0");
        item.put("count_evaluate","0");
        maps.add(item);

        item = new HashMap<String, Object>();
        item.put("userName","Apersonalive");
        item.put("data","2018-5-20 13:14");
        item.put("picture","http://img1.imgtn.bdimg.com/it/u=1966746834,3552635933&fm=27&gp=0.jpg;http://img1.imgtn.bdimg.com/it/u=1966746834,3552635933&fm=27&gp=0.jpg;http://img1.imgtn.bdimg.com/it/u=1966746834,3552635933&fm=27&gp=0.jpg;http://img1.imgtn.bdimg.com/it/u=1966746834,3552635933&fm=27&gp=0.jpg;http://img1.imgtn.bdimg.com/it/u=1966746834,3552635933&fm=27&gp=0.jpg;http://img1.imgtn.bdimg.com/it/u=1966746834,3552635933&fm=27&gp=0.jpg;http://img1.imgtn.bdimg.com/it/u=1966746834,3552635933&fm=27&gp=0.jpg");
        item.put("comContext","这是内容。");
        item.put("count_support","0");
        item.put("count_browse","0");
        item.put("count_share","0");
        item.put("count_evaluate","0");
        maps.add(item);

        if(myCommunityListAdapter!=null)
        myCommunityListAdapter.notifyDataSetChanged();
    }

    private void initListView(){
        myCommunityListAdapter = new MyCommunityListAdapter(CommunityActivity.this,R.layout.layout_communitylist,maps);
        listView.setItemsCanFocus(true);
        //listView.setEnabled(false);
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
                    startActivity(intent);

                    default:
                        break;
            }
        }
    };

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
}
