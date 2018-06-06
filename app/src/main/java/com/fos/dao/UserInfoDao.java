package com.fos.dao;

import android.database.Cursor;
import android.util.Log;

import com.fos.entity.User;

import org.litepal.crud.DataSupport;

/**
 * Created by Apersonalive on 2018/5/29.
 */

public class UserInfoDao {
    private volatile static UserInfoDao instance;

    public static UserInfoDao getInstance(){
        if (instance == null){
            synchronized (UserInfoDao.class){
                if (instance == null){
                    instance = new UserInfoDao();
                }
            }
        }
        return instance;
    }
    private UserInfoDao(){
    }

    public User[] getAllUserInfo(){
        Cursor cursor = DataSupport.findBySQL("select * from User");
        int resultCounts =cursor.getCount();
        Log.e("info","表中有数据条目："+resultCounts+"");
        if(resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        User[] user = new User[resultCounts];
        for(int i =0;i<resultCounts;i++){
            user[i] = new User();
            String UserID = cursor.getString(cursor.getColumnIndex("userid"));;
            String UserIcon = cursor.getString(cursor.getColumnIndex("userheadimage"));
            String UserPW = cursor.getString(cursor.getColumnIndex("userpassword"));

            user[i].setUserId(UserID);
            user[i].setUserPassword(UserPW);
            user[i].setUserHeadImage(UserIcon);
            cursor.moveToNext();
        }
        return user;
    }

    public User getUserInfo(String userId){
        Cursor cursor = DataSupport.findBySQL("select * from User where userid = ?",userId);
        User user = new User();
        if(cursor.moveToFirst()){
            do {
                String UserID = cursor.getString(cursor.getColumnIndex("userid"));;
                String UserIcon = cursor.getString(cursor.getColumnIndex("userheadimage"));
                String UserPW = cursor.getString(cursor.getColumnIndex("userpassword"));

                user.setUserId(UserID);
                user.setUserPassword(UserPW);
                user.setUserHeadImage(UserIcon);
            }while (cursor.moveToNext());
        }
        return user;
    }

    /**
     * 更新头像后调用此方法
     * @param user
     */
    public void insertUserInfo(User  user){

        Cursor cursor = DataSupport.findBySQL(" select * from User where userid = ? ",user.getUserId());
        if(cursor.moveToFirst()){
            String userId = cursor.getString(cursor.getColumnIndex("userid"));
            String userIcon = cursor.getString(cursor.getColumnIndex("userheadimage"));
            if("".equals(userIcon)) {
                user.updateAll("userid=?", userId);
            }
        }else
            user.save();
    }
}
