package com.fos.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fos.R;
import com.fos.entity.Flower;
import com.fos.service.ClientSocket;
import com.fos.service.Client_phone;
import com.fos.service.MainService;
import com.fos.util.LogUtil;
import com.fos.util.MyFragmentPagerAdapter;
import com.fos.util.MyViewPager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    public static SharedPreferences.Editor editor;
    public static Flower flower;
    //Preferece机制操作的文件名
    public static final String PREFERENCE_NAME = "SaveContent";
    //Preferece机制的操作模式
    public static int MODE = MODE_PRIVATE;
    public static LinearLayout left_linearLayout,menu_tab;
    private SharedPreferences  sharedPreferences;
    private MyViewPager myViewPager;
    private ImageView left_menu,text_control,text_data,text_flower,contextMenu;
    private DrawerLayout dl;
    private FragmentManager fragmentManager;
    private RelativeLayout main_relativeLayout;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private ViewPager.OnPageChangeListener onPageChangeListener ;
    private Button btn_selectFlower;
    private Intent intent;
    private MainService infomationService;
    private ServiceConnection serviceConnection;
    public static Handler  handler;
    public static Client_phone Client_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.i("MING","ThreradID="+Thread.currentThread().getName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        if(Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //设置让应用主题内容占据状态栏和导航栏
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏和导航栏颜色为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }


        sharedPreferences = getSharedPreferences(PREFERENCE_NAME,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        flower = new Flower();
        flower.setFlowerName(sharedPreferences.getString("flowerName","flowerName"));

        intent = new Intent(MainActivity.this, MainService.class);
        dl = (DrawerLayout)findViewById(R.id.dl);
        main_relativeLayout =  (RelativeLayout)findViewById(R.id.main_relativeLayout);
        left_linearLayout  =(LinearLayout)findViewById(R.id.left_relativeLayout);
        menu_tab=(LinearLayout)findViewById(R.id.menu_tab);
        left_menu =  (ImageView)findViewById(R.id.left_menu);
        text_control =  (ImageView)findViewById(R.id.text_control);
        text_data =  (ImageView)findViewById(R.id.text_data);
        text_flower =  (ImageView)findViewById(R.id.text_flower);
        fragmentManager = getSupportFragmentManager();
        myViewPager = (MyViewPager)findViewById(R.id.vp);
        btn_selectFlower  = (Button)findViewById(R.id.btn_selectFlower);
        text_control.setSelected(true);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//不弹出输入法
        setupViewPager();//初始化viewpager

        //viewpager的监听器
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        setAllSelected();
                        text_control.setSelected(true);
                        break;
                    case 1:
                        setAllSelected();
                        text_data.setSelected(true);
                        MainActivity.menu_tab.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        setAllSelected();
                        text_flower.setSelected(true);
                        MainActivity.menu_tab.setVisibility(View.VISIBLE);
                        break;

                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        };
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                infomationService = ((MainService.LocalBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                infomationService = null;
            }
        };
        View.OnClickListener onClickListener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.text_control:
                        setAllSelected();
                        text_control.setSelected(true);
                        myViewPager.setCurrentItem(0);

                        break;
                    case R.id.text_data:
                        setAllSelected();
                        text_data.setSelected(true);
                        myViewPager.setCurrentItem(1);
                        MainActivity.menu_tab.setVisibility(View.VISIBLE);
                        break;
                    case R.id.text_flower:
                        setAllSelected();
                        text_flower.setSelected(true);
                        myViewPager.setCurrentItem(2);
                        MainActivity.menu_tab.setVisibility(View.VISIBLE);
                        break;
                    case R.id.left_menu:
                        if(!dl.isDrawerOpen(left_linearLayout)){
                            dl.openDrawer(left_linearLayout);
                        }
                        break;
                    case R.id.btn_selectFlower:
                        Intent intent  = new Intent(MainActivity.this,SelectFlower.class);
                        startActivity(intent);
                }
            }
        };
        myViewPager.addOnPageChangeListener(onPageChangeListener);
        left_menu.setOnClickListener(onClickListener);
        text_control.setOnClickListener(onClickListener);
        text_data.setOnClickListener(onClickListener);
        text_flower.setOnClickListener(onClickListener);
        btn_selectFlower.setOnClickListener(onClickListener);
        dl.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
                Display display  = manager.getDefaultDisplay();
                main_relativeLayout.layout(left_linearLayout.getRight(),0,left_linearLayout.getRight()+display.getWidth(),display.getHeight());
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        startService(intent);//开启服务
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);//绑定服务

    }
    private void setAllSelected(){
        text_control.setSelected(false);
        text_data.setSelected(false);
        text_flower.setSelected(false);
    }
    /**
     * 配置viewpager
     */
    private void setupViewPager(){
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        myViewPager.setOffscreenPageLimit(3);//最大缓存三个Fragment
        if(myFragmentPagerAdapter != null &&  myViewPager != null)
            myViewPager.setAdapter(myFragmentPagerAdapter);//设置适配器
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        Log.e("info","success");
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);//关闭服务
        unbindService(serviceConnection);//解除服务绑定
    }
}
