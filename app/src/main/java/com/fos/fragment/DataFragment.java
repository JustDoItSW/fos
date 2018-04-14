package com.fos.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fos.R;
import com.fos.activity.MainActivity;
import com.fos.entity.Infomation;
import com.fos.myView.MyLineChart;
import com.fos.util.InfomationAnalysis;
import com.fos.util.MyDataFragmentPagerAdapter;
import com.fos.util.MyFragmentPagerAdapter;
import com.fos.util.MyViewPager;

import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Apersonalive（丁起柠） on 2018/3/28 23 43.
 * Project_name TianShow
 * Package_name dqn.demo.com.tianshow.MyFragment
 * Email 745267209@QQ.com
 */
public class DataFragment extends Fragment {
    private View  view;
    private MyViewPager myViewPager;
    private MyDataFragmentPagerAdapter myFragmentPagerAdapter;
    private ViewPager.OnPageChangeListener onPageChangeListener ;
    private TextView menu_temp,menu_hum,menu_soilHum,menu_lux;
    public static Handler handler;
    private static DataFragment dataFragment;
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
        myViewPager.addOnPageChangeListener(onPageChangeListener);
        setupViewPager();

        menu_temp.setSelected(true);
        View.OnClickListener onClickListener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.menu_temp:
                        setAllSelected();
                        menu_temp.setSelected(true);
                        myViewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_hum:
                        setAllSelected();
                        menu_hum.setSelected(true);
                        myViewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_soilHum:
                        setAllSelected();
                        menu_soilHum.setSelected(true);
                        myViewPager.setCurrentItem(2);
                        break;
                    case R.id.menu_lux:
                        setAllSelected();
                        menu_lux.setSelected(true);
                        myViewPager.setCurrentItem(3);
                        break;
                }
            }
        };
        menu_temp.setOnClickListener(onClickListener);
        menu_hum.setOnClickListener(onClickListener);
        menu_soilHum.setOnClickListener(onClickListener);
        menu_lux.setOnClickListener(onClickListener);


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
        if(myFragmentPagerAdapter != null &&  myViewPager != null)
            myViewPager.setAdapter(myFragmentPagerAdapter);//设置适配器
    }
}
