package com.team7.app_chat.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class RoomChat {

    private DocumentReference room;
    private Date updatedAt;

    public RoomChat() {
    }

    public RoomChat(DocumentReference room, Date updatedAt) {
        this.room = room;
        this.updatedAt = updatedAt;
    }

    public DocumentReference getRoom() {
        return room;
    }

    public void setRoom(DocumentReference room) {
        this.room = room;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
