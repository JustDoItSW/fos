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
import com.fos.entity.User;
import com.fos.entity.UserInfo;

import java.util.List;

/**
 * Created by Apersonalive on 2018/5/29.
 */

public class MyLoginListView extends BaseAdapter {

    private Context context;
    private List<User> mList;
    private LayoutInflater mLayoutinflater;
    public MyLoginListView(Context context, List<User> mList){
        this.context = context;
        this.mList=mList;
        mLayoutinflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder  = new ViewHolder();
            convertView = mLayoutinflater.inflate(R.layout.layout_userid,parent,false);
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.userIconList);
            viewHolder.userId = (TextView)convertView.findViewById(R.id.userIDList);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.userId.setText(mList.get(position).getUserId());
        Glide.with(context)
                .load(mList.get(position).getUserHeadImage())
                .priority(Priority.HIGH)
                .centerCrop()
                .into(viewHolder.imageView);
        viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return convertView;
    }

    class ViewHolder{
        public TextView userId;
        public ImageView imageView;
    }
}
