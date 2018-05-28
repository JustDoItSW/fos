package com.fos.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Apersonalive on 2018/5/21.
 */

public class MyGridViewAdapter extends BaseAdapter {
    private Context context;
    private int resource;
    private String[] arr;
    private LayoutInflater mLayoutInflater;
    public MyGridViewAdapter(Context context , int resource, String[] arr){
        this.context = context;
        this.resource  = resource;
        this.arr =  arr;
        mLayoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return arr.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView=mLayoutInflater.inflate(resource,null);
            viewHolder.imageView=(ImageView)convertView.findViewById(R.id.gridview_image);
            convertView.setTag(viewHolder);

        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        ViewGroup.LayoutParams para = viewHolder.imageView.getLayoutParams();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if(arr.length ==  1)
            para.width = dm.widthPixels-2;
        else if(arr.length ==  2)
            para.width = (dm.widthPixels-3)/2;
        else {
            para.width = (dm.widthPixels - 4) / 3;
        }
        para.height = para.width;
        viewHolder.imageView.setLayoutParams(para);

        // LoadImageUtil.onLoadListImage(viewHolder.imageView,arr[position]);
        Glide.with(context)
                .load("http://"+arr[position])
                .priority(Priority.HIGH)
                .into(viewHolder.imageView);
        viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);



        return convertView;
    }

    class ViewHolder{
        public ImageView imageView;

    }
}
