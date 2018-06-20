package com.fos.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;
import com.fos.entity.Evaluate;

import java.util.List;

/**
 * Created by Apersonalive on 2018/6/7.
 */

public class MyEvaluateAdapter extends BaseAdapter {
    private Context context;
    private int resource;
    private List<Evaluate> mapList;
    private LayoutInflater mLayoutInflater;
    public MyEvaluateAdapter(Context context , int resource, List<Evaluate> mapList){
        this.context = context;
        this.resource  = resource;
        this.mapList =  mapList;
        mLayoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Object getItem(int position) {
        return mapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder = null;
        if(convertView==null){
            viewholder=new ViewHolder();
            convertView=mLayoutInflater.inflate(resource,null);
            viewholder.userIcon=(ImageView)convertView.findViewById(R.id.list_evaluateIcon);
            viewholder.userName= (TextView)convertView.findViewById(R.id.list_evaluateUserName);
            viewholder.date=(TextView)convertView.findViewById(R.id.list_evaluateContentDate);
            viewholder.context= (TextView)convertView.findViewById(R.id.list_evaluateContent);
            convertView.setTag(viewholder);
        }
        else {
            viewholder=(ViewHolder)convertView.getTag();
        }
        Glide.with(context)
                .load(mapList.get(position).getUserInfo().getUserHeadImage().toString())
                .transform(new BitmapSetting(context))
                .priority(Priority.HIGH)
                .into(viewholder.userIcon);
        viewholder.userName.setText(mapList.get(position).getUserInfo().getUserName().toString());
        viewholder.date.setText(TimeUtils.dateBefore(mapList.get(position).getDate().toString()));
        viewholder.context.setText(mapList.get(position).getContent().toString());
        return convertView;
    }

    class ViewHolder{
        public TextView userName;
        public TextView date;
        public ImageView userIcon;
        public TextView context;

    }
}
