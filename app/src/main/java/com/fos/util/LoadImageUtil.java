package com.fos.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogRecord;

/**
 * Created by Apersonalive on 2018/4/23.
 */

public class LoadImageUtil {

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    public static Map<ImageView,String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    public static void  onLoadImage(final ImageView imageView, final String urlPath){
        imageViews.put(imageView, urlPath);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                    if ((Bitmap) msg.obj != null) {
                        if (imageView.getWidth() <= 50)
                            imageView.setImageBitmap(BitmapSetting.resizeBitmap(BitmapSetting.getOvalBitmap((Bitmap) msg.obj), 50, 50));
                        else
                            imageView.setImageBitmap(BitmapSetting.resizeBitmap(BitmapSetting.getOvalBitmap((Bitmap) msg.obj), imageView.getWidth(), imageView.getHeight()));

                    }
                }

        };
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    if(imageViewReused(imageView, urlPath))
                        return;
                    Log.e("info",Thread.currentThread().getName() + "线程被调用了。");
                    URL imageUrl =new URL(urlPath);
                    Log.e("info", "下载图片地址："+urlPath);
                    HttpURLConnection connection = (HttpURLConnection)imageUrl.openConnection();
                    connection.setInstanceFollowRedirects(true);
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
                    connection.disconnect();
                    if(imageViewReused(imageView, urlPath))
                        return;
                    Message msg = new Message();
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.e("info",Thread.currentThread().getName() + "线程结束。");
            }
        });

    }

    public  interface OnLoadImageListener{
        public void OnLoadImage(Bitmap bitmap,String bitmapPath);
    }

    public static boolean imageViewReused(ImageView  imageView,String url){
        String  tag = imageViews.get(imageView);
        if(tag == null|| !tag.equals(url)){
            return true;
        }
        return false;
    }

}
