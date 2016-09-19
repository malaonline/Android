package com.malalaoshi.android.entity;

/**
 * Created by kang on 15/12/29.
 */
public class ScheduleItem {
    public static int TYPE_DATE = 0;
    public static int TYPE_COURSE = 1;
    protected int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
