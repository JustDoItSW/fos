package com.fos.dao;

import android.database.Cursor;

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
    public Flower getPlantInfo(String plantName){
        Cursor cursor = DataSupport.findBySQL("select * from flower where flowerName = ?",plantName);
        Flower flower = new Flower();
        if(cursor.moveToFirst()){
            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String flowerName = cursor.getString(cursor.getColumnIndex("flowerName"));
                String flowerTemp = cursor.getString(cursor.getColumnIndex("flowerTemp"));
                String flowerSoilHum = cursor.getString(cursor.getColumnIndex("flowerSoilHum"));
                String flowerLux = cursor.getString(cursor.getColumnIndex("flowerLux"));
                String flowerInfo = cursor.getString(cursor.getColumnIndex("flowerInfo"));
                String flowerImage = cursor.getString(cursor.getColumnIndex("flowerImage"));
                String flowerOtherName = cursor.getString(cursor.getColumnIndex("flowerOtherName"));

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
}
