package com.mg.shineglass.models;


import com.google.gson.annotations.SerializedName;

public class BasicResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("otp")
    private String otp;



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
