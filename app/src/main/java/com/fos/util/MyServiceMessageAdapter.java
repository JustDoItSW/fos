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
import com.fos.entity.Flower;

import java.util.List;

/**
 * Created by Apersonalive on 2018/6/12.
 */

public class MyServiceMessageAdapter extends BaseAdapter {
    private Context context;
    private List<Flower> mList;
    private LayoutInflater mlayoutinflater;
    public MyServiceMessageAdapter(Context context,List<Flower> mList){
        this.context = context;
        this.mList=mList;
        mlayoutinflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {

        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if(view==null){
            viewHolder=new ViewHolder();
            //将item的layout文件转化为view，必须是item文件在之后的view中还要找到对应的控件
            view=mlayoutinflater.inflate(R.layout.layout_servicemessagelist,viewGroup,false);
            viewHolder.flowerName= (TextView)view.findViewById(R.id.message_serviceList);
            view.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder)view.getTag();

        }
        Flower flower=mList.get(i);
        viewHolder.flowerName.setText(flower.getFlowerName());
        return view;
    }
    class ViewHolder{
        public TextView flowerName;
    }
}
