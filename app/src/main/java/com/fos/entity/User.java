package com.fos.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Apersonalive on 2018/5/29.
 */

public class User extends DataSupport{

    /**

     * 用户账号
     */
    private String userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 用户头像
     */
    private String userHeadImage;



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserHeadImage() {
        return userHeadImage;
    }

    public void setUserHeadImage(String userHeadImage) {
        this.userHeadImage = userHeadImage;
    }


    public String toString(){
        return "UserInfo [userId="+ userId + ", userName=" + userName +", userPassword=" + userPassword +", userHeadImage=" + userHeadImage +
                 "]";
    }

}
