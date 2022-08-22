package com.team7.app_chat.models;

import com.google.firebase.firestore.DocumentReference;

public class Member {
    private boolean isMod;
    private DocumentReference user;

    public Member() {
    }

    public Member(boolean isMod, DocumentReference user) {
        this.isMod = isMod;
        this.user = user;
    }

    public boolean isMod() {
        return isMod;
    }

    public void setMod(boolean mod) {
        isMod = mod;
    }

    public DocumentReference getUser() {
        return user;
    }

    public void setUser(DocumentReference user) {
        this.user = user;
    }
}
