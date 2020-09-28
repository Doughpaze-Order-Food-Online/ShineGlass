package com.mg.shineglass.models;

public class OrderDeliver {
    String orderId;
    int otp;

    public String getOrderId() {
        return orderId;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
