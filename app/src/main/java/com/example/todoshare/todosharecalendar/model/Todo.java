package com.example.todoshare.todosharecalendar.model;

/**
 * Created by OHRok on 2017-12-31.
 */

public class Todo {

    private String mNum;
    private String mDate;
    private String mTime;
    private String mTitle;
    private String mContent;
    private String mWritor;
    private String mShare;

    public Todo(String mNum, String mDate, String mTime, String mTitle, String mContent, String mWritor, String mShare) {
        this.mNum = mNum;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mTitle = mTitle;
        this.mContent = mContent;
        this.mWritor = mWritor;
        this.mShare = mShare;
    }

    public String getmNum() {
        return mNum;
    }

    public void setmNum(String mNum) {
        this.mNum = mNum;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmWritor() {
        return mWritor;
    }

    public void setmWritor(String mWritor) {
        this.mWritor = mWritor;
    }

    public String getmShare() {
        return mShare;
    }

    public void setmShare(String mShare) {
        this.mShare = mShare;
    }
}
