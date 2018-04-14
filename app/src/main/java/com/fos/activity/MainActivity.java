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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fos.R;
import com.fos.service.ClientSocket;
import com.fos.service.MainService;
import com.fos.util.MyFragmentPagerAdapter;
import com.fos.util.MyViewPager;

public class MainActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar toolbar;
    private MyViewPager myViewPager;
    private MenuItem menuItem;
    private LinearLayout menu_control,menu_data,menu_flower;
    private TextView text_control,text_data,text_flower;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
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
        //navigation的监听器
        myViewPager = (MyViewPager)findViewById(R.id.vp);
        myViewPager.addOnPageChangeListener(onPageChangeListener);
        setupViewPager();
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
        menu_control = (LinearLayout)findViewById(R.id.menu_control);
        menu_data = (LinearLayout)findViewById(R.id.menu_data);
        menu_flower = (LinearLayout)findViewById(R.id.menu_flower);
        text_control =  (TextView)findViewById(R.id.text_control);
        text_data =  (TextView)findViewById(R.id.text_data);
        text_flower =  (TextView)findViewById(R.id.text_flower);

        text_control.setSelected(true);
        View.OnClickListener onClickListener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.menu_control:
                        setAllSelected();
                        text_control.setSelected(true);
                        myViewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_data:
                        setAllSelected();
                        text_data.setSelected(true);
                        myViewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_flower:
                        setAllSelected();
                        text_flower.setSelected(true);
                        myViewPager.setCurrentItem(2);
                        break;
                }
            }
        };
        menu_control.setOnClickListener(onClickListener);
        menu_data.setOnClickListener(onClickListener);
        menu_flower.setOnClickListener(onClickListener);
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
