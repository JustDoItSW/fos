package com.fos.activity;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;
import com.fos.dao.UserInfoDao;
import com.fos.entity.User;
import com.fos.entity.UserInfo;
import com.fos.service.netty.Client;
import com.fos.util.BitmapSetting;
import com.fos.util.InfomationAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    private RelativeLayout  exit_userInfo;
    private ListView listView_userInfo;
    private TextView changeIcon,userInfo_userName,photograph,album;
    private ImageView userInfo_icon;
    private Intent intent;
    private String  userInfo[];
    private List<Map<String,Object>> mapList;
    private Map<String,Object> item;
    private SimpleAdapter simpleAdapter;
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        createMyDialog();
        init();
        initListView();
    }

    private void init(){
        intent = getIntent();
        exit_userInfo =  (RelativeLayout)findViewById(R.id.exit_userInfo);
        listView_userInfo = (ListView)findViewById(R.id.listView_userInfo);
        changeIcon= (TextView)findViewById(R.id.changeIcon);
        userInfo_userName = (TextView)findViewById(R.id.userInfo_userName);
        userInfo_icon = (ImageView)findViewById(R.id.userInfo_icon);

        userInfo_userName.setText(intent.getExtras().getString("userName"));
        Glide.with(UserInfoActivity.this)
                .load(MainActivity.userInfo.getUserHeadImage())
                .priority(Priority.HIGH)
                .into(userInfo_icon);
        userInfo_icon.setScaleType(ImageView.ScaleType.CENTER_CROP);

        exit_userInfo.setOnClickListener(onClickListener);
        changeIcon.setOnClickListener(onClickListener);
    }

    private void initListView(){
        String content[] = {"账号","昵称"};
        userInfo = new  String[]{MainActivity.userInfo.getUserId(),MainActivity.userInfo.getUserName()};
        mapList =new   ArrayList<>();
        for(int i =0;i<content.length;i++){
            item = new HashMap<>();
            item.put("content",content[i]);
            item.put("userInfo",userInfo[i]);
            mapList.add(item);
        }
        simpleAdapter = new SimpleAdapter(UserInfoActivity.this,mapList,R.layout.layout_userinfolist
                ,new String[]{"content","userInfo"},new int[]{R.id.userInfoList_content,R.id.userInfoList_userInfo});
        listView_userInfo.setAdapter(simpleAdapter);


    }

    private void createMyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
        dialog  = builder.create();
        View dialogView = View.inflate(UserInfoActivity.this,R.layout.selectimage,null);
        dialog.setView(dialogView);
        photograph = (TextView)dialogView.findViewById(R.id.photograph);
        album = (TextView)dialogView.findViewById(R.id.album);

        photograph.setOnClickListener(onClickListener);//拍照
        album.setOnClickListener(onClickListener);//相册

    }

    /**
     * 获得 uri后调用次方法
     * @param uri
     */

    private void resetIcon(String uri){
        /**
         * 更新 ui
         */
        Glide.with(UserInfoActivity.this)
                .load(uri)
                .priority(Priority.HIGH)
                .into(userInfo_icon);
        Glide.with(UserInfoActivity.this)
                .load(uri)
                .transform(new BitmapSetting(UserInfoActivity.this))
                .priority(Priority.HIGH)
                .into(MainActivity.user_icon);
        MainActivity.userInfo.setUserHeadImage(uri);

        /**
         * 发送数据至服务器
         */
        UserInfo user = new UserInfo();
        user.setUserId(MainActivity.userInfo.getUserId());
        user.setUserName(MainActivity.userInfo.getUserName());
        user.setUserHeadImage(uri);
        Client.getClient(InfomationAnalysis.BeantoUserInfo(user));

        /**
         * 更新 数据库
         */
        User user2 = new User();
        user2.setUserId(MainActivity.userInfo.getUserId());
        user2.setUserName(MainActivity.userInfo.getUserName());
        user2.setUserHeadImage(uri);
        UserInfoDao.getInstance().insertUserInfo(user2);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.exit_userInfo:
                    finish();
                    break;
                case R.id.changeIcon:
                    dialog.show();
                    break;
                case R.id.photograph:
                    dialog.dismiss();
                    break;
                case R.id.album:
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };
}
