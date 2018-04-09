package com.fos.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.fos.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Apersonalive（丁起柠） on 2018/3/28 23 43.
 * Project_name TianShow
 * Package_name dqn.demo.com.tianshow.MyFragment
 * Email 745267209@QQ.com
 */
public class FlowerFragment extends Fragment {
    private View view;
    private ListView listView;
    private List<Map<String,Object>> data;//数据源
    private Map<String,Object> item;//数据项
    private static FlowerFragment flowerFragment;
    public static FlowerFragment newInstance(){
        if(flowerFragment == null )
            flowerFragment = new FlowerFragment();
        return flowerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_flower,null,false);
        init();
        return view;
    }

    private void init(){

    }
    private void  initListView(){
        listView = (ListView)view.findViewById(R.id.list_flowerData);
        data = new ArrayList<Map<String,Object>>();//存放数据
        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), data, R.layout.layout_flowerlist
                , new String[]{"flowerImage","floerName","flowerOtherName"}, new int[]{R.id.image_flower,R.id.text_flowerName,R.id.text_flowerOtherName});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
