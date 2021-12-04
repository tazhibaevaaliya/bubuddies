package com.green.bubuddies;

import android.content.Context;

public class ListingModel {
    private String title;
    private String price;
    private String image;
    private Context context;

    //constructor
    public ListingModel(String title,String price, String image, Context context){
        this.title = title;
        this.price = price;
        this.image = image;
        this.context = context;
    }

    //Getter and Setter
    public String getTitle(){
        return this.title;
    }

    public String getPrice(){
        return this.price;
    }

    public String getImage(){
        return this.image;
    }

    public Context getContext(){
        return this.context;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public void setImage(String image){
        this.image = image;
    }
}
