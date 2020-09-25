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

    @SerializedName("invoice")
    String invoice;

    @SerializedName("invoice_generated")
    Boolean  invoice_generated;



    public String getStatus() {
        return status;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public Boolean getInvoice_generated() {
        return invoice_generated;
    }

    public String getInvoice() {
        return invoice;
    }

}
