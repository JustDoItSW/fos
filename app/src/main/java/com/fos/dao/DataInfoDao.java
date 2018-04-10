package com.fos.dao;

import com.fos.util.LogUtil;

import com.fos.database.DataInfo;

import org.litepal.crud.DataSupport;

/**
 * Author: 曾勇胜
 * Date: 2018/4/9 20:08
 * Email: 592813685@qq.com
 * Description: 数据信息Dao类
 **/
public class DataInfoDao {

    private static DataInfoDao instance;

    public static DataInfoDao getInstance(){
        if (instance == null){
            synchronized (DataInfoDao.class){
                if (instance == null){
                    instance = new DataInfoDao();
                }
            }
        }
        return instance;
    }

    private DataInfoDao(){
    }

    /**
     * 保存手机数据库天气数据
     * @return true 保存成功  false 保存失败
     */
    public boolean save(DataInfo dataInfo){
        boolean result = dataInfo.save();
        if (result==false){
            LogUtil.e("ERROR",dataInfo.toString()+"保存数据至数据库失败");
        }else{
            LogUtil.e("SUCCESS",dataInfo.toString()+"保存数据至数据库成功");
        }
        return result;
    }

    /**
     * 删除手机数据库所有天气数据
     */
    public void delete(){
        DataSupport.deleteAll(DataInfo.class,null,null);
    }

    /**
     * 获得数据库的第一条数据
     */
    public DataInfo findFirstData(){
        return DataSupport.findFirst(DataInfo.class);
    }

    /**
     * 获得数据库的最后一条数据
     */
    public DataInfo findLastData(){
        return DataSupport.findLast(DataInfo.class);
    }

    /**
     * 更新数据
     */
    public void updateData(DataInfo dataInfo){
        DataInfo data = dataInfo;
        data.updateAll("id = ?","1");
    }
}
