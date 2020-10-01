package com.mg.shineglass.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {



    @SerializedName("name")
    String name;

    @SerializedName("subcategory")
    List<subcategory> subcategory;

    int resourceID;



    public Category(String name, int resourceID)
    {
        this.name=name;
        this.resourceID=resourceID;
    }


    public int getResourceID() {
        return resourceID;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<subcategory> getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(List<subcategory> subcategory) {
        this.subcategory = subcategory;
    }
}
