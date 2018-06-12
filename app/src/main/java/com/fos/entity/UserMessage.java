package com.fos.entity;

import java.io.Serializable;

/**
 * Created by Apersonalive on 2018/6/11.
 */

public class UserMessage implements Serializable {
    private String content;
    public UserMessage(){}
    public UserMessage(String content){this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
