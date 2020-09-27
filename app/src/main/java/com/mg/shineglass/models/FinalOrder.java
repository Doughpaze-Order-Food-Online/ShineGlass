package com.mg.shineglass.models;

public class FinalOrder {
    String QuotationNo,amount;
    Address address;

    public void setQuotationNo(String quotationNo) {
        QuotationNo = quotationNo;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }



    public String getQuotationNo() {
        return QuotationNo;
    }
}
