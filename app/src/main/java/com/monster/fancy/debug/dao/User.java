package com.monster.fancy.debug.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fancy on 2017/4/21.
 */
public class User implements Serializable {
    private int userID;
    private String photoUrl;
    private String nickName;
    private String realName;
    private String signature;
    private String gender;
    private String phone;
    private String password;
    private List<Friend> friendList;

    //setters and getters

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserID() {
        return userID;
    }

    public List<Friend> getFriendList() {
        return friendList;
    }

    public String getPhotoUrl() {
        return photoUrl;
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

    public String getPassword() {
        return password;
    }
}
