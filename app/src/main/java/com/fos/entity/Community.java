package com.fos.entity;

import java.io.Serializable;

/**
 * Created by Apersonalive on 2018/5/19.
 */

public class Community implements Serializable{
    private String ID;
    /**
     * 用户对象
     */

    private UserInfo userInfo;
    /**
     * 用户发动态时间
     */
    private String time;
    /**
     * 用户动态内容不超 200字符
     */
    private String content;
    /**
     * 用户动态中的图片 每张图URL不超50字符
     */
    private String picture;
    /**
     * 浏览量
     */
    private int browse;
    /**
     * 获得的点赞数
     */
    private int support;
    /**
     * 好友评论==》  用户名userId&评论
     */
    private int evaluate;
    /**
     * 类名
     */
    private String className =  "Community";

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getPicture() {
        return picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public int getBrowse() {
        return browse;
    }
    public void setBrowse(int browse) {
        this.browse = browse;
    }
    public int getSupport() {
        return support;
    }
    public void setSupport(int support) {
        this.support = support;
    }
    public int getEvaluate() {
        return evaluate;
    }
    public void setEvaluate(int evaluate) {
        this.evaluate = evaluate;
    }

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String toString(){
        return "Community [userInfo=" + userInfo.toString() + ", time=" + time + ", content=" + content + ", picture=" + picture + ", browse=" + browse +
                ", support=" + support + ",evaluate=" + evaluate+", className=" + className + ",type="+type+" ]";
    }
}

