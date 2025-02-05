package com.example.pichainventory.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Order {
    // Existing fields
    private String mName;
    private String mImageUrl;
    private String mDesc;
    private String mCont;
    private int mUnits;
    private String mCategory;
    private String mDate;
    private String mAddInfo;
    private String mStatus;
    private int mDayCount;
    private String mKey;

    public Order(String mName, String mImageUrl, String mDesc, String mCont, int mUnits, String mCategory, Date mDate, String mAddInfo, String mStatus, int mDayCount, String mKey) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.mDesc = mDesc;
        this.mCont = mCont;
        this.mUnits = mUnits;
        this.mCategory = mCategory;
        this.mDate = formatDate(mDate); // Format the date without time
        this.mAddInfo = mAddInfo;
        this.mStatus = mStatus;
        this.mDayCount = mDayCount;
        this.mKey = mKey;
    }

    public Order() {
        // Empty constructor needed
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    public int calculateDayCount() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date orderDate = dateFormat.parse(mDate);
            Date currentDate = new Date();
            long diffInMillis = currentDate.getTime() - orderDate.getTime();
            return (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Existing getters and setters

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

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public int getmDayCount() {
        return mDayCount;
    }

    public void setmDayCount(int mDayCount) {
        this.mDayCount = mDayCount;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
