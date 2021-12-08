package com.green.bubuddies;

import android.net.Uri;

//inner class for a listing item
public class Listing {

    private String title;
    private Double price;
    private String owner;
    private String imageURI;
    private String description;


    public Listing(String title, Double price, String owner, String imageURI, String description) {
        this.title = title;
        this.price = price;
        this.owner = owner;
        this.imageURI = imageURI;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}