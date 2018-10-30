package com.gregory.spur.domain;

public class User {

    private double age;
    private String bio;
    private String city;
    private String fName;
    private String lName;
    private String gender;
    private double rating;
    private String username;
    private String authId;

    public User(){

    }

    public User(double age, String bio, String city, String fName, String lName, String gender, double rating, String username){
        this.age = age;
        this.bio = bio;
        this.city = city;
        this.fName = fName;
        this.lName = lName;
        this.gender = gender;
        this.rating = rating;
        this.username = username;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getAge() {
        return age;
    }

    public String getBio() {
        return bio;
    }

    public String getCity() {
        return city;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getGender() {
        return gender;
    }

    public double getRating() {
        return rating;
    }

    public String getUsername() {
        return username;
    }

}
