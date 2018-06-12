package com.fos.entity;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: 识别植物类
 * @date 2018/6/11 21:49
 */

public class AIPlant {
    /**
     * 置信度
     */
    private String score;
    /**
     * 植物名称
     */
    private String name;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
