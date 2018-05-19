package com.fos.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fos.R;
import com.fos.entity.Flower;

import java.util.List;
import java.util.Map;

/**
 * Created by Apersonalive on 2018/5/19.
 */

public class MyCommunityListAdapter extends BaseAdapter {



    private String allImageUri;
    private Context context;
    private int resource;
    private List<Map<String,Object>> mapList;
    private LayoutInflater mLayoutInflater;
    public MyCommunityListAdapter(Context context , int resource, List<Map<String,Object>> mapList){
        this.context = context;
        this.resource  = resource;
        this.mapList =  mapList;
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
            //将item的layout文件转化为view，必须是item文件在之后的view中还要找到对应的控件
            convertView=mLayoutInflater.inflate(resource,null);
            viewholder.userIcon=(ImageView)convertView.findViewById(R.id.image_comIcon);
            viewholder.userID= (TextView)convertView.findViewById(R.id.community_userName);
            viewholder.date=(TextView)convertView.findViewById(R.id.community_date);
            viewholder.grid_image = (GridLayout)convertView.findViewById(R.id.grid_image);
            viewholder.comContext= (TextView)convertView.findViewById(R.id.comContext);
            viewholder.count_support= (TextView)convertView.findViewById(R.id.count_support);
            viewholder.count_browse= (TextView)convertView.findViewById(R.id.count_browse);
            viewholder.count_evaluate= (TextView)convertView.findViewById(R.id.count_evaluate);
            viewholder.count_share= (TextView)convertView.findViewById(R.id.count_share);

            convertView.setTag(viewholder);
        }
        else {
            viewholder=(ViewHolder)convertView.getTag();
        }
       // viewholder.userIcon.setImageResource(R.mipmap.ic_launcher_round);
        //调用方法传递所需信息
      //  LoadImageUtil.onLoadImage(viewholder.userIcon, mapList.get(position));
    //    viewholder.userIcon.setTag(mList.get(i).getFlowerImage());
        viewholder.userID.setText(mapList.get(position).get("userName").toString());
        viewholder.date.setText(mapList.get(position).get("data").toString());
        viewholder.comContext.setText(mapList.get(position).get("comContext").toString());
        viewholder.count_support.setText(mapList.get(position).get("count_support").toString());
        viewholder.count_browse.setText(mapList.get(position).get("count_browse").toString());
        viewholder.count_evaluate.setText(mapList.get(position).get("count_evaluate").toString());
        viewholder.count_share.setText(mapList.get(position).get("count_share").toString());

        viewholder.count_share.setTag("count_share");
        viewholder.count_support.setTag("count_support");
        viewholder.count_browse.setTag("count_browse");
        viewholder.count_evaluate.setTag("count_evaluate");

        if(!mapList.get(position).get("picture").toString().equals("")) {
            allImageUri =  mapList.get(position).get("picture").toString();
            for (int i = 0;i<getImageCount(position);i++){
                ImageView imageView = new ImageView(context);
                GridLayout.Spec  rowSpec = GridLayout.spec(i/3);
                GridLayout.Spec  columnSpec = GridLayout.spec(i%3);

                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
                layoutParams.height = 0;
                layoutParams.width = 0;

                layoutParams.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_0_5);
                layoutParams.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_0_5);
                layoutParams.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_0_5);
                layoutParams.topMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_0_5);

                LoadImageUtil.onLoadImage(imageView, getImageUri(i));
                viewholder.grid_image.addView(imageView);
            }
            allImageUri =  null;
        }
        return convertView;
    }

    class ViewHolder{
        public TextView userID;
        public TextView date;
        public ImageView userIcon;
        public TextView comContext;
        public GridLayout grid_image;
        public TextView count_support;
        public TextView count_browse;
        public TextView count_evaluate;
        public TextView count_share;
    }

    public int getImageCount(int position){
        String[] arr = allImageUri.split(";");
        return arr.length;

    }

    public String getImageUri(int position){
        String[] arr = allImageUri.split(";");
        return arr[position];

    }
}
