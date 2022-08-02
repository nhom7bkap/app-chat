package com.team7.app_chat.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.team7.app_chat.Util.Identifiable;

import java.util.Date;

public class User implements Identifiable<String> {
    private int id;
    private String userName;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private int gender;
    private Date DOB;
    private String address;
    private String image;
    private boolean verification;
    private int type;
    private int status;
    private Date created_at;
    private Date updated_at;

    public User() {
    }

    public User(int id, String userName, String email, String phone, String password, String confirmPassword, String firstName, String lastName, int gender, Date DOB, String address, String image, boolean verification, int type, int status, Date created_at, Date updated_at) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.DOB = DOB;
        this.address = address;
        this.image = image;
        this.verification = verification;
        this.type = type;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public User(String userName, String email, String phone, String password, String confirmPassword, String firstName, String lastName, int gender, Date DOB, String address, String image, boolean verification, int type, int status, Date created_at, Date updated_at) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.DOB = DOB;
        this.address = address;
        this.image = image;
        this.verification = verification;
        this.type = type;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
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
    public String getEntityKey() {
        return "";
    }
}
