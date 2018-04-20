package com.fos.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fos.R;
import com.fos.service.ClientSocket;
import com.fos.service.Client_phone;
import com.fos.service.MainService;
import com.fos.util.LogUtil;
import com.fos.util.MyFragmentPagerAdapter;
import com.fos.util.MyViewPager;

public class MainActivity extends AppCompatActivity {


    private MyViewPager myViewPager;
    private ImageView left_menu,text_control,text_data,text_flower;
    private DrawerLayout dl;
    private RelativeLayout left_relativeLayout,main_relativeLayout;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private ViewPager.OnPageChangeListener onPageChangeListener ;
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
        //viewpager的监听器

        intent = new Intent(MainActivity.this, MainService.class);
        dl = (DrawerLayout)findViewById(R.id.dl);
        main_relativeLayout =  (RelativeLayout)findViewById(R.id.main_relativeLayout);
        left_relativeLayout  =(RelativeLayout)findViewById(R.id.left_relativeLayout);
        left_menu =  (ImageView)findViewById(R.id.left_menu);
        text_control =  (ImageView)findViewById(R.id.text_control);
        text_data =  (ImageView)findViewById(R.id.text_data);
        text_flower =  (ImageView)findViewById(R.id.text_flower);
        myViewPager = (MyViewPager)findViewById(R.id.vp);
        text_control.setSelected(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//不弹出输入法
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
                        break;
                    case 2:
                        setAllSelected();
                        text_flower.setSelected(true);
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
                        break;
                    case R.id.text_flower:
                        setAllSelected();
                        text_flower.setSelected(true);
                        myViewPager.setCurrentItem(2);
                        break;
                    case R.id.left_menu:
                        if(!dl.isDrawerOpen(left_relativeLayout)){
                            dl.openDrawer(left_relativeLayout);
                        }
                        break;
                }
            }
        };
        myViewPager.addOnPageChangeListener(onPageChangeListener);
        left_menu.setOnClickListener(onClickListener);
        text_control.setOnClickListener(onClickListener);
        text_data.setOnClickListener(onClickListener);
        text_flower.setOnClickListener(onClickListener);
        dl.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
                Display display  = manager.getDefaultDisplay();
                main_relativeLayout.layout(left_relativeLayout.getRight(),0,left_relativeLayout.getRight()+display.getWidth(),display.getHeight());
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
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);//关闭服务
        unbindService(serviceConnection);//解除服务绑定
    }
}
