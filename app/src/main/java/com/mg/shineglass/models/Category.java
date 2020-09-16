package com.mg.shineglass.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {
    String name;

    @SerializedName("subcategory")
    List<String> subcategory;

    int resourceID;

    Category(){

    }

    public Category(String category, int resourceID)
    {
        this.name=category;
        this.resourceID=resourceID;
    }


    public int getResourceID() {
        return resourceID;
    }

    public String getCategory() {
        return name;
    }

    public void setCategory(String category) {
        this.name = category;
    }

    public List<String> getSubcategoryList() {
        return subcategory;
    }

    public void setSubcategoryList(List<String> subcategoryList) {
        this.subcategory = subcategoryList;
    }
}
