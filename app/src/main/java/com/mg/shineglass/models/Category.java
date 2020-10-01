package com.mg.shineglass.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {

    public class subcategory{

       @SerializedName("name")
       String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @SerializedName("name")
    String name;

    @SerializedName("subcategory")
    List<subcategory> subcategory;

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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category.subcategory> getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(List<Category.subcategory> subcategory) {
        this.subcategory = subcategory;
    }
}
