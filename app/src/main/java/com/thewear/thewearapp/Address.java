package com.thewear.thewearapp;

public class Address {
    private String address, postal, country,user;

    public Address(){
        //empty constructor do not delete
    }

    public Address(String address, String postal, String country, String user) {
        this.address = address;
        this.postal = postal;
        this.country = country;
        this.user = user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
