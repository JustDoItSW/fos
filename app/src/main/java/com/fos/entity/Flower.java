package com.fos.entity;

import android.graphics.Bitmap;
import android.util.Log;

import com.fos.util.LoadImageUtil;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Apersonalive on 2018/4/17.
 */

public class Flower extends DataSupport implements Serializable{

    /**
     * 植物ID
     */
    private String flowerId;

    /**
     * 花朵图片路径
     */

    private String flowerImage;
    /**
     * 花朵名称
     */
    private String flowerName;
    /**
     *花朵别名
     */
    private String flowerOtherName;
    /**
     *花朵温度
     */
    private String flowerTemp;
    /**
     *花朵土壤湿度
     */
    private String flowerSoilHum;
    /**
     *花朵光强
     */
    private String flowerLux;
    /**
     *花朵信息
     */
    private String flowerInfo;




    public String getFlowerImage() {
        return flowerImage;
    }


    public void setFlowerImage(String flowerImage) {
        this.flowerImage = flowerImage;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public String getFlowerOtherName() {
        return flowerOtherName;
    }

    public void setFlowerOtherName(String flowerOtherName) {
        this.flowerOtherName = flowerOtherName;
    }

    public String getFlowerTemp() {
        return flowerTemp;
    }

    public void setFlowerTemp(String flowerTemp) {
        this.flowerTemp = flowerTemp;
    }

    public String getFlowerSoilHum() {
        return flowerSoilHum;
    }

    public void setFlowerSoilHum(String flowerSoilHum) {
        this.flowerSoilHum = flowerSoilHum;
    }

    public String getFlowerLux() {
        return flowerLux;
    }

    public void setFlowerLux(String flowerLux) {
        this.flowerLux = flowerLux;
    }

    public String getFlowerInfo() {
        return flowerInfo;
    }

    public void setFlowerInfo(String flowerInfo) {
        this.flowerInfo = flowerInfo;
    }

    public String getId() {
        return flowerId;
    }
    public void setId(String id) {
        this.flowerId = id;
    }
}
