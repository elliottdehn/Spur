package com.gregory.spur.domain;

public class User {

    private int age;
    private String bio;
    private String city;
    private String fName;
    private String lName;
    private String gender;
    private double rating;
    private String username;

    public User(int age, String bio, String city, String fName, String lName, String gender, double rating, String username){
        this.age = age;
        this.bio = bio;
        this.city = city;
        this.fName = fName;
        this.lName = lName;
        this.gender = gender;
        this.rating = rating;
        this.username = username;
    }

    public int getAge() {
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
