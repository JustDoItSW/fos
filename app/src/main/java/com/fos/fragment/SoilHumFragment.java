package com.fos.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fos.R;
import com.fos.myView.MyLineChart;

import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Apersonalive on 2018/4/14.
 */

public class SoilHumFragment extends Fragment {
    private View view;
    public static MyLineChart myLineChart;
    public LineChartView lineChartView;
    private TextView info;
    private static SoilHumFragment soilHumFragment;
    public static SoilHumFragment newInstance(){
        if(soilHumFragment == null )
            soilHumFragment = new SoilHumFragment();
        return soilHumFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_soilhum,null,false);
        init();
        return view;
    }

    public void init(){

        lineChartView = (LineChartView)view.findViewById(R.id.lineChart_soilHum);
        myLineChart =  new MyLineChart(100,0,0,300,lineChartView,"%");

    }
}
