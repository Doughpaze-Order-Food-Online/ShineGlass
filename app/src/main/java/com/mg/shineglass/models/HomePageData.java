package com.mg.shineglass.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomePageData {

    @SerializedName("banner")
    List<Banners> bannersList;

    @SerializedName("rates")
    List<Rates> RatesList;

    public List<Banners> getBannersList() {
        return bannersList;
    }

    public List<Rates> getRatesList() {
        return RatesList;
    }

    public void setBannersList(List<Banners> bannersList) {
        this.bannersList = bannersList;
    }

    public void setRatesList(List<Rates> ratesList) {
        RatesList = ratesList;
    }
}
