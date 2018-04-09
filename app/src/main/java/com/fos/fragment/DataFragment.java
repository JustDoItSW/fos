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
    public static MyLineChart myLineChart_TAH,myLineChart_light;
    public static LineChartView lineChartView_TAH,lineChartView_light;
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
        lineChartView_TAH = (LineChartView)view.findViewById(R.id.lineChart_TAH);
        lineChartView_light = (LineChartView)view.findViewById(R.id.lineChart_light);

        myLineChart_TAH =  new MyLineChart();
        myLineChart_TAH.initLineChartView(lineChartView_TAH,"°");

        myLineChart_light = new MyLineChart();
        myLineChart_light.initLineChartView(lineChartView_light,"%");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String str = bundle.getString("info");
                Infomation infomation = InfomationAnalysis.jsonToBean(str);
                myLineChart_TAH.repaintView(Integer.parseInt(infomation.getTemperature()), Color.rgb(199,232,245));
                myLineChart_light.repaintView(Integer.parseInt(infomation.getTemperature()),Color.rgb(199,232,245));
            }
        };
    }
}
