package com.fos.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
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
            //将item的layout文件转化为view，必须是item文件在之后的view中还要找到对应的控件
            convertView=mLayoutInflater.inflate(resource,null);
            viewholder.userIcon=(ImageView)convertView.findViewById(R.id.image_comIcon);
            viewholder.userID= (TextView)convertView.findViewById(R.id.community_userName);
            viewholder.date=(TextView)convertView.findViewById(R.id.community_date);
            viewholder.grid_image = (android.support.v7.widget.GridLayout)convertView.findViewById(R.id.grid_image);
            viewholder.comContext= (TextView)convertView.findViewById(R.id.comContext);
            viewholder.count_support= (TextView)convertView.findViewById(R.id.count_support);
            viewholder.count_browse= (TextView)convertView.findViewById(R.id.count_browse);
            viewholder.count_evaluate= (TextView)convertView.findViewById(R.id.count_evaluate);
            viewholder.count_share= (TextView)convertView.findViewById(R.id.count_share);
            viewholder.gridView =(GridView)convertView.findViewById(R.id.gridView);

            LoadImageUtil.onLoadImage(viewholder.userIcon, "http://img2.imgtn.bdimg.com/it/u=3234961814,2409259132&fm=27&gp=0.jpg");
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
            allImageUri =  mapList.get(position).get("picture").toString();

            if(getImageCount(position) == 1) {
                viewholder.gridView.setNumColumns(1);
            }
            if(getImageCount(position) == 2) {
                viewholder.gridView.setNumColumns(2);
            }
            if(getImageCount(position) >= 3){
                ViewGroup.LayoutParams para = viewholder.gridView.getLayoutParams();
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                para.height = (int)(Math.ceil(getImageCount(position)/3f))*((dm.widthPixels-4)/3);
                viewholder.gridView.setLayoutParams(para);
            }
            viewholder.gridView.setAdapter(new MyGridViewAdapter(context,R.layout.layout_gridview,getAllImageUri(position)));

            viewholder.count_support.setOnClickListener(onClickListener);
            viewholder.count_browse.setOnClickListener(onClickListener);
            viewholder.count_evaluate.setOnClickListener(onClickListener);
            viewholder.count_share.setOnClickListener(onClickListener);


            convertView.setTag(viewholder);
        }
        else {
            viewholder=(ViewHolder)convertView.getTag();
        }
        //调用方法传递所需信息

        return convertView;
    }

    class ViewHolder{
        public TextView userID;
        public TextView date;
        public ImageView userIcon;
        public TextView comContext;
        public android.support.v7.widget.GridLayout grid_image;
        public TextView count_support;
        public TextView count_browse;
        public TextView count_evaluate;
        public TextView count_share;
        public GridView gridView;
    }

    public int getImageCount(int position){
        String[] arr = allImageUri.split(";");
        Log.e("info","图片数量"+arr.length);
        return arr.length;

    }

    public String getImageUri(int position){
        String[] arr = allImageUri.split(";");
        return arr[position];

    }

    public String[] getAllImageUri(int position){
        String[] arr = allImageUri.split(";");
        return arr;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.count_support:
                    TextView textView1 = (TextView) v;
                    if(v.isSelected()) {
                        v.setSelected(false);
                        textView1.setText((Integer.parseInt(textView1.getText().toString()) - 1) + "");
                    }else{
                        v.setSelected(true);

                        textView1.setText((Integer.parseInt(textView1.getText().toString()) + 1) + "");
                    }
                    break;
                case R.id.count_browse:
                    TextView textView2 = (TextView) v;
                    if(v.isSelected()) {
                        v.setSelected(false);
                        textView2.setText((Integer.parseInt(textView2.getText().toString()) - 1) + "");
                    }else{
                        v.setSelected(true);

                        textView2.setText((Integer.parseInt(textView2.getText().toString()) + 1) + "");
                    }
                    break;
                case R.id.count_evaluate:
                    TextView textView3 = (TextView) v;
                    if(v.isSelected()) {
                        v.setSelected(false);
                        textView3.setText((Integer.parseInt(textView3.getText().toString()) - 1) + "");
                    }else{
                        v.setSelected(true);

                        textView3.setText((Integer.parseInt(textView3.getText().toString()) + 1) + "");
                    }
                    break;
                case R.id.count_share:
                    TextView textView4 = (TextView) v;
                    if(v.isSelected()) {
                        v.setSelected(false);
                        textView4.setText((Integer.parseInt(textView4.getText().toString()) - 1) + "");
                    }else{
                        v.setSelected(true);

                        textView4.setText((Integer.parseInt(textView4.getText().toString()) + 1) + "");
                    }
                    break;
                    default:
                        break;
            }
        }
    };
}
