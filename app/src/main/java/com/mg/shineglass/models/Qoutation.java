package com.mg.shineglass.models;

public class Qoutation {
    String width,height;
    int quantity;

    public Qoutation()
    {

    }

    public int getQuantity() {
        return quantity;
    }

    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
