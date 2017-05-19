package com.malalaoshi.android.entity;

import com.malalaoshi.android.core.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by donald on 2017/5/11.
 */

public class TopicAnswer extends BaseEntity implements Serializable{
    /**
     * id : 401
     * text : million of
     */

    private int id;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TopicAnswer{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
