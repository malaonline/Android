package com.malalaoshi.android.entity;

import java.io.Serializable;

/**
 * Created by donald on 2017/5/9.
 */

public class TopicSubject implements Serializable{
    private String subject;
    private int topicNum;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TopicSubject(String subject, int topicNum, int id) {
        this.subject = subject;
        this.topicNum = topicNum;
        this.id = id;
    }

    public int getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(int topicNum) {
        this.topicNum = topicNum;
    }

    public String getSubject() {

        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
