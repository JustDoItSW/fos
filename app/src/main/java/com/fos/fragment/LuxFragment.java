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

public class LuxFragment extends Fragment {
    private View view;
    public static MyLineChart myLineChart;
    public LineChartView lineChartView;
    private TextView info;
    private static LuxFragment luxFragment;
    public static LuxFragment newInstance(){
        if(luxFragment == null )
            luxFragment = new LuxFragment();
        return luxFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lux,null,false);
        init();
        return view;
    }

    public void init(){


        lineChartView = (LineChartView)view.findViewById(R.id.lineChart_lux);
        myLineChart =  new MyLineChart(1000,0,0,300,lineChartView,"L");

    }
}
