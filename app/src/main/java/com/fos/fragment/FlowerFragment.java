package com.fos.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fos.R;
import com.fos.activity.FlowerInfo;
import com.fos.activity.LoginActivity;
import com.fos.activity.MainActivity;
import com.fos.activity.SelectFlower;
import com.fos.dao.FlowerDao;
import com.fos.entity.Flower;
import com.fos.service.netty.Client;
import com.fos.util.InfomationAnalysis;
import com.fos.util.LoadImageUtil;
import com.fos.util.MyListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Apersonalive（丁起柠） on 2018/3/28 23 43.
 * Project_name TianShow
 * Package_name dqn.demo.com.tianshow.MyFragment
 * Email 745267209@QQ.com
 */
public class FlowerFragment extends Fragment {
    private View view;
    private ListView listView;
    private LinearLayout layout_notFind;
    private TextView text_notFind;

    private EditText edit_search;
    private FlowerDao flowerDao;
    private ImageView delSearch;
    private Flower[] flowers;
    private List<Flower> data;//数据源
    private Map<String,Object> item;//数据项;
    private MyListViewAdapter myListViewAdapter;
    public static Handler handler;
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
        initData();
        initListView();
        return view;
    }

    private void init(){

        flowerDao  = FlowerDao.getInstance();
       // flowerDao.delAll();
        listView = (ListView)view.findViewById(R.id.list_flowerData);
        layout_notFind  =(LinearLayout)view.findViewById(R.id.layout_notFind);
        text_notFind = (TextView)view.findViewById(R.id.text_notFind);
        delSearch =(ImageView)view.findViewById(R.id.delSearch);
        edit_search = (EditText)view.findViewById(R.id.edit_search);
        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String flowerName = edit_search.getText().toString();
                if (!flowerName.equals("")) {
//                    if (LoginActivity.Client_phone != null) {
//                        if (actionId==EditorInfo.IME_ACTION_SEARCH) {
//                            LoginActivity.Client_phone.clientSendMessage("search" + flowerName);
//                        }
//                    }
                    if (actionId== EditorInfo.IME_ACTION_SEARCH) {
                        Client.getClient("search" + flowerName);
                    }
                }
                return false;
            }
        });
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    delSearch.setVisibility(View.GONE);
                    updateData();
                    myListViewAdapter.notifyDataSetChanged();
                }
                else{
                    delSearch.setVisibility(View.VISIBLE);
//                    if (LoginActivity.Client_phone != null) {
//                        LoginActivity.Client_phone.clientSendMessage("search" + edit_search.getText());
//                    }else{
//                        searchFlower(edit_search.getText().toString());
//                    }
                    if(Client.isExist()){
                        Client.getClient("search" + edit_search.getText().toString());
                    }else{
                        searchFlower(edit_search.getText().toString());
                    }

                }
            }
        });
        delSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_search.setText("");
                updateData();
                myListViewAdapter.notifyDataSetChanged();
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String str = bundle.getString("info");
                if(msg.what == 0x003){
                    searchFlower(edit_search.getText().toString());
                    Log.e("info","植物不存在");
                }else {
                    Flower[] flower = InfomationAnalysis.jsonToFlower(str);
                    flowerDao.insertFlower(flower);
                    updateData();
                    searchFlower(edit_search.getText().toString());
                }
            }
        };
    }

    private void  initData(){
        data = new ArrayList<Flower>();//存放数据
        Flower[] flowers = flowerDao.getAllFlower();
        if(flowers!=null) {
            for (int i = 0; i < flowers.length; i++) {
                data.add(flowers[i]);
            }
        }
    }
    private void initListView(){
        myListViewAdapter  =  new MyListViewAdapter(getActivity(),R.layout.layout_flowerlist,data);
        listView.setAdapter(myListViewAdapter);
        listView.setOnItemClickListener(onItemClickListener);
    }
    /**
     * 更新数据源
     */
    private void  updateData(){
        data.clear();
        Flower[] flowers = flowerDao.getAllFlower();
        listView.setVisibility(View.VISIBLE);
        layout_notFind.setVisibility(View.GONE);
        if(flowers!=null) {
            for (int i = 0; i < flowers.length; i++) {
                data.add(flowers[i]);
            }
        }
    }

    /**
     * 搜索植物
     * @param str
     */
    private void searchFlower(String str){
        data.clear();
        Flower[] flowers  = flowerDao.searchFlower(str);
        if(flowers!=null){
            listView.setVisibility(View.VISIBLE);
            layout_notFind.setVisibility(View.GONE);
            for(int i= 0;i < flowers.length;i++){
                data.add(flowers[i]);
            }
        }
        else{
            listView.setVisibility(View.GONE);
            layout_notFind.setVisibility(View.VISIBLE);
        }
        myListViewAdapter.notifyDataSetChanged();
    }
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String str = data.get(position).getFlowerName();
            String str2 = data.get(position).getFlowerImage();
            String str3 = data.get(position).getFlowerInfo();

            Log.e("info","选中的花名为:"+position+" "+str+""+str2);
            Bundle  bundle = new Bundle();
            bundle.putString("flowerName",str);
            bundle.putString("flowerImage",str2);
            bundle.putString("flowerInfo",str3);
            bundle.putBoolean("isSelect",false);
            Intent intent = new Intent(getContext(),FlowerInfo.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };


}
