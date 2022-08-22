package com.team7.app_chat.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class RoomChat {

    private String avatar;
    private String name;
    private boolean isPublic;
    private DocumentReference lastMessage;
    private Date created_at;

    public RoomChat() {
    }

    public RoomChat(String avatar, String name, boolean isPublic, DocumentReference lastMessage, Date created_at) {
        this.avatar = avatar;
        this.name = name;
        this.isPublic = isPublic;
        this.lastMessage = lastMessage;
        this.created_at = created_at;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public DocumentReference getLastMessage() {
        return lastMessage;
    }

    public void setLastMessages(DocumentReference lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
