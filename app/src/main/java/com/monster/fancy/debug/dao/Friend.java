package com.monster.fancy.debug.dao;

import java.io.Serializable;

/**
 * Created by fancy on 2017/4/24.
 */
public class Friend implements Serializable {
    private String ID;
    private boolean starFriend;//star friend
    private String photoUrl;
    private String remark;
    private String username;
    private String realName;
    private String signature;
    private String gender;
    private String phone;
    private String letter;
    private String sortLetter;

    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setStarFriend(boolean starFriend) {
        this.starFriend = starFriend;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getID() {
        return ID;
    }

    public boolean isStarFriend() {
        return starFriend;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getRemark() {
        return remark;
    }

    public String getUsername() {
        return username;
    }

    public String getRealName() {
        return realName;
    }

    public String getSignature() {
        return signature;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getLetter() {
        return letter;
    }

    public String getSortLetter() {
        return sortLetter;
    }
}
