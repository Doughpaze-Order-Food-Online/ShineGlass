package com.mg.shineglass.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MyOrders {

    @SerializedName("OrderNo")
    String OrderNo;

    @SerializedName("order_date")
    Date order_date;

    @SerializedName("status")
    String status;


    public String getStatus() {
        return status;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public String getOrderNo() {
        return OrderNo;
    }
}
