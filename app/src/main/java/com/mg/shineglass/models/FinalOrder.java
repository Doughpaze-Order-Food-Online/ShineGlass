package com.mg.shineglass.models;

public class FinalOrder {
    String QuotationNo,address,amount;

    public void setQuotationNo(String quotationNo) {
        QuotationNo = quotationNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public String getQuotationNo() {
        return QuotationNo;
    }
}
