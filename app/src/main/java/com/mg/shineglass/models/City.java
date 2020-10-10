package com.mg.shineglass.models;

import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("name")
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
