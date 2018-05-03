package com.fos.fragment;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fos.R;
import com.fos.util.LoadImageUtil;
import com.fos.util.MyDataFragmentPagerAdapter;
import com.fos.util.MyFragmentPagerAdapter;
import com.fos.util.MyViewPager;
import com.fos.util.SpringIndicator.SpringIndicator;

import java.util.List;


/**
 * Created by Apersonalive（丁起柠） on 2018/3/28 23 43.
 * Project_name TianShow
 * Package_name dqn.demo.com.tianshow.MyFragment
 * Email 745267209@QQ.com
 */
public class DataFragment extends Fragment {
    private View  view;
    private int current_index ;
    private MyViewPager myViewPager;
    private MyDataFragmentPagerAdapter myFragmentPagerAdapter;
    private ViewPager.OnPageChangeListener onPageChangeListener ;
    private TextView menu_temp,menu_hum,menu_soilHum,menu_lux;
    public static Handler handler;
    private static DataFragment dataFragment;

    SpringIndicator springIndicator;

    public static DataFragment newInstance(){
        if(dataFragment == null )
            dataFragment = new DataFragment();
        return dataFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data,null,false);
        init();
        return view;
    }
    private void init(){
        menu_temp = (TextView)view.findViewById(R.id.menu_temp);
        menu_hum = (TextView)view.findViewById(R.id.menu_hum);
        menu_soilHum = (TextView)view.findViewById(R.id.menu_soilHum);
        menu_lux = (TextView)view.findViewById(R.id.menu_lux);


        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        setAllSelected();
                        menu_temp.setSelected(true);

                        break;
                    case 1:
                        setAllSelected();
                        menu_hum.setSelected(true);

                        break;
                    case 2:
                        setAllSelected();
                        menu_soilHum.setSelected(true);

                        break;
                    case 3:
                        setAllSelected();
                        menu_lux.setSelected(true);

                        break;

                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        };
        myViewPager = (MyViewPager)view.findViewById(R.id.vp_data);

        springIndicator=view.findViewById(R.id.indicator);

        myViewPager.addOnPageChangeListener(onPageChangeListener);
        setupViewPager();


        View.OnClickListener onClickListener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.menu_temp:
                        setAllSelected();
                        setCurrentTextView(menu_temp);
                        myViewPager.setCurrentItem(0);

                        break;
                    case R.id.menu_hum:
                        setAllSelected();
                        setCurrentTextView(menu_hum);
                        myViewPager.setCurrentItem(1);

                        break;
                    case R.id.menu_soilHum:
                        setAllSelected();
                        setCurrentTextView(menu_soilHum);
                        myViewPager.setCurrentItem(2);

                        break;
                    case R.id.menu_lux:
                        setAllSelected();
                        setCurrentTextView(menu_lux);
                        myViewPager.setCurrentItem(3);

                        break;
                }
            }
        };
        menu_temp.setOnClickListener(onClickListener);
        menu_hum.setOnClickListener(onClickListener);
        menu_soilHum.setOnClickListener(onClickListener);
        menu_lux.setOnClickListener(onClickListener);
        menu_temp.setSelected(true);


    }

    private void setCurrentTextView(TextView  textView){
//        textView.setSelected(true);
//        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textView.getPaint().setAntiAlias(true);
    }
    private void setAllSelected(){
        menu_temp.setSelected(false);
        menu_hum.setSelected(false);
        menu_soilHum.setSelected(false);
        menu_lux.setSelected(false);
    }

    private void setupViewPager(){
        myFragmentPagerAdapter = new MyDataFragmentPagerAdapter(getActivity().getSupportFragmentManager());
        myViewPager.setOffscreenPageLimit(4);//最大缓存三个Fragment

        if(myFragmentPagerAdapter != null &&  myViewPager != null) {
            myViewPager.setAdapter(myFragmentPagerAdapter);//设置适配器
            myViewPager.fixScrollSpeed();
            springIndicator.setViewPager(myViewPager);
        }

    }

}
