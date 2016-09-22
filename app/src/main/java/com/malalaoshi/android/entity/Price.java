package com.malalaoshi.android.entity;

import android.os.Parcel;

/**
 * Created by kang on 16/9/22.
 */

public class Price extends BaseEntity{
    private Long min_hours;
    private Long max_hours;
    private Long price;

    public Long getMin_hours() {
        return min_hours;
    }

    public void setMin_hours(Long min_hours) {
        this.min_hours = min_hours;
    }

    public Long getMax_hours() {
        return max_hours;
    }

    public void setMax_hours(Long max_hours) {
        this.max_hours = max_hours;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.min_hours);
        dest.writeValue(this.max_hours);
        dest.writeValue(this.price);
    }

    public Price() {
    }

    protected Price(Parcel in) {
        super(in);
        this.min_hours = (Long) in.readValue(Long.class.getClassLoader());
        this.max_hours = (Long) in.readValue(Long.class.getClassLoader());
        this.price = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<Price> CREATOR = new Creator<Price>() {
        @Override
        public Price createFromParcel(Parcel source) {
            return new Price(source);
        }

        @Override
        public Price[] newArray(int size) {
            return new Price[size];
        }
    };

    @Override
    public String toString() {
        return "Price{" +
                "min_hours=" + min_hours +
                ", max_hours=" + max_hours +
                ", price=" + price +
                '}';
    }
}
