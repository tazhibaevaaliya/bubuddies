package com.green.bubuddies;

import android.net.Uri;

//inner class for a listing item
public class Listing {

    private String title;
    private Double price;
    private String owner;
    private String picture;
    private String description;


    public Listing(String title, Double price, String owner, String imageURI, String description) {
        this.title = title;
        this.price = price;
        this.owner = owner;
        this.picture = imageURI;
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public Double getPrice() {
        return this.price;
    }

    public String getOwner(){
        return this.owner;
    }

    public String getImageURI(){
        return this.picture;
    }

    public String getDescription() {
        return this.description;
    }


}
