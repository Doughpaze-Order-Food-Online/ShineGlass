package com.mg.shineglass.models;

public class User {
    String username,email,password,mobile,userfield,userType,old_password;
    String idToken,accessToken,userID;

    public User(String username, String email, String password, String mobile){
        this.username=username;
        this.email=email;
        this.password=password;
        this.mobile=mobile;
    }

    public User(){

    }

    public User(String userfield, String password){
        this.userfield=userfield;
        this.password=password;

    }

    public User(String mobile){
        this.mobile=mobile;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserType() {
        return userType;
    }
}
