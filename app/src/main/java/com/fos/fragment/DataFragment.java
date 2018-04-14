package com.fos.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fos.R;
import com.fos.entity.Infomation;
import com.fos.myView.MyLineChart;
import com.fos.util.InfomationAnalysis;

import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Apersonalive（丁起柠） on 2018/3/28 23 43.
 * Project_name TianShow
 * Package_name dqn.demo.com.tianshow.MyFragment
 * Email 745267209@QQ.com
 */
public class DataFragment extends Fragment {
    private View  view;
    public static MyLineChart myLineChart_temp,myLineChart_lux,myLineChart_soilHum,myLineChart_hum;
    public LineChartView lineChartView_temp,lineChartView_lux,lineChartView_soilHum,lineChartView_hum;
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
        lineChartView_temp = (LineChartView)view.findViewById(R.id.lineChart_temp);
        lineChartView_lux = (LineChartView)view.findViewById(R.id.lineChart_lux);
        lineChartView_soilHum = (LineChartView)view.findViewById(R.id.lineChart_soilHum);
        lineChartView_hum = (LineChartView)view.findViewById(R.id.lineChart_hum);

        myLineChart_temp =  new MyLineChart(50,0,0,300,lineChartView_temp,"°");
        myLineChart_lux =  new MyLineChart(1000,0,0,300,lineChartView_lux,"l");
        myLineChart_soilHum =  new MyLineChart(100,0,0,300,lineChartView_soilHum,"%");
        myLineChart_hum =  new MyLineChart(100,0,0,300,lineChartView_hum,"%");


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String str = bundle.getString("info");
                Infomation infomation = InfomationAnalysis.jsonToBean(str);
            }
        };
    }
}
