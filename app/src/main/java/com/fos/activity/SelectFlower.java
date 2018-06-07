package com.fos.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fos.R;
import com.fos.dao.FlowerDao;
import com.fos.entity.Flower;
import com.fos.entity.ServiceFlower;
import com.fos.service.netty.Client;
import com.fos.util.InfomationAnalysis;
import com.fos.util.MyListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectFlower extends AppCompatActivity {

    private RelativeLayout exit_selectFlower;
    private ListView listView;
    private LinearLayout layout_notFind_select;
    private TextView text_notFind_select;
    private EditText edit_search_select;
    private FlowerDao flowerDao;
    private ImageView delSearch_select;
    private List<Flower> data;//数据源
    private Map<String,Object> item;//数据项;
    private MyListViewAdapter myListViewAdapter;
    public static Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_flower);
        init();
        initData();
        initListView();
    }

    public void init(){
        exit_selectFlower = (RelativeLayout)findViewById(R.id.exit_selectFlower);
        exit_selectFlower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        data = new ArrayList<Flower>();//存放数据
        listView = (ListView)findViewById(R.id.list_flowerSelect);
        layout_notFind_select  =(LinearLayout)findViewById(R.id.layout_notFind_select);
        text_notFind_select = (TextView)findViewById(R.id.text_notFind_select);
        delSearch_select =(ImageView)findViewById(R.id.delSearch_select);
        edit_search_select = (EditText)findViewById(R.id.edit_search_select);
        edit_search_select.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String flowerName = edit_search_select.getText().toString();
                if (!flowerName.equals("")) {
                    if (actionId== EditorInfo.IME_ACTION_SEARCH) {
                        ServiceFlower serviceFlower  =  new ServiceFlower();
                        serviceFlower.setFlowerName(flowerName);
                        Client.getClient(InfomationAnalysis.BeanToFlower(serviceFlower));
                    }
                }
                return false;
            }
        });
        edit_search_select.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    delSearch_select.setVisibility(View.GONE);
                    updateData();
                    myListViewAdapter.notifyDataSetChanged();
                }
                else{
                    delSearch_select.setVisibility(View.VISIBLE);
                    if(Client.isExist()){
                        ServiceFlower serviceFlower  =  new ServiceFlower();
                        serviceFlower.setFlowerName(edit_search_select.getText().toString());
                        Client.getClient(InfomationAnalysis.BeanToFlower(serviceFlower));
                    }else{
                        searchFlower(edit_search_select.getText().toString());
                    }
                }
            }
        });
        delSearch_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_search_select.setText("");
                updateData();
                myListViewAdapter.notifyDataSetChanged();
            }
        });
        flowerDao  = FlowerDao.getInstance();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String str = bundle.getString("info");
                if(msg.what == 0x003){
                    searchFlower(edit_search_select.getText().toString());
                    Log.e("info","植物不存在");
                }else {
                    Flower[] flower = InfomationAnalysis.jsonToFlower(str);
                    flowerDao.insertFlower(flower);
                    updateData();
                    searchFlower(edit_search_select.getText().toString());
                }
            }
        };
    }

    private void  initData(){
        Flower[]  flowers = flowerDao.getAllFlower();
        if(flowers!=null) {
            for (int i = 0; i < flowers.length; i++) {
                data.add(flowers[i]);
            }
        }
    }
    private void initListView(){
        myListViewAdapter  =  new MyListViewAdapter(SelectFlower.this,R.layout.layout_flowerlist,data);
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
        layout_notFind_select.setVisibility(View.GONE);
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
            layout_notFind_select.setVisibility(View.GONE);
            for(int i= 0;i < flowers.length;i++){
                data.add(flowers[i]);
            }
        }
        else{
            listView.setVisibility(View.GONE);
            layout_notFind_select.setVisibility(View.VISIBLE);
        }
        myListViewAdapter.notifyDataSetChanged();
    }
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e("info","选中的花名为:"+data.get(position).getFlowerName()+"");
            Bundle  bundle = new Bundle();
            bundle.putString("flowerName",data.get(position).getFlowerName());
            bundle.putString("flowerImage",data.get(position).getFlowerImage());
            bundle.putString("flowerInfo",data.get(position).getFlowerInfo());
            bundle.putString("light",data.get(position).getFlowerLux());
            bundle.putString("hum",data.get(position).getFlowerSoilHum());
            bundle.putString("temp",data.get(position).getFlowerTemp());
            bundle.putBoolean("isSelect",true);
            Intent intent = new Intent(SelectFlower.this,FlowerInfo.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

}
