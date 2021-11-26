package com.green.bubuddies;

//inner class for a listing item
public class Listing {

    private String title;
    private int price;

    public Listing(String title, int price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return this.title;
    }

    public int getPrice() {
        return this.price;
    }
}
