package com.green.bubuddies;

import android.net.Uri;

//inner class for a listing item
public class Listing {

    private String title;
    private int price;
    private String owner;
    private String picture;
    private String description;


    public Listing(String title, int price, String owner, String imageURI, String description) {
        this.title = title;
        this.price = price;
        this.owner = owner;
        this.picture = imageURI;
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public int getPrice() {
        return this.price;
    }

    public String getOwner(){
        return this.owner;
    }

    public String getImageURI(){
        return this.picture;
    }

    public void setImageURI(String imageURI) {
        this.picture = imageURI;
    }


}
