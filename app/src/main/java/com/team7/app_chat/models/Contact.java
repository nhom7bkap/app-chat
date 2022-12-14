package com.team7.app_chat.models;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.Date;


public class Contact {


    private String nickName;
    private boolean blocked;
    private DocumentReference user;
    private Date created;

    public Contact() {
    }

    public Contact(String nickName, boolean blocked, DocumentReference user, Date created) {
        this.nickName = nickName;
        this.blocked = blocked;
        this.user = user;
        this.created = created;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public DocumentReference getUser() {
        return user;
    }

    public void setUser(DocumentReference user) {
        this.user = user;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
