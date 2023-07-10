package com.example.pichainventory.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Order {
    private String mName;
    private String mImageUrl;
    private String mDesc;
    private String mCont;
    private int mUnits;
    private String mCategory;
    private String mDate;
    private String mAddInfo;
    private boolean mBrought;

    public Order(String mName, String mImageUrl, String mDesc, String mCont, int mUnits, String mCategory, Date mDate, String mAddInfo, boolean mBrought) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.mDesc = mDesc;
        this.mCont = mCont;
        this.mUnits = mUnits;
        this.mCategory = mCategory;
        this.mDate = formatDate(mDate);
        this.mAddInfo = mAddInfo;
        this.mBrought = mBrought;
    }
    public Order() {
        //empty constructor needed
    }
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }
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

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getmCont() {
        return mCont;
    }

    public void setmCont(String mCont) {
        this.mCont = mCont;
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

    public String getmAddInfo() {
        return mAddInfo;
    }

    public void setmAddInfo(String mAddInfo) {
        this.mAddInfo = mAddInfo;
    }

    public boolean ismBrought() {
        return mBrought;
    }

    public void setmBrought(boolean mBrought) {
        this.mBrought = mBrought;
    }
}
