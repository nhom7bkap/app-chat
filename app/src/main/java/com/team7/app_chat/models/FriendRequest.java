package com.team7.app_chat.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class FriendRequest {

    // 0 = DeAccept
    // 1 = wait
    // 2 = accept

    private DocumentReference sender;
    private String senderId;
    private String receiverId;
    private Date created_at;


    public FriendRequest() {
    }

    public FriendRequest(DocumentReference sender, String senderId, String receiverId,Date created_at) {
        this.sender = sender;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.created_at = created_at;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public DocumentReference getSender() {
        return sender;
    }

    public void setSender(DocumentReference sender) {
        this.sender = sender;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
