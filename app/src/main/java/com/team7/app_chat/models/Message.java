package com.team7.app_chat.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Date;

public class Message {
    private String text;
    private Date createdDate;
    private boolean isNotify = false;
    private boolean isFile = false;
    private String fileUrl;
    private ArrayList<DocumentReference> viewer;
    private DocumentReference sendBy;

    public Message() {
    }

    public Message(String text, Date createdDate, boolean isNotify, boolean isFile, String fileUrl, ArrayList<DocumentReference> viewer, DocumentReference sendBy) {
        this.text = text;
        this.createdDate = createdDate;
        this.isNotify = isNotify;
        this.isFile = isFile;
        this.fileUrl = fileUrl;
        this.viewer = viewer;
        this.sendBy = sendBy;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    public DocumentReference getSendBy() {
        return sendBy;
    }

    public void setSendBy(DocumentReference sendBy) {
        this.sendBy = sendBy;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public ArrayList<DocumentReference> getViewer() {
        return viewer;
    }

    public void setViewer(ArrayList<DocumentReference> viewer) {
        this.viewer = viewer;
    }
}