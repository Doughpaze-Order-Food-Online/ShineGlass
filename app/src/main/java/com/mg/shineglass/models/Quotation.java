package com.mg.shineglass.models;

public class Quotation {
    String width,height,quantity,thickness,scale;

public  Quotation(){

}

public Quotation(String thickness,String width,String height,String quantity,String scale)
{
    this.thickness=thickness;
    this.width=width;
    this.height=height;
    this.quantity=quantity;
    this.scale=scale;
}



    public String getQuantity() {
        return quantity;
    }

    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }
}
