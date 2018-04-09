package com.fos.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.fos.R;
import com.fos.service.ClientSocket;
import com.fos.service.MainService;
import com.fos.util.MyFragmentPagerAdapter;
import com.fos.util.MyViewPager;

public class MainActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar toolbar;
    private MyViewPager myViewPager;
    private MenuItem menuItem;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private BottomNavigationView navigation;//底部控件
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private ViewPager.OnPageChangeListener onPageChangeListener ;
    private Intent intent;
    private MainService infomationService;
    private ServiceConnection serviceConnection;
    public static Handler  handler;
    public static ClientSocket clientSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        //viewpager的监听器
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                if(menuItem != null){
                    menuItem.setChecked(false);
                }else{
                    navigation.getMenu().getItem(0).setChecked(false);//默认为MessagFragment
                }
                menuItem = navigation.getMenu().getItem(position);//跳转到相应的界面
                menuItem.setChecked(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        };
        //navigation的监听器
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_control:
                        myViewPager.setCurrentItem(0);
                        break;
                    case R.id.navigation_data:
                        myViewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_flower:
                        myViewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        };
        myViewPager = (MyViewPager)findViewById(R.id.vp);
        myViewPager.addOnPageChangeListener(onPageChangeListener);
        setupViewPager();
        navigation = (BottomNavigationView)findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //  toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.myToolbar);
        //    setSupportActionBar(toolbar);

        intent = new Intent(MainActivity.this, MainService.class);
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
        startService(intent);//开启服务
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);//绑定服务
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
