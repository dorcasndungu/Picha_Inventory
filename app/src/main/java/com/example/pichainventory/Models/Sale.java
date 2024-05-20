package com.example.pichainventory.Models;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Sale {
    private String mName;
    private String mImageUrl;
    private int mBp;
    private int mSp;
    private int mProfit;
    private int mUnits;
    private String mCategory;
    private String mDate;
//    private String mTime;
    private String mAddInfo;
    private String mPayMode;

    public Sale() {
        //empty constructor needed
    }
    public Sale(String mName, String mImageUrl, int mBp, int mSp, int mUnits, String mCategory, Date mDate, String mAddInfo, String mPayMode) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.mBp = mBp;
        this.mSp = mSp;
        this.mProfit = mSp-mBp;
        this.mUnits = mUnits;
        this.mCategory = mCategory;
        this.mDate = formatDate(mDate);
//        this.mTime=formatTime(mTime);
        this.mAddInfo = mAddInfo;
        this.mPayMode = mPayMode;
    }
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }
//    private String formatTime(Time time) {
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        return timeFormat.format(time);
//    }
    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public int getmBp() {
        return mBp;
    }

    public void setmBp(int mBp) {
        this.mBp = mBp;
    }

    public int getmSp() {
        return mSp;
    }

    public void setmSp(int mSp) {
        this.mSp = mSp;
    }

    public int getmProfit() {
        return mProfit;
    }

    public void setmProfit(int mProfit) {
        this.mProfit = mProfit;
    }

    public int getmUnits() {
        return mUnits;
    }

    public void setmUnits(int mUnits) {
        this.mUnits = mUnits;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

//    public String getmTime() {
//        return mTime;
//    }
//
//    public void setmTime(String mTime) {
//        this.mTime = mTime;
//    }

    public String getmAddInfo() {
        return mAddInfo;
    }

    public void setmAddInfo(String mAddInfo) {
        this.mAddInfo = mAddInfo;
    }

    public String getmPayMode() {
        return mPayMode;
    }

    public void setmPayMode(String mPayMode) {
        this.mPayMode = mPayMode;
    }
}
