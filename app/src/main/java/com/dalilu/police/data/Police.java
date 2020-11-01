package com.dalilu.police.data;

public class Police {
    String userName, userId, userFullName, phoneNumber, userEmail, userPhotoUrl;

    public Police() {
    }

    public Police(String userName, String userId, String userFullName, String phoneNumber, String userEmail, String userPhotoUrl) {
        this.userName = userName;
        this.userId = userId;
        this.userFullName = userFullName;
        this.phoneNumber = phoneNumber;
        this.userEmail = userEmail;
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }
}
