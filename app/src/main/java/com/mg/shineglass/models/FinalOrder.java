package com.mg.shineglass.models;

public class FinalOrder {
    String QuotationNo;
    Address address;
    Double total,wallet,Amount;
    public void setQuotationNo(String quotationNo) {
        QuotationNo = quotationNo;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    public Double getWallet() {
        return wallet;
    }

    public Double getTotal() {
        return total;
    }

    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }

    public void setWallet(Double wallet) {
        this.wallet = wallet;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getQuotationNo() {
        return QuotationNo;
    }
}
