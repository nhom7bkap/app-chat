package com.team7.app_chat.models;

import com.google.firebase.Timestamp;

public class Contact {
    private String nickName;
    private boolean isFriend;
    private boolean isBlocked;
    private String userId;
    private Timestamp created;

    public Contact() {
    }

    public Contact(String nickName, boolean isFriend, boolean isBlocked, String userId, Timestamp created) {
        this.nickName = nickName;
        this.isFriend = isFriend;
        this.isBlocked = isBlocked;
        this.userId = userId;
        this.created = created;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setisFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setisBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }
}
