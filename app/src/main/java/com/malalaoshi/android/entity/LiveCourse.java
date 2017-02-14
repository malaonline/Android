package com.malalaoshi.android.entity;

import android.os.Parcel;

/**
 * Created by kang on 16/10/13.
 */

public class LiveCourse extends BaseEntity{
    private String course_name;
    private Long course_start;
    private Long course_end;
    private String course_period;
    private Long course_fee;
    private Long course_lessons;
    private String course_grade;
    private String course_description;
    private Long room_capacity;
    private Long students_count;
    private String lecturer_name;
    private String lecturer_title;
    private String lecturer_bio;
    private String lecturer_avatar;
    private String assistant_name;
    private String assistant_avatar;
    private String assistant_phone;
    private String school_name;
    private String school_address;
    private boolean is_paid;

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public Long getCourse_start() {
        return course_start;
    }

    public void setCourse_start(Long course_start) {
        this.course_start = course_start;
    }

    public Long getCourse_end() {
        return course_end;
    }

    public void setCourse_end(Long course_end) {
        this.course_end = course_end;
    }

    public String getCourse_period() {
        return course_period;
    }

    public void setCourse_period(String course_period) {
        this.course_period = course_period;
    }

    public Long getCourse_fee() {
        return course_fee;
    }

    public void setCourse_fee(Long course_fee) {
        this.course_fee = course_fee;
    }

    public Long getCourse_lessons() {
        return course_lessons;
    }

    public void setCourse_lessons(Long course_lessons) {
        this.course_lessons = course_lessons;
    }

    public String getCourse_grade() {
        return course_grade;
    }

    public void setCourse_grade(String course_grade) {
        this.course_grade = course_grade;
    }

    public String getCourse_description() {
        return course_description;
    }

    public void setCourse_description(String course_description) {
        this.course_description = course_description;
    }

    public Long getRoom_capacity() {
        return room_capacity;
    }

    public void setRoom_capacity(Long room_capacity) {
        this.room_capacity = room_capacity;
    }

    public Long getStudents_count() {
        return students_count;
    }

    public void setStudents_count(Long students_count) {
        this.students_count = students_count;
    }

    public String getLecturer_name() {
        return lecturer_name;
    }

    public void setLecturer_name(String lecturer_name) {
        this.lecturer_name = lecturer_name;
    }

    public String getLecturer_title() {
        return lecturer_title;
    }

    public void setLecturer_title(String lecturer_title) {
        this.lecturer_title = lecturer_title;
    }

    public String getLecturer_bio() {
        return lecturer_bio;
    }

    public void setLecturer_bio(String lecturer_bio) {
        this.lecturer_bio = lecturer_bio;
    }

    public String getLecturer_avatar() {
        return lecturer_avatar;
    }

    public void setLecturer_avatar(String lecturer_avatar) {
        this.lecturer_avatar = lecturer_avatar;
    }

    public String getAssistant_name() {
        return assistant_name;
    }

    public void setAssistant_name(String assistant_name) {
        this.assistant_name = assistant_name;
    }

    public String getAssistant_avatar() {
        return assistant_avatar;
    }

    public void setAssistant_avatar(String assistant_avatar) {
        this.assistant_avatar = assistant_avatar;
    }

    public String getAssistant_phone() {
        return assistant_phone;
    }

    public void setAssistant_phone(String assistant_phone) {
        this.assistant_phone = assistant_phone;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getSchool_address() {
        return school_address;
    }

    public void setSchool_address(String school_address) {
        this.school_address = school_address;
    }

    public boolean is_paid() {
        return is_paid;
    }

    public void setIs_paid(boolean is_paid) {
        this.is_paid = is_paid;
    }

    public LiveCourse() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.course_name);
        dest.writeValue(this.course_start);
        dest.writeValue(this.course_end);
        dest.writeString(this.course_period);
        dest.writeValue(this.course_fee);
        dest.writeValue(this.course_lessons);
        dest.writeString(this.course_grade);
        dest.writeString(this.course_description);
        dest.writeValue(this.room_capacity);
        dest.writeValue(this.students_count);
        dest.writeString(this.lecturer_name);
        dest.writeString(this.lecturer_title);
        dest.writeString(this.lecturer_bio);
        dest.writeString(this.lecturer_avatar);
        dest.writeString(this.assistant_name);
        dest.writeString(this.assistant_avatar);
        dest.writeString(this.assistant_phone);
        dest.writeString(this.school_name);
        dest.writeString(this.school_address);
        dest.writeByte(this.is_paid ? (byte) 1 : (byte) 0);
    }

    protected LiveCourse(Parcel in) {
        super(in);
        this.course_name = in.readString();
        this.course_start = (Long) in.readValue(Long.class.getClassLoader());
        this.course_end = (Long) in.readValue(Long.class.getClassLoader());
        this.course_period = in.readString();
        this.course_fee = (Long) in.readValue(Long.class.getClassLoader());
        this.course_lessons = (Long) in.readValue(Long.class.getClassLoader());
        this.course_grade = in.readString();
        this.course_description = in.readString();
        this.room_capacity = (Long) in.readValue(Long.class.getClassLoader());
        this.students_count = (Long) in.readValue(Long.class.getClassLoader());
        this.lecturer_name = in.readString();
        this.lecturer_title = in.readString();
        this.lecturer_bio = in.readString();
        this.lecturer_avatar = in.readString();
        this.assistant_name = in.readString();
        this.assistant_avatar = in.readString();
        this.assistant_phone = in.readString();
        this.school_name = in.readString();
        this.school_address = in.readString();
        this.is_paid = in.readByte() != 0;
    }

    public static final Creator<LiveCourse> CREATOR = new Creator<LiveCourse>() {
        @Override
        public LiveCourse createFromParcel(Parcel source) {
            return new LiveCourse(source);
        }

        @Override
        public LiveCourse[] newArray(int size) {
            return new LiveCourse[size];
        }
    };
}