package com.fos.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;
import com.fos.dao.UserInfoDao;
import com.fos.entity.User;
import com.fos.entity.UserInfo;
import com.fos.service.netty.Client;
import com.fos.service.netty.SimpleClient;
import com.fos.util.BitmapSetting;
import com.fos.util.InfomationAnalysis;
import com.fos.util.MyLoginListView;

import org.litepal.crud.DataSupport;
import org.litepal.util.Const;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFERENCE_NAME = "UserContent";
    //Preferece机制的操作模式
    public static int MODE = MODE_PRIVATE;
    public static SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private Button btn_login, btn_register;
    private EditText login_userID, login_password, register_userName, register_password, register_certainPW;
    private RelativeLayout login_ui, register_ui;
    private ImageView login_userIcon, downPush;
    private LinearLayout backLogin;
    private TextView login_register, findPassword;
    private LinearLayout ll_input;
    private ObjectAnimator objectAnimator, objectAnimator2, objectAnimator3, objectAnimator4, objectAnimator5, objectAnimator6, objectAnimator7, objectAnimator8, objectAnimator9, objectAnimator10;
    private View view_rotation, view2_rotation;
    private ListView list_userID;
    private User user = new User();
  //  private List<String> userList;
    private UserInfoDao userInfoDao;
    private List<User> userInfoList;
    private String ip = "47.106.161.42";
    private int port = 8000;
    // public static SimpleClient Client_phone;
    public static Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        getCountInfo();
        initAnimator();
        objectAnimator.start();
        objectAnimator3.start();
        objectAnimator9.start();

    }

    private void initView() {
        userInfoDao = UserInfoDao.getInstance();
        btn_login = (Button) findViewById(R.id.btn_login);
        login_password = (EditText) findViewById(R.id.login_password);
        login_userID = (EditText) findViewById(R.id.login_userID);
        login_userIcon = (ImageView) findViewById(R.id.login_userIcon);
        downPush = (ImageView) findViewById(R.id.downPush);
        login_register = (TextView) findViewById(R.id.login_register);
        findPassword = (TextView) findViewById(R.id.findPassword);
        ll_input = (LinearLayout) findViewById(R.id.ll_input);
        view_rotation = (View) findViewById(R.id.view_rotation);
        list_userID = (ListView) findViewById(R.id.list_userID);
        login_ui = (RelativeLayout) findViewById(R.id.login_ui);
        register_ui = (RelativeLayout) findViewById(R.id.register_ui);
        backLogin = (LinearLayout) findViewById(R.id.backLogin);
        view2_rotation = (View) findViewById(R.id.view2_rotation);
        register_userName = (EditText) findViewById(R.id.register_userName);
        register_password = (EditText) findViewById(R.id.register_password);
        register_certainPW = (EditText) findViewById(R.id.register_certainPW);
        btn_register = (Button) findViewById(R.id.btn_register);

        downPush.setOnClickListener(onClickListener);
        btn_login.setOnClickListener(onClickListener);
        login_register.setOnClickListener(onClickListener);
        backLogin.setOnClickListener(onClickListener);
        register_userName.setOnClickListener(onClickListener);
        btn_register.setOnClickListener(onClickListener);
        list_userID.setOnItemClickListener(onItemClickListener);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String str = bundle.getString("info");
                Log.e("Login收到：", str);
                if (msg.what == 0x001) {
                    /**
                     * 注册成功
                     */
                    UserInfo userInfo = InfomationAnalysis.jsonToUserInfo(str);
                    objectAnimator8.start();

                    login_userID.setText(userInfo.getUserId().toString());
                    login_password.setText(register_password.getText().toString());
                    btn_register.setEnabled(true);
                    btn_register.setText("注册");
                    register_userName.setText("");
                    register_password.setText("");
                    register_certainPW.setText("");
                    register_userName.setEnabled(true);
                    register_password.setEnabled(true);
                    register_certainPW.setEnabled(true);
                    backLogin.setEnabled(true);
                } else if (msg.what == 0x002) {
                    /**
                     * 登录成功
                     */
                    user.setUserName(InfomationAnalysis.jsonToUserInfo(str).getUserName());
                    user.setUserHeadImage(InfomationAnalysis.jsonToUserInfo(str).getUserHeadImage());
                    saveCountInfo();
                    Bundle b = new Bundle();
                    b.putString("userName", user.getUserName());
                    b.putString("userID", user.getUserId());
                    b.putString("userIcon",user.getUserHeadImage());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();

                } else if (msg.what == 0x003) {
                    Toast.makeText(LoginActivity.this, "账号或密码不正确！", Toast.LENGTH_SHORT).show();
                    login_userID.setEnabled(true);
                    login_password.setEnabled(true);
                    btn_login.setEnabled(true);
                    btn_login.setText("登录");
                }
            }
        };


    }

    private void initAnimator() {

        objectAnimator = ObjectAnimator.ofFloat(view_rotation, "rotation", 0, 15);
        objectAnimator2 = ObjectAnimator.ofFloat(view_rotation, "rotation", 15, 0);
        objectAnimator.setDuration(500);
        objectAnimator2.setDuration(500);

        objectAnimator3 = ObjectAnimator.ofFloat(ll_input, "translationY", 0, 100);
        objectAnimator4 = ObjectAnimator.ofFloat(ll_input, "translationY", 100, 0);
        objectAnimator3.setDuration(500);
        objectAnimator4.setDuration(500);

        objectAnimator5 = ObjectAnimator.ofFloat(login_ui, "rotationY", 0, 90);
        objectAnimator6 = ObjectAnimator.ofFloat(login_ui, "rotationY", 90, 0);
        objectAnimator5.setDuration(500);
        objectAnimator6.setDuration(500);

        objectAnimator7 = ObjectAnimator.ofFloat(register_ui, "rotationY", -90, 0);
        objectAnimator8 = ObjectAnimator.ofFloat(register_ui, "rotationY", 0, -90);
        objectAnimator7.setDuration(500);
        objectAnimator8.setDuration(500);

        objectAnimator9 = ObjectAnimator.ofFloat(view2_rotation, "rotation", 0, -15);
        objectAnimator10 = ObjectAnimator.ofFloat(view2_rotation, "rotation", -15, 0);
        objectAnimator9.setDuration(500);
        objectAnimator10.setDuration(500);

        objectAnimator5.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                login_ui.setVisibility(View.GONE);
                register_ui.setVisibility(View.VISIBLE);
                objectAnimator7.start();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator8.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                register_ui.setVisibility(View.GONE);
                login_ui.setVisibility(View.VISIBLE);
                objectAnimator6.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }

    private void saveCountInfo() {

      //  DataSupport.deleteAll("user");
        user.setUserId(login_userID.getText().toString());
        user.setUserPassword(login_password.getText().toString());
        userInfoDao.insertUserInfo(user);

//        sharedPreferences = getSharedPreferences(PREFERENCE_NAME,MODE);
//        editor = sharedPreferences.edit();
//        Set<String> idSet;
//        idSet = sharedPreferences.getStringSet("userID",null);
//        if(idSet!=null){
//            idSet.add(login_userID.getText().toString());
//            editor.remove("userID");
//            editor.commit();
//            editor.putStringSet("userID",idSet);
//            editor.commit();
//        }else{
//            idSet = new HashSet<String>();
//            idSet.add(login_userID.getText().toString());
//            editor.remove("userID");
//            editor.commit();
//            editor.putStringSet("userID",idSet);
//            editor.commit();
//        }
    }

    private void getCountInfo() {
        User[] userInfos = userInfoDao.getAllUserInfo();
        userInfoList = new ArrayList<>();
        if (userInfos != null) {
            for (int i = 0; i < userInfos.length; i++) {
                userInfoList.add(userInfos[i]);
            }
        }
//        sharedPreferences = getSharedPreferences(PREFERENCE_NAME,MODE);
//        editor = sharedPreferences.edit();
//        Set<String> idSet;
//        idSet = sharedPreferences.getStringSet("userID",null);
//        if(idSet != null){
//            List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
//            Map<String,Object> item;
//            userList = new ArrayList<>(idSet);
//            for(int i = 0;i<userList.size();i++){
//                item = new HashMap<String, Object>();
//                item.put("userID",userList.get(i));
//                item.put("userIcon",null);
//                data.add(item);
//            }
        MyLoginListView myLoginListView = new MyLoginListView(LoginActivity.this, userInfoList);
        //   SimpleAdapter simpleAdapter = new SimpleAdapter(LoginActivity.this,data,R.layout.layout_userid,new String[]{"userIcon","userID"},new int[]{R.id.userIconList,R.id.userIDList});
        list_userID.setAdapter(myLoginListView);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            userInfoDao = UserInfoDao.getInstance();
            user =  userInfoDao.getUserInfo(userInfoList.get(position).getUserId());
            login_userID.setText(user.getUserId());
            login_password.setText(user.getUserPassword());
            Glide.with(LoginActivity.this)
                    .load(user.getUserHeadImage())
                    .transform(new BitmapSetting(LoginActivity.this))
                    .priority(Priority.HIGH)
                    .into(login_userIcon);
            login_userIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            downPush.setSelected(false);
            objectAnimator.start();
            objectAnimator3.start();
            list_userID.setVisibility(View.GONE);
            login_password.setVisibility(View.VISIBLE);
        }
    };
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.downPush:

                    if(!v.isSelected()){
                        v.setSelected(true);
                        objectAnimator2.start();
                        objectAnimator4.start();
                        getCountInfo();
                        list_userID.setVisibility(View.VISIBLE);
                        login_password.setVisibility(View.GONE);

                    }else {
                        v.setSelected(false);
                        objectAnimator.start();
                        objectAnimator3.start();
                        list_userID.setVisibility(View.GONE);
                        login_password.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.btn_login:
                    if(login_userID.getText().toString().equals("")||login_password.getText().toString().equals("")){
                        Toast.makeText(LoginActivity.this,"账号或密码不能为空！",Toast.LENGTH_SHORT).show();
                    }else {
                            if(login_password.getText().toString().length()>16){
                                Toast.makeText(LoginActivity.this,"密码不能超过16位！",Toast.LENGTH_SHORT).show();
                            }else {
                                login_userID.setEnabled(false);
                                login_password.setEnabled(false);
                                btn_login.setEnabled(false);
                                btn_login.setText("正在登录");
                                final UserInfo userInfo = new UserInfo();
                                userInfo.setUserId(login_userID.getText().toString());
                                userInfo.setUserPassword(login_password.getText().toString());
                                userInfo.setType(1);
                                Client.getClient(InfomationAnalysis.BeantoUserInfo(userInfo));
                                Log.e("info", InfomationAnalysis.BeantoUserInfo(userInfo));
                        }
                    }
                    break;
                case R.id.login_register:
                    objectAnimator5.start();
                    break;
                case R.id.findPassword:
                    break;
                case  R.id.backLogin:
                    objectAnimator8.start();
                    break;
                case R.id.register_userName:
                    break;
                case  R.id.btn_register:
                    if(register_userName.getText().toString().equals("")||register_password.getText().toString().equals("")||register_certainPW.getText().toString().equals("")){
                        Toast.makeText(LoginActivity.this,"用户名或密码不能为空！",Toast.LENGTH_SHORT).show();
                    }else if(!register_password.getText().toString().equals(register_certainPW.getText().toString())){
                        Toast.makeText(LoginActivity.this,"两次密码输入不一致！",Toast.LENGTH_SHORT).show();
                    }else{
                        if(register_userName.getText().toString().length()>8){
                            Toast.makeText(LoginActivity.this,"用户名不能超过8位！",Toast.LENGTH_SHORT).show();
                        }else {
                            if (register_password.getText().toString().length() > 16) {
                                Toast.makeText(LoginActivity.this, "密码不能超过16位！", Toast.LENGTH_SHORT).show();
                            } else {
                                final UserInfo userInfo = new UserInfo();
                                userInfo.setUserName(register_userName.getText().toString());
                                userInfo.setUserPassword(register_password.getText().toString());
                                userInfo.setType(0);

                                Client.getClient(InfomationAnalysis.BeantoUserInfo(userInfo));

                                btn_register.setEnabled(false);
                                btn_register.setText("正在注册");
                                register_userName.setEnabled(false);
                                register_password.setEnabled(false);
                                register_certainPW.setEnabled(false);
                                backLogin.setEnabled(false);
                            }
                        }
                    }
                    break;
                    default:
                        break;

            }
        }
    };



}
