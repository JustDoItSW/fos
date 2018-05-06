package com.fos.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fos.R;
import com.fos.util.ActivityCollector;
import com.fos.util.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: app启动页面制作,判断是否是第一次进入，是的话进入引导页，否则，进入MainActivity
 * @date 2018/5/2 21:20
 */

public class StartActivity extends AppCompatActivity {
    private Boolean isFirst=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ActivityCollector.addActivity(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences=getSharedPreferences(MainActivity.PREFERENCE_NAME,MODE_PRIVATE);
        isFirst=preferences.getBoolean("isFirst",true);

        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                if(isFirst) {
                    Intent intent = new Intent(StartActivity.this, GuideLauncherActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(StartActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timer.schedule(timerTask,1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        ActivityCollector.removeActivity(this);
    }
}
