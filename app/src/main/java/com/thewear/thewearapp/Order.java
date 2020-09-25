package com.thewear.thewearapp;

public class Order {
    private String title, color, size,user,orderID,itemURL;
    private double totalprice;
    private int qty;
    private boolean isPaid;

    public Order(){
        //empty constructor do not delete
    }

    public Order(String orderID, String title, String size, String color, int qty, double totalprice, String user,boolean isPaid,String itemURL) {
        this.title = title;
        this.color = color;
        this.size = size;
        this.user = user; //user ID
        this.orderID = orderID; //order timestamp
        this.totalprice = totalprice;
        this.qty = qty;
        this.isPaid = isPaid;
        this.itemURL=itemURL;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public boolean getPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getItemURL(){ return itemURL;}

    public void setItemURL(String itemURL){this.itemURL=itemURL;}
}
