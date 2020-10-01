package com.mg.shineglass.models;

import com.google.gson.annotations.SerializedName;

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
