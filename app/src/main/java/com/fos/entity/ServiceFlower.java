package com.fos.entity;

/**
 * Created by Apersonalive on 2018/6/7.
 */

public class ServiceFlower {
    private String flowerName;
    private String className =  "Flower";
    private int type = 0;

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String toString() {
        return "Flower [ "+"flowerName="+flowerName+",className="+className+",type="+type+" ]";
    }
}
