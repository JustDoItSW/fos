package com.fos.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.baidu.aip.imageclassify.AipImageClassify;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: TODO
 * @date 2018/6/11 22:29
 */

public class AIPlantUtil {

    public static AipImageClassify aic=null;
    private AIPlantUtil(){}

    public static AipImageClassify getAccess_token(String APP_ID,String API_KEY,String SECRET_KEY){
        if(aic==null){
            synchronized(AIPlantUtil.class){
                if(aic==null){
                    aic=new AipImageClassify(APP_ID,API_KEY,SECRET_KEY);
                }
            }
        }
        return aic;
    }

    public static String sample( AipImageClassify client, String path){
        HashMap<String,String> options=new HashMap<String,String>();
        byte[] file=readFile(path);
        if(file==null)
            return null;
        JSONObject res=client.plantDetect(file,options);
        try {
            String str= res.toString(2);
            return str;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static byte[] readFile(String path) {
        try {
            int inSampleSize=8;
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds=false;
            options.inSampleSize=inSampleSize;
            Bitmap bitmap=BitmapFactory.decodeFile(path,options);
            ByteArrayOutputStream byteArrayIOutputStream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayIOutputStream);
            byte[] byteFile=byteArrayIOutputStream.toByteArray();
            return byteFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
