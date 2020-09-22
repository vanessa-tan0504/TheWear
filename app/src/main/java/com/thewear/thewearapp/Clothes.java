package com.thewear.thewearapp;

public class Clothes {
    private String coverURL;
    private String title;
    private double price;
    private String id;

    public Clothes(){} //empty constructor do not delete

    public Clothes(String coverURL, String title, double price, String id){
        this.coverURL=coverURL;
        this.title = title;
        this.price = price;
        this.id = id;
    }

    public String getCoverURL(){
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void getId(String id) {
        this.id = id;
    }
}
