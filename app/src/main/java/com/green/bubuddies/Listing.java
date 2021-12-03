package com.green.bubuddies;

import android.net.Uri;

//inner class for a listing item
public class Listing {

    private String title;
    private int price;
    private String owner;
    private String picture;
    private String default_picture;

    public Listing(String title, int price, String owner, String imageURI, String default_picture) {
        this.title = title;
        this.price = price;
        this.owner = owner;
        this.picture = imageURI;
        this.default_picture = default_picture;
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


}
