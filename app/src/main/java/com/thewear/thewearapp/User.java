package com.thewear.thewearapp;

public class User {
    String username;
    String email;
    //String website;

    public User(){
        // Default constructor required
    }

    public User(String username,String email){
        this.username=username;
        this.email=email;
    }

    public void setUserName(String username){this.username = username;}
    public void setEmail(String surname){this.email = email;}
   // public void setWebsite(String website){this.website = website;}

    public String getUserName(){ return username; }
    public String getEmail(){ return email; }
}

