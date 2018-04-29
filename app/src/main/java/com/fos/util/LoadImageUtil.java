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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogRecord;

/**
 * Created by Apersonalive on 2018/4/23.
 */

public class LoadImageUtil {

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    public static void  onLoadImage(final ImageView imageView, final String urlPath){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
               // onLoadImageListener.OnLoadImage((Bitmap)msg.obj,null);
                if((Bitmap)msg.obj!=null) {
                    imageView.setImageBitmap(BitmapSetting.resizeBitmap(BitmapSetting.getOvalBitmap((Bitmap)msg.obj),50,50));
                }
            }
        };
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.e("info",Thread.currentThread().getName() + "线程被调用了。");
                    URL imageUrl =new URL(urlPath);
                    Log.e("info", "下载图片地址："+urlPath);
                    HttpURLConnection connection = (HttpURLConnection)imageUrl.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);

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

}
