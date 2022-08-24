package com.team7.app_chat.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;
import java.util.List;

public class User {
    @Exclude private String id;
    private String userName;
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private int gender;
    private Date DOB;
    private String address;
    private String avatar;
    private boolean verification;
    private boolean firstTime;
    private int type;
    private int status;
    private Date created_at;
    private Date updated_at;

    public User() {
    }



    public User(String id, String userName, String email, String password, String confirmPassword, String fullName, int gender, Date DOB, String address, String avatar, boolean verification, boolean firstTime, int type, int status, Date created_at, Date updated_at) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.fullName = fullName;
        this.gender = gender;
        this.DOB = DOB;
        this.address = address;
        this.avatar = avatar;
        this.verification = verification;
        this.firstTime = firstTime;
        this.type = type;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getDOB() {
        return DOB;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    @Exclude
    @Override
    public String toString() {
        return "User{" +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender=" + gender +
                ", DOB='" + DOB + '\'' +
                ", address='" + address + '\'' +
                ", avatar='" + avatar + '\'' +
                ", verification=" + verification +
                ", type=" + type +
                ", status=" + status +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }


}
