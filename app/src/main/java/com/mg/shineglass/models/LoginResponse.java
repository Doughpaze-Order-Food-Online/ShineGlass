package com.mg.shineglass.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("user")
    User user;

    @SerializedName("message")
    String message;

    @SerializedName("jwt")
    String jwt;

    @SerializedName("otp")
    String otp;

    @SerializedName("type")
    String type;


    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getType() {
        return type;
    }
}

