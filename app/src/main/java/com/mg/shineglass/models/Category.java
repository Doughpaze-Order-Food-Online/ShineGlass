package com.mg.shineglass.models;

import java.util.List;

public class Category {
    String category;
    List<String> subcategoryList;

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
