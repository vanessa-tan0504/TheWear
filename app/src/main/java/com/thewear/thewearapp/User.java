package com.thewear.thewearapp;

public class User {
    String username;
    String email;
    String exp_gender;
    String uid;

    public User(){
        // Default constructor required
    }

    public User(String username,String email,String exp_gender,String uid){
        this.username=username;
        this.email=email;
        this.exp_gender=exp_gender;
        this.uid=uid;
    }

    public void setUserName(String username){this.username = username;}
    public void setEmail(String surname){this.email = email;}
    public void setExpGender(String exp_gender){this.exp_gender=exp_gender;}
    public void setUid(String uid){this.uid=uid;}

    public String getUserName(){ return username; }
    public String getEmail(){ return email; }
    public String getExpectedGender(){ return exp_gender; }
    public String getUid(){ return uid; }
}

