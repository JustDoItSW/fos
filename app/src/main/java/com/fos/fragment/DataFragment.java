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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;
import com.fos.activity.MainActivity;
import com.fos.entity.Infomation;
import com.fos.util.BitmapSetting;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LoadImageUtil;
import com.fos.util.MyDataFragmentPagerAdapter;
import com.fos.util.MyFragmentPagerAdapter;
import com.fos.util.MyViewPager;
import com.fos.util.SpringIndicator.SpringIndicator;
import com.fos.util.TimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private ImageView image_flowerInfo;
    private TextView menu_temp,menu_hum,menu_soilHum,menu_lux,level_light,level_water,level_temp,level_nut,data_flowerName,data_flowerName_g,data_date;
    private View light_lv1,light_lv2,light_lv3,water_lv1,water_lv2,water_lv3,temp_lv1,temp_lv2,temp_lv3,nut_lv1,nut_lv2,nut_lv3;
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
        initUserFlower();
        setupViewPager();
        return view;
    }
    private void init(){
        menu_temp = (TextView)view.findViewById(R.id.menu_temp);
        menu_hum = (TextView)view.findViewById(R.id.menu_hum);
        menu_soilHum = (TextView)view.findViewById(R.id.menu_soilHum);
        menu_lux = (TextView)view.findViewById(R.id.menu_lux);

        image_flowerInfo = (ImageView)view.findViewById(R.id.image_flowerInfo);

        data_flowerName = (TextView)view.findViewById(R.id.data_flowerName);
        data_flowerName_g = (TextView)view.findViewById(R.id.data_flowerName_g);
        data_date = (TextView)view.findViewById(R.id.data_date);

        level_light = (TextView)view.findViewById(R.id.level_light);
        level_water = (TextView)view.findViewById(R.id.level_water);
        level_temp = (TextView)view.findViewById(R.id.level_temp);
        level_nut = (TextView)view.findViewById(R.id.level_nut);

        light_lv1 = (View)view.findViewById(R.id.light_lv1);
        light_lv2 = (View)view.findViewById(R.id.light_lv2);
        light_lv3 = (View)view.findViewById(R.id.light_lv3);

        water_lv1 = (View)view.findViewById(R.id.water_lv1);
        water_lv2 = (View)view.findViewById(R.id.water_lv2);
        water_lv3 = (View)view.findViewById(R.id.water_lv3);

        temp_lv1 = (View)view.findViewById(R.id.temp_lv1);
        temp_lv2 = (View)view.findViewById(R.id.temp_lv2);
        temp_lv3 = (View)view.findViewById(R.id.temp_lv3);

        nut_lv1 = (View)view.findViewById(R.id.nut_lv1);
        nut_lv2 = (View)view.findViewById(R.id.nut_lv2);
        nut_lv3 = (View)view.findViewById(R.id.nut_lv3);

        myViewPager = (MyViewPager)view.findViewById(R.id.vp_data);

        springIndicator=view.findViewById(R.id.indicator);



        menu_temp.setOnClickListener(onClickListener);
        menu_hum.setOnClickListener(onClickListener);
        menu_soilHum.setOnClickListener(onClickListener);
        menu_lux.setOnClickListener(onClickListener);
        menu_temp.setSelected(true);

        setNutLevel();

        handler =  new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x004)
                    initUserFlower();
                else if(msg.what == 0x006){
                    setNutLevel();
                }
                else {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData();
                    String str = bundle.getString("info");
                    Log.e("Data收到：", str);
                    Infomation infomation = InfomationAnalysis.jsonToData(str);
                    try {
                        HumFragment.myLineChart.repaintView(Integer.parseInt(infomation.getHumidity()), infomation.getDate().toString(), Color.rgb(199, 232, 245));
                        LuxFragment.myLineChart.repaintView(1000-Integer.parseInt(infomation.getLux()), infomation.getDate().toString(), Color.rgb(246, 235, 188));
                        SoilHumFragment.myLineChart.repaintView(Math.round(Float.parseFloat(infomation.getSoilHumidity())), infomation.getDate().toString(), Color.rgb(199, 232, 245));
                        TempFragment.myLineChart.repaintView(Integer.parseInt(infomation.getTemperature()), infomation.getDate().toString(), Color.rgb(255, 150, 150));
                        setLevel(infomation);
                    }catch(Exception e){}
                }
            }
        };
    }
    private  void initUserFlower(){
        Glide.with(getContext())
                .load(MainActivity.flower.getFlowerImage())
                .transform(new BitmapSetting(getContext()))
                .priority(Priority.HIGH)
                .into(image_flowerInfo);
        image_flowerInfo.setScaleType(ImageView.ScaleType.CENTER_CROP);
     //   LoadImageUtil.onLoadImage(image_flowerInfo,MainActivity.flower.getFlowerImage());
        data_flowerName.setText(MainActivity.flower.getFlowerName());
        data_flowerName_g.setText(MainActivity.flower.getFlowerName());

        data_date.setText("已监测"+ TimeUtils.dateDiff(MainActivity.browseDate));
    }
    private void initLevel(){
        light_lv1.setBackgroundResource(R.drawable.round_view_gray);
        light_lv2.setBackgroundResource(R.drawable.round_view_gray);
        light_lv3.setBackgroundResource(R.drawable.round_view_gray);
        water_lv1.setBackgroundResource(R.drawable.round_view_gray);
        water_lv2.setBackgroundResource(R.drawable.round_view_gray);
        water_lv3.setBackgroundResource(R.drawable.round_view_gray);
        temp_lv1.setBackgroundResource(R.drawable.round_view_gray);
        temp_lv2.setBackgroundResource(R.drawable.round_view_gray);
        temp_lv3.setBackgroundResource(R.drawable.round_view_gray);

    }
    private void  setLevel(Infomation infomation){
        initLevel();
        int light  = 1000-Integer.parseInt(infomation.getLux());
        int hum = Math.round(Float.parseFloat(infomation.getSoilHumidity()));
        int temp = Integer.parseInt(infomation.getTemperature());

        int trueLight = Integer.parseInt(MainActivity.flower.getFlowerLux());
        int trueHum = Math.round(Float.parseFloat(infomation.getSoilHumidity()));
        int trueTemp = Integer.parseInt(MainActivity.flower.getFlowerTemp());
        if(light>(trueLight+200)){
            level_light.setText("过强");
            light_lv3.setBackgroundResource(R.drawable.round_view_red);
        }else if(light<(trueLight-200)){
            level_light.setText("较弱");
            light_lv1.setBackgroundResource(R.drawable.round_view_red);
        }else{
            level_light.setText("合适");
            light_lv2.setBackgroundResource(R.drawable.round_view_green);
        }

        if(hum>(trueHum+15)){
            level_water.setText("过高");
            water_lv3.setBackgroundResource(R.drawable.round_view_red);
        }else if(hum<(trueHum-15)){
            level_water.setText("较低");
            water_lv1.setBackgroundResource(R.drawable.round_view_red);
        }else{
            level_water.setText("合适");
            water_lv2.setBackgroundResource(R.drawable.round_view_green);
        }

        if(temp>(trueTemp+5)){
            level_temp.setText("过高");
            temp_lv3.setBackgroundResource(R.drawable.round_view_red);
        }else if(temp<(trueTemp-10)){
            level_temp.setText("较低");
            temp_lv1.setBackgroundResource(R.drawable.round_view_red);
        }else{
            level_temp.setText("合适");
            temp_lv2.setBackgroundResource(R.drawable.round_view_green);
        }


    }

    private void setNutLevel(){
        try {
            nut_lv1.setBackgroundResource(R.drawable.round_view_gray);
            nut_lv2.setBackgroundResource(R.drawable.round_view_gray);
            nut_lv3.setBackgroundResource(R.drawable.round_view_gray);
            long nurData = TimeUtils.dateDiffNm(MainActivity.nurData);
            Log.e("info",nurData+"");
            long aDayNm = 24 * 60 * 60 * 1000;
            if (nurData != 0) {
                if (nurData > 5 * aDayNm) {
                    level_nut.setText("较低");
                    nut_lv1.setBackgroundResource(R.drawable.round_view_red);
                } else if (nurData < 2 * aDayNm) {
                    level_nut.setText("过高");
                    nut_lv3.setBackgroundResource(R.drawable.round_view_red);
                } else {
                    level_nut.setText("合适");
                    nut_lv2.setBackgroundResource(R.drawable.round_view_green);
                }
            }

        }catch (Exception e){e.printStackTrace();}
        finally {
            MainActivity.nurData = TimeUtils.getCurrentTime();
            MainActivity.editor.putString("nurData", MainActivity.nurData);
            MainActivity.editor.commit();
        }
    }

    private void setCurrentTextView(TextView  textView){
        textView.setSelected(true);
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
        myViewPager.addOnPageChangeListener(onPageChangeListener);

    }
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
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

                    default:
                        break;

            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {}
    };
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

}
