package com.mg.shineglass.models;

public class OrderDeliver {
    String OrderNo,_id;
    int otp;


    public String getOrderNo() {
        return OrderNo;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }



    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }


}
