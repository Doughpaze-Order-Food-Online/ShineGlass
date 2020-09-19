package com.mg.shineglass.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class MyQuotation {

    class Files{
        String filetype,url;

        public String getUrl() {
            return url;
        }

        public String getFiletype() {
            return filetype;
        }
    }

    @SerializedName("customer")
    User customer;

    @SerializedName("QuotationNo")
    String QuotationNo;

    @SerializedName("date")
    Date date;

    @SerializedName("files")
    List<Files> files;

    @SerializedName("status")
    String status;

    @SerializedName(" valuation")
    Boolean  valuation;

    @SerializedName("url")
    String url;

    @SerializedName("total")
    Double total;

    @SerializedName("quotation")
    List<Quotation> quotation;


    public String getUrl() {
        return url;
    }

    public Date getDate() {
        return date;
    }

    public List<Files> getFiles() {
        return files;
    }

    public String getQuotationNo() {
        return QuotationNo;
    }

    public User getCustomer() {
        return customer;
    }

    public Boolean getValuation() {
        return valuation;
    }

    public String getStatus() {
        return status;
    }

    public Double getTotal() {
        return total;
    }

    public List<Quotation> getQuotation() {
        return quotation;
    }


}
