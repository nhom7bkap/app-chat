package com.team7.app_chat.models;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;


public class Contact {

    @Exclude
    private String id;
    private String nickName;
    private boolean friend;
    private boolean blocked;
    private DocumentReference user;
    private Timestamp created;

    public Contact() {
    }

    public Contact(String id, String nickName, boolean friend, boolean blocked, DocumentReference user, Timestamp created) {
        this.id = id;
        this.nickName = nickName;
        this.friend = friend;
        this.blocked = blocked;
        this.user = user;
        this.created = created;
    }

    public Contact(String nickName, boolean friend, boolean blocked, DocumentReference user, Timestamp created) {
        this.nickName = nickName;
        this.friend = friend;
        this.blocked = blocked;
        this.user = user;
        this.created = created;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Exclude
    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    @Exclude
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

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }
}
