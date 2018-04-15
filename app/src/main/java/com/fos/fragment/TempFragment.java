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

public class TempFragment extends Fragment {
    private View  view;
    public static MyLineChart myLineChart;
    public LineChartView lineChartView;
    private TextView info;
    private static TempFragment tempFragment;
    public static TempFragment newInstance(){
        if(tempFragment == null )
            tempFragment = new TempFragment();
        return tempFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_temp,null,false);
        init();
        return view;
    }

    public void init(){

        lineChartView = (LineChartView)view.findViewById(R.id.lineChart_temp);
        myLineChart =  new MyLineChart(50,0,0,300,lineChartView,"°");

    }
}
