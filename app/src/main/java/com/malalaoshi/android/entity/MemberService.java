package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.support.annotation.DrawableRes;

/**
 * Created by kang on 15/12/24.
 */
public class MemberService extends BaseEntity {

    private String detail;
    private boolean enbaled;
    private String title;
    @DrawableRes
    private int resId;

    public MemberService(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean isEnbaled() {
        return enbaled;
    }

    public void setEnbaled(boolean enbaled) {
        this.enbaled = enbaled;
    }

    @Override
    public String toString() {
        return "MemberService{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", enbaled=" + enbaled +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.detail);
        dest.writeByte(this.enbaled ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
        dest.writeInt(this.resId);
    }

    public MemberService() {
    }

    protected MemberService(Parcel in) {
        super(in);
        this.detail = in.readString();
        this.enbaled = in.readByte() != 0;
        this.title = in.readString();
        this.resId = in.readInt();
    }

    public static final Creator<MemberService> CREATOR = new Creator<MemberService>() {
        @Override
        public MemberService createFromParcel(Parcel source) {
            return new MemberService(source);
        }

        @Override
        public MemberService[] newArray(int size) {
            return new MemberService[size];
        }
    };
}
