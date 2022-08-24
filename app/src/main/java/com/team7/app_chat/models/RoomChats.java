package com.team7.app_chat.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class RoomChats {
    private DocumentReference room;
    private Date updated_at;

    public RoomChats(DocumentReference room, Date updated_at) {
        this.room = room;
        this.updated_at = updated_at;
    }

    public DocumentReference getRoom() {
        return room;
    }

    public void setRoom(DocumentReference room) {
        this.room = room;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
