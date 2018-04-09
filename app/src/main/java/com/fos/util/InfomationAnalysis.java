package com.fos.util;

import com.alibaba.fastjson.JSON;
import com.fos.entity.Infomation;


/**
 * Created by Apersonalive（丁起柠） on 2018/3/18 15 12.
 * Project_name TemperatureTest
 * Package_name dqn.demo.com.temperaturetest.AnalysisJson
 * Email 745267209@QQ.com
 */

public class InfomationAnalysis {
    String info = "";


    public InfomationAnalysis(String info){
        this.info = info;
    }

    public  static Infomation jsonToBean(String info){
        Infomation i = JSON.parseObject(info,Infomation.class);
        return i;
    }

}
