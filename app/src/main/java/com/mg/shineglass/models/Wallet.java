package com.mg.shineglass.models;

import android.view.SurfaceControl;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Wallet {

   public static class Transaction{
        String type;
        Double amount,total;

       public Double getTotal() {
           return total;
       }

       public String getType() {
           return type;
       }

       public Double getAmount() {
           return amount;
       }

       public void setAmount(Double amount) {
           this.amount = amount;
       }

       public void setTotal(Double total) {
           this.total = total;
       }

       public void setType(String type) {
           this.type = type;
       }
   }

    @SerializedName("wallet")
    Double wallet;

    @SerializedName("wallet_transaction")
    List<Transaction> wallet_transaction;


    public Double getWallet() {
        return wallet;
    }

    public List<Transaction> getWallet_transaction() {
        return wallet_transaction;
    }
}
