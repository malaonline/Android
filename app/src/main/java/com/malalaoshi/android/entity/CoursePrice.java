package com.malalaoshi.android.entity;

import android.os.Parcel;

import java.util.List;

/**
 * Created by kang on 15/12/24.
 */
public class CoursePrice extends BaseEntity {
    private Long grade;
    private String grade_name;
    private List<Price> prices;
    private boolean check;

    public Long getGrade() {
        return grade;
    }

    public void setGrade(Long grade) {
        this.grade = grade;
    }

    public String getGrade_name() {
        return grade_name;
    }

    public void setGrade_name(String grade_name) {
        this.grade_name = grade_name;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.grade);
        dest.writeString(this.grade_name);
        dest.writeTypedList(this.prices);
        dest.writeByte(this.check ? (byte) 1 : (byte) 0);
    }

    public CoursePrice() {
    }

    protected CoursePrice(Parcel in) {
        super(in);
        this.grade = (Long) in.readValue(Long.class.getClassLoader());
        this.grade_name = in.readString();
        this.prices = in.createTypedArrayList(Price.CREATOR);
        this.check = in.readByte() != 0;
    }

    public static final Creator<CoursePrice> CREATOR = new Creator<CoursePrice>() {
        @Override
        public CoursePrice createFromParcel(Parcel source) {
            return new CoursePrice(source);
        }

        @Override
        public CoursePrice[] newArray(int size) {
            return new CoursePrice[size];
        }
    };
}
