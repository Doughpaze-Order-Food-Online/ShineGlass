package com.mg.shineglass.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MyOrders {

    @SerializedName("OrderNo")
    String OrderNo;

    @SerializedName("order_date")
    Date order_date;

    @SerializedName("customer")
    User user;

    @SerializedName("status")
    String status;

    @SerializedName("invoice")
    String invoice;

    @SerializedName("invoice_generated")
    Boolean  invoice_generated;

    @SerializedName("address")
    Address address;

    @SerializedName("payment_mode")
    String payment_mode;

    @SerializedName("total")
    Double total;

    @SerializedName("delivery_status")
    Boolean delivery_status;


    public Boolean getDelivery_status() {
        return delivery_status;
    }

    public Double getTotal() {
        return total;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }
}
