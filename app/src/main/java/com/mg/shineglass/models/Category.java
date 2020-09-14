package com.mg.shineglass.models;

import java.util.List;

public class Category {
    String category;
    List<String> subcategoryList;
    int resourceID;

    Category(){

    }

    public Category(String category, int resourceID)
    {
        this.category=category;
        this.resourceID=resourceID;
    }


    public int getResourceID() {
        return resourceID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getSubcategoryList() {
        return subcategoryList;
    }

    public void setSubcategoryList(List<String> subcategoryList) {
        this.subcategoryList = subcategoryList;
    }
}
