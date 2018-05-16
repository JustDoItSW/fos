package com.fos.entity;

/**
 * Created by Apersonalive on 2018/5/15.
 */

public class UserInfo {
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
    /**
     * 用户Arduino板子序列号
     */
    private String userArduinoDeviceId;
    /**
     * 用户摄像头序列号
     */
    private String userCameraDeviceId;
    /**
     * 类名
     */
    private String className;

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
    public String getUserArduinoDeviceId() {
        return userArduinoDeviceId;
    }
    public void setUserArduinoDeviceId(String userArduinoDeviceId) {
        this.userArduinoDeviceId = userArduinoDeviceId;
    }
    public String getUserCameraDeviceId() {
        return userCameraDeviceId;
    }
    public void setUserCameraDeviceId(String userCameraDeviceId) {
        this.userCameraDeviceId = userCameraDeviceId;
    }


    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }

    public String toString(){
        return "UserInfo [userId="+ userId + ", userName=" + userName +", userPassword=" + userPassword +", userHeadImage=" + userHeadImage +
                ", userArduinoDeviceId=" + userArduinoDeviceId +", userCameraDeviceId=" + userCameraDeviceId + ", className=" + className + "]";
    }

}
