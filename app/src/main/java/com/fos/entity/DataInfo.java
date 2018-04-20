package com.fos.entity;

import org.litepal.crud.DataSupport;

/**
 * Author: 曾勇胜
 * Date: 2018/4/9 18:37
 * Email: 592813685@qq.com
 * Description: 关于花朵的各种传感数据实体类
 **/
public class DataInfo extends DataSupport {

    private long id;

    /**
     * 温度
     */
    private int temperature;

    /**
     * 湿度
     */
    private int humidity;

    /**
     * 光照强度
     */
    private int lux;

    /**
     * 水位高度
     */
    private int waterWeight;

    /**
     * 土壤湿度
     */
    private int soilHumidity;

    /**
     * 空气湿度
     */
    private int airHumidity;

    /**
     * 时间
     */
    private String date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getLux() {
        return lux;
    }

    public void setLux(int lux) {
        this.lux = lux;
    }

    public int getWaterWeight() {
        return waterWeight;
    }

    public void setWaterWeight(int waterWeight) {
        this.waterWeight = waterWeight;
    }

    public int getSoilHumidity() {
        return soilHumidity;
    }

    public void setSoilHumidity(int soilHumidity) {
        this.soilHumidity = soilHumidity;
    }

    public int getAirHumidity() {
        return airHumidity;
    }

    public void setAirHumidity(int airHumidity) {
        this.airHumidity = airHumidity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DataInfo{" +
                "id='" + id + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", lux=" + lux +
                ", waterWeight=" + waterWeight +
                ", soilHumidity=" + soilHumidity +
                ", airHumidity=" + airHumidity +
                ", date='" + date + '\'' +
                '}';
    }
}
