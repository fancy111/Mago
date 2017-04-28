package com.monster.fancy.debug.dao;

import java.io.Serializable;

/**
 * Created by fancy on 2017/4/24.
 */
public class Friend implements Serializable {
    private int friendID;
    private boolean starFriend;//star friend
    private String photoUrl;
    private String remark;
    private String nickName;
    private String realName;
    private String signature;
    private String gender;
    private String phone;
    private String letter;
    private String sortLetter;

    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }

    public void setFriendID(int friendID) {
        this.friendID = friendID;
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

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public int getFriendID() {
        return friendID;
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

    public String getNickName() {
        return nickName;
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
