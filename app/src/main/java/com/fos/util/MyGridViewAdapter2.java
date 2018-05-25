package com.fos.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.fos.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Apersonalive on 2018/5/24.
 */

public class MyGridViewAdapter2 extends BaseAdapter {


    private List<String> datas;
    private Context context;
    private LayoutInflater inflater;
    /**
     * 可以动态设置最多上传几张，之后就不显示+号了，用户也无法上传了
     * 默认9张
     */
    private int maxImages = 9;

    public MyGridViewAdapter2(List<String> datas, Context context) {
        this.datas = datas;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 获取最大上传张数
     *
     * @return
     */
    public int getMaxImages() {
        return maxImages;
    }

    /**
     * 设置最大上传张数
     *
     * @param maxImages
     */
    public void setMaxImages(int maxImages) {
        this.maxImages = maxImages;
    }

    /**
     * 让GridView中的数据数目加1最后一个显示+号
     *
     * @return 返回GridView中的数量
     */
    @Override
    public int getCount() {
        int count = datas == null ? 1 : datas.size() + 1;
        if (count > maxImages) {
            return datas.size();
        } else {
            return count;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void notifyDataSetChanged(List<String> datas) {
        this.datas = datas;
        this.notifyDataSetChanged();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_gridview, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (datas != null && position < datas.size()) {
            Glide.with(context)
                    .load(datas.get(position))
                    .priority(Priority.HIGH)
                    .into(viewHolder.ivimage);
            viewHolder.button.setVisibility(View.VISIBLE);
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datas.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else {

            Glide.with(context)
                    .load(R.mipmap.ic_add)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .into(viewHolder.ivimage);
            //  viewHolder.ivimage.setBackgroundResource(R.mipmap.image_add);
            viewHolder.ivimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.button.setVisibility(View.GONE);
        }

        return convertView;

    }

    public class ViewHolder {
        public final ImageView ivimage;
       // public final ImageView  removeImage;
        public final Button button;
        public final View root;

        public ViewHolder(View root) {
            ivimage = (ImageView) root.findViewById(R.id.gridview_image);
            //removeImage = (ImageView) root.findViewById(R.id.removeImage);
            button = (Button) root.findViewById(R.id.bt_del);
            ViewGroup.LayoutParams para =ivimage.getLayoutParams();
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            para.width = (dm.widthPixels-50)/3;
            para.height = para.width;
            ivimage.setLayoutParams(para);
            ivimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            this.root = root;
        }
    }
}
