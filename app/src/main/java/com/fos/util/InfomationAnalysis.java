package com.fos.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fos.entity.Flower;
import com.fos.entity.FlowerInfo;
import com.fos.entity.Infomation;
import com.fos.entity.JudgeClass;
import com.fos.entity.UserInfo;

import java.util.List;


/**
 * Created by Apersonalive（丁起柠） on 2018/3/18 15 12.
 * Project_name TemperatureTest
 * Package_name dqn.demo.com.temperaturetest.AnalysisJson
 * Email 745267209@QQ.com
 */

public class InfomationAnalysis {
    String info = "";


    public InfomationAnalysis(String info){
        this.info = info;
    }

    public  static Infomation jsonToData(String info){
        if(info!=null) {
            Infomation i = JSON.parseObject(info, Infomation.class);
            return i;
        }else{
            return null;
        }
    }
    public static  String  judgeInfo(String info){
        try {
            if(info!=null) {
                JSONArray myJsonArray = JSONArray.parseArray(info);
               // String str = JSONObject.parseObject(myJsonArray.get(0).toString(), JudgeClass.class).getClassName();
                String str = JSON.parseObject(myJsonArray.get(0).toString()).getString("className");
                if(str.equals("Flower") && JSONObject.parseObject(myJsonArray.get(0).toString(), Flower.class).getFlowerName().equals("error")){
                    return "error";
                }
                return str;
            }
            return null;
        }catch (Exception  e){
            e.printStackTrace();
            String str2 = JSON.parseObject(info).getString("className");
            return str2;
        }
    }

    public  static Flower[] jsonToFlower(String info){
        try {
            if(info!= null) {
                Log.e("info", "开始解析植物");
                JSONArray myJsonArray = JSONArray.parseArray(info);
                Flower[] flowers = new Flower[myJsonArray.size()];
                for (int i = 0; i < myJsonArray.size(); i++) {
                    flowers[i] = JSONObject.parseObject(myJsonArray.get(i).toString(), Flower.class);
                }
                return flowers;
            }
            else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static FlowerInfo jsonToFlowerInfo(String info){
//        Flower[] flowers = jsonToFlower(info);
//        FlowerInfo[] flowerInfos = new FlowerInfo[flowers.length];
//        for(int i = 0;i<flowers.length;i++){
//            if(flowers[i].getFlowerInfo()!= null)
//            flowerInfos[i] = JSONObject.parseObject(flowers[i].getFlowerInfo(),FlowerInfo.class);
//        }
//        return flowerInfos;
        if(info!=null){
            return JSONObject.parseObject(info,FlowerInfo.class);
        }
        else
            return null;
    }

    public static String BeantoUserInfo(UserInfo userInfo){
        return JSON.toJSONString(userInfo);
    }

    public static UserInfo jsonToUserInfo(String s){
        if(s!=null) {
            UserInfo u= JSON.parseObject(s, UserInfo.class);
            return u;
        }else{
            return null;
        }
    }
}
