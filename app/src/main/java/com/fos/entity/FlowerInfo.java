package com.fos.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Apersonalive on 2018/5/2.
 */

public class FlowerInfo extends DataSupport {
    String soil;
    String light;
    String water;
    String fertilize;

    public FlowerInfo(String s1,String s2,String s3,String s4){
        this.soil=s1;
        this.light=s2;
        this.water=s3;
        this.fertilize=s4;
    }
    public FlowerInfo(){}
    public String getSoil() {
        return soil;
    }

    public void setSoil(String soil) {
        this.soil = soil;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getFertilize() {
        return fertilize;
    }

    public void setFertilize(String fertilize) {
        this.fertilize = fertilize;
    }

    public String toString(){
        return "FlowerInfo [soil=" + soil + ", light=" + light + ", water=" + water +",fertilize"+fertilize+ "]";
    }
    public String dataError() {
        // TODO Auto-generated method stub
        return "FlowerInfo [soil=" + "error" + ", light=" + "error" + ", water=" + "error" +",fertilize"+"error"+ "]";
    }
}

