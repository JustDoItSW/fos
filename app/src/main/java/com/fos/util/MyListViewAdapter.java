package com.fos.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
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
import java.util.Map;

/**
 * Created by Apersonalive on 2018/4/24.
 */

public class MyListViewAdapter extends BaseAdapter {
    private Context context;
    private List<Flower> mList;
    private LayoutInflater mlayoutinflater;
    private int resource;
    public MyListViewAdapter(Context context, int resource,List<Flower> mList){
        this.context = context;
        this.mList=mList;
        this.resource  = resource;
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
        ViewHolder viewholder=null;
        if(view==null){
            viewholder=new ViewHolder();
            //将item的layout文件转化为view，必须是item文件在之后的view中还要找到对应的控件
            view=mlayoutinflater.inflate(resource,viewGroup,false);
            viewholder.imageView=(ImageView)view.findViewById(R.id.image_flower);
            viewholder.flowerName= (TextView)view.findViewById(R.id.text_flowerName);
            viewholder.flowerOtherName=(TextView)view.findViewById(R.id.text_flowerOtherName);

            //view.setTag(viewholder);
        }
        else {
          //  viewholder=(ViewHolder)view.getTag();
            viewholder=new ViewHolder();
            //将item的layout文件转化为view，必须是item文件在之后的view中还要找到对应的控件
            view=mlayoutinflater.inflate(resource,viewGroup,false);
            viewholder.imageView=(ImageView)view.findViewById(R.id.image_flower);
            viewholder.flowerName= (TextView)view.findViewById(R.id.text_flowerName);
            viewholder.flowerOtherName=(TextView)view.findViewById(R.id.text_flowerOtherName);
        }

        Flower flower=mList.get(i);
        //调用方法传递所需信息
       // LoadImageUtil.onLoadImage(viewholder.imageView, mList.get(i).getFlowerImage());
        Glide.with(context)
                .load(mList.get(i).getFlowerImage())
                .transform(new BitmapSetting(context))
                .priority(Priority.HIGH)
                .into(viewholder.imageView);
        viewholder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Log.e("info", "当前图片地址："+mList.get(i).getFlowerImage());
        viewholder.imageView.setTag(mList.get(i).getFlowerImage());
        viewholder.flowerName.setText(flower.getFlowerName());
        viewholder.flowerOtherName.setText(flower.getFlowerName());

        return view;
    }
    class ViewHolder{
        public TextView flowerName;
        public TextView flowerOtherName;
        public ImageView imageView;
    }
}
