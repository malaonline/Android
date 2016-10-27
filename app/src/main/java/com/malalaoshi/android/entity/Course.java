package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kang on 16/2/17.
 */
public class Course implements Parcelable, Comparable<Course> {
    private Integer id;
    private String grade;
    private String subject;
    private boolean is_passed;
    private Long start;
    private Long end;
    private boolean is_commented;
    private String school;
    private Teacher teacher;
    private Teacher lecturer;
    private Comment comment;
    private boolean is_expired;
    private boolean is_live;
    public Course() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean is_passed() {
        return is_passed;
    }

    public void setIs_passed(boolean is_passed) {
        this.is_passed = is_passed;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public boolean is_commented() {
        return is_commented;
    }

    public void setIs_commented(boolean is_commented) {
        this.is_commented = is_commented;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public boolean is_expired() {
        return is_expired;
    }

    public void setIs_expired(boolean is_expired) {
        this.is_expired = is_expired;
    }

    public Teacher getLecturer() {
        return lecturer;
    }

    public void setLecturer(Teacher lecturer) {
        this.lecturer = lecturer;
    }

    public boolean is_live() {
        return is_live;
    }

    public void setIs_live(boolean is_live) {
        this.is_live = is_live;
    }

    @Override
    public int compareTo(Course another) {
        return this.start.compareTo(another.start);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.grade);
        dest.writeString(this.subject);
        dest.writeByte(this.is_passed ? (byte) 1 : (byte) 0);
        dest.writeValue(this.start);
        dest.writeValue(this.end);
        dest.writeByte(this.is_commented ? (byte) 1 : (byte) 0);
        dest.writeString(this.school);
        dest.writeParcelable(this.teacher, flags);
        dest.writeParcelable(this.lecturer, flags);
        dest.writeParcelable(this.comment, flags);
        dest.writeByte(this.is_expired ? (byte) 1 : (byte) 0);
        dest.writeByte(this.is_live ? (byte) 1 : (byte) 0);
    }

    protected Course(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.grade = in.readString();
        this.subject = in.readString();
        this.is_passed = in.readByte() != 0;
        this.start = (Long) in.readValue(Long.class.getClassLoader());
        this.end = (Long) in.readValue(Long.class.getClassLoader());
        this.is_commented = in.readByte() != 0;
        this.school = in.readString();
        this.teacher = in.readParcelable(Teacher.class.getClassLoader());
        this.lecturer = in.readParcelable(Teacher.class.getClassLoader());
        this.comment = in.readParcelable(Comment.class.getClassLoader());
        this.is_expired = in.readByte() != 0;
        this.is_live = in.readByte() != 0;
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
}
