package com.thewear.thewearapp;

public class User {
    String username;
    String email;
    String exp_gender;

    public User(){
        // Default constructor required
    }

    public User(String username,String email,String exp_gender){
        this.username=username;
        this.email=email;
        this.exp_gender=exp_gender;
    }

    public void setUserName(String username){this.username = username;}
    public void setEmail(String surname){this.email = email;}
    public void setExpGenderl(String exp_gender){this.exp_gender=exp_gender;}

    public String getUserName(){ return username; }
    public String getEmail(){ return email; }
    public String getExpectedGender(){ return exp_gender; }
}

