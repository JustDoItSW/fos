package com.fos.dao;

import android.database.Cursor;
import android.util.Log;

import com.fos.entity.Flower;

import org.litepal.crud.DataSupport;

/**
 * Author: 曾勇胜
 * Date: 2018/4/18 10:27
 * Email: 592813685@qq.com
 * Description: 植物Dao类
 **/
public class FlowerDao {
    private volatile static FlowerDao instance;

    public static FlowerDao getInstance(){
        if (instance == null){
            synchronized (FlowerDao.class){
                if (instance == null){
                    instance = new FlowerDao();
                }
            }
        }
        return instance;
    }

    private FlowerDao(){
    }

    /**
     * 查询植物的信息
     */
    public void insertFlower(Flower[]  flower){


        for(int i= 0;i<flower.length;i++) {
            Cursor cursor = DataSupport.findBySQL("select * from flower where flowername = ?",flower[i].getFlowerName());
            if(cursor.moveToFirst()){
                String flowerName = cursor.getString(cursor.getColumnIndex("flowername"));
                Log.e("info","插入的花名为："+flowerName+"");
                if(flowerName.equals(flower[i].getFlowerName()))
                    Log.e("info",""+flowerName+"已存在");
                    continue;
            }
            flower[i].setId(flower[i].getId());
            flower[i].setFlowerName(flower[i].getFlowerName());
            flower[i].setFlowerTemp(flower[i].getFlowerTemp());
            flower[i].setFlowerLux(flower[i].getFlowerLux());
            flower[i].setFlowerInfo(flower[i].getFlowerInfo());
            flower[i].setFlowerSoilHum(flower[i].getFlowerSoilHum());
            flower[i].setFlowerOtherName(flower[i].getFlowerOtherName());
            flower[i].setFlowerImage(flower[i].getFlowerImage());

            flower[i].save();
        }
    }
    public Flower getPlantInfo(String plantName){
        Cursor cursor = DataSupport.findBySQL("select * from flower where flowername = ?",plantName);
        Flower flower = new Flower();
        if(cursor.moveToFirst()){
            do {
                String id = cursor.getString(cursor.getColumnIndex("flowerid"));
                String flowerName = cursor.getString(cursor.getColumnIndex("flowername"));
                String flowerTemp = cursor.getString(cursor.getColumnIndex("flowertemp"));
                String flowerSoilHum = cursor.getString(cursor.getColumnIndex("flowersoilhum"));
                String flowerLux = cursor.getString(cursor.getColumnIndex("flowerlux"));
                String flowerInfo = cursor.getString(cursor.getColumnIndex("flowerinfo"));
                String flowerImage = cursor.getString(cursor.getColumnIndex("flowerimage"));
                String flowerOtherName = cursor.getString(cursor.getColumnIndex("flowerothername"));

                flower.setId(id);
                flower.setFlowerName(flowerName);
                flower.setFlowerTemp(flowerTemp);
                flower.setFlowerLux(flowerLux);
                flower.setFlowerInfo(flowerInfo);
                flower.setFlowerSoilHum(flowerSoilHum);
                flower.setFlowerOtherName(flowerOtherName);
                flower.setFlowerImage(flowerImage);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return flower;
    }

    public Flower[] getAllFlower(){
        Cursor cursor = DataSupport.findBySQL("select * from flower");
        int resultCounts =cursor.getCount();
        Log.e("info","表中有数据条目："+resultCounts+"");
        if(resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        Flower[] flowers = new Flower[resultCounts];
        for(int i =0;i<resultCounts;i++){
            flowers[i] = new Flower();
            String id = cursor.getString(cursor.getColumnIndex("flowerid"));
            String flowerName = cursor.getString(cursor.getColumnIndex("flowername"));
            String flowerTemp = cursor.getString(cursor.getColumnIndex("flowertemp"));
            String flowerSoilHum = cursor.getString(cursor.getColumnIndex("flowersoilhum"));
            String flowerLux = cursor.getString(cursor.getColumnIndex("flowerlux"));
            String flowerInfo = cursor.getString(cursor.getColumnIndex("flowerinfo"));
            String flowerImage = cursor.getString(cursor.getColumnIndex("flowerimage"));
            String flowerOtherName = cursor.getString(cursor.getColumnIndex("flowerothername"));

            flowers[i].setId(id);
            flowers[i].setFlowerName(flowerName);
            flowers[i].setFlowerTemp(flowerTemp);
            flowers[i].setFlowerLux(flowerLux);
            flowers[i].setFlowerInfo(flowerInfo);
            flowers[i].setFlowerSoilHum(flowerSoilHum);
            flowers[i].setFlowerOtherName(flowerOtherName);
            flowers[i].setFlowerImage(flowerImage);
            cursor.moveToNext();
        }
        return flowers;
    }

    public void delAll(){
        DataSupport.deleteAll("flower");
        Log.e("info","已清空");
    }

    public Flower[] searchFlower(String str) {
        Cursor cursor = DataSupport.findBySQL("select * from flower where flowername  like ?","%"+str+"%");
        int resultCounts =cursor.getCount();
        Log.e("info","搜索到的数据条目为"+resultCounts+"");
        if(resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        Flower[] flowers = new Flower[resultCounts];
        for(int i =0;i<resultCounts;i++){
            flowers[i] = new Flower();
            String id = cursor.getString(cursor.getColumnIndex("flowerid"));
            String flowerName = cursor.getString(cursor.getColumnIndex("flowername"));
            String flowerTemp = cursor.getString(cursor.getColumnIndex("flowertemp"));
            String flowerSoilHum = cursor.getString(cursor.getColumnIndex("flowersoilhum"));
            String flowerLux = cursor.getString(cursor.getColumnIndex("flowerlux"));
            String flowerInfo = cursor.getString(cursor.getColumnIndex("flowerinfo"));
            String flowerImage = cursor.getString(cursor.getColumnIndex("flowerimage"));
            String flowerOtherName = cursor.getString(cursor.getColumnIndex("flowerothername"));

            flowers[i].setId(id);
            flowers[i].setFlowerName(flowerName);
            flowers[i].setFlowerTemp(flowerTemp);
            flowers[i].setFlowerLux(flowerLux);
            flowers[i].setFlowerInfo(flowerInfo);
            flowers[i].setFlowerSoilHum(flowerSoilHum);
            flowers[i].setFlowerOtherName(flowerOtherName);
            flowers[i].setFlowerImage(flowerImage);
            cursor.moveToNext();
        }
        return flowers;
    }
}
