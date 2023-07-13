package com.example.pichainventory.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Upload {
    private String mName;
    private String mImageUrl;
    private int mBp;
    private int mSp;
    private int mUnits;
    private String mCategory;
    private String mDate;
    private String mAddInfo;
    private String mKey;

    public Upload() {
        //empty constructor needed
    }
    public Upload(String mName, String mImageUrl, int mBp, int mSp, int mUnits, String mCategory, Date mDate, String mAddInfo, String mKey) {
//        if (mName.trim().equals("")) {
//            mName = "No Name";
//        }
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.mBp = mBp;
        this.mSp = mSp;
        this.mUnits = mUnits;
        this.mCategory = mCategory;
        this.mDate = formatDate(mDate); // Format the date without time
        this.mAddInfo = mAddInfo;
        this.mKey = mKey;
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

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}