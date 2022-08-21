package com.team7.app_chat.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class RoomChat {

    @Exclude
    private String id;
    private String avatar;
    private String name;
    private boolean isPublic;
    private DocumentReference member;
    private DocumentReference messages;
    private Date created_at;

    public RoomChat() {
    }

    public RoomChat(String avatar, String name, boolean isPublic, DocumentReference member, DocumentReference messages, Date created_at) {
        this.avatar = avatar;
        this.name = name;
        this.isPublic = isPublic;
        this.member = member;
        this.messages = messages;
        this.created_at = created_at;
    }

    public RoomChat(String id, String avatar, String name, boolean isPublic, DocumentReference member, DocumentReference messages, Date created_at) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.isPublic = isPublic;
        this.member = member;
        this.messages = messages;
        this.created_at = created_at;
    }

    @Exclude
    public String getId() {
        return id;
    }
    @Exclude
    public void setId(String id) {
        this.id = id;
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

    public DocumentReference getMember() {
        return member;
    }

    public void setMember(DocumentReference member) {
        this.member = member;
    }

    public DocumentReference getMessages() {
        return messages;
    }

    public void setMessages(DocumentReference messages) {
        this.messages = messages;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
