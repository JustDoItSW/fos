package com.fos.entity;

/**
 * Created by Apersonalive on 2018/6/7.
 */

public class Evaluate {

    private UserInfo userInfo;//用户信息
    private String content;//评论内容
    private String date ;//评论时间p
    private String className = "Evaluate";


    private int communityID;//所属动态id
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public int getCommunityID() {
        return communityID;
    }

    public void setCommunityID(int communityID) {
        this.communityID = communityID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String toString() {
        return "Evaluate [ userInfo = "+userInfo.toString()+",content="+content+",date="+date+",className="+className+",communityID="+communityID+",type="+type+" ]";
    }
}
