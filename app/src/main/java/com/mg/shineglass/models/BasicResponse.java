package com.mg.shineglass.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BasicResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("otp")
    private String otp;

    @SerializedName("Address")
    private List<Address> addressList;

    @SerializedName("token")
    String token;

    @SerializedName("OrderNo")
    String OrderNo;



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

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public String getToken() {
        return token;
    }
}
