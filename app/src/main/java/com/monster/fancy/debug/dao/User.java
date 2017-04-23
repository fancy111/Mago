package com.monster.fancy.debug.dao;

import java.io.Serializable;

/**
 * Created by fancy on 2017/4/21.
 */
public class User implements Serializable {
    private int userID;
    private boolean userStar;//star friend
    private String photoUrl;
    private String userRemark;
    private String userNickName;
    private String userRealName;
    private String userSignature;
    private String userGender;
    private String userPhone;
    private String sortLetter;

    public String getSortLetter() {
        return sortLetter;
    }

    public int getUserID() {
        return userID;
    }

    public boolean isUserStar() {
        return userStar;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public String getUserSignature() {
        return userSignature;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUserStar(boolean userStar) {
        this.userStar = userStar;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public void setUserSignature(String userSignature) {
        this.userSignature = userSignature;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }
}
