package com.fos.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fos.R;
import com.fos.activity.FlowerInfo;
import com.fos.entity.Flower;
import com.fos.entity.ServiceMessage;
import com.fos.entity.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Apersonalive on 2018/6/11.
 */

public class MyRecordAdapter extends BaseAdapter {
    private  static final int TYPE_USER = 0;
    private  static final int TYPE_SERVICE = 1;
    private static final int TYPE_FLOWER = 2;
    private Context context;
    private List<Object> data;
    private LayoutInflater layoutInflater;
    public MyRecordAdapter(Context context,List<Object> as){
        this.context = context;
        data = as;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        int result = 0;
        if(data.get(position) instanceof UserMessage){
            result = TYPE_USER;
        }else if (data.get(position) instanceof ServiceMessage){
            result = TYPE_SERVICE;
        }else if(data.get(position) instanceof Flower[])
            result = TYPE_FLOWER;
        return result;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);
        if(convertView == null){
            viewHolder = new ViewHolder();
            switch (type){
                case TYPE_USER:
                    convertView = LayoutInflater.from(context).inflate(R.layout.layout_usermessage,null,false);
                    viewHolder.content = (TextView)convertView.findViewById(R.id.message_user);
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_SERVICE:
                    convertView = LayoutInflater.from(context).inflate(R.layout.layout_servicemessage,null,false);
                    viewHolder.content = (TextView)convertView.findViewById(R.id.message_service);
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_FLOWER:
                    convertView = LayoutInflater.from(context).inflate(R.layout.layout_servicemessage,null,false);
                    viewHolder.listView = (ListView) convertView.findViewById(R.id.list_serviceMessage);
                    convertView.setTag(viewHolder);
                    break;
            }
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Object o = data.get(position);
        switch (type){
            case TYPE_USER:
                UserMessage userMessage = (UserMessage)o;
                viewHolder.content.setText(userMessage.getContent());
                break;
            case TYPE_SERVICE:
                ServiceMessage serviceMessage = (ServiceMessage)o;
                viewHolder.content.setText(serviceMessage.getContent());
                break;
            case TYPE_FLOWER:
                Flower[] flowers = (Flower[]) o;
                final List<Flower> list =  new ArrayList<>();
                for(int i=0;i<flowers.length;i++)
                    list.add(flowers[i]);
                MyServiceMessageAdapter myServiceMessageAdapter = new MyServiceMessageAdapter(context,list);
                viewHolder.listView.setAdapter(myServiceMessageAdapter);
                viewHolder.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(context,FlowerInfo.class);
                        intent.putExtra("Flower",list.get(position));
                        intent.putExtra("isSelect",false);
                        context.startActivity(intent);
                    }
                });
                break;
        }
        return convertView;
    }

    private static class ViewHolder{
        TextView content;
        ListView listView;
    }



}
