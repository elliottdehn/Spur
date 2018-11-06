package com.gregory.spur.domain;

public class Attendee {

    private double age;
    private String bio;
    private String city;
    private String first;
    private String last;
    private String gender;
    private String username;
    private String authId;
    private String userId;

    public Attendee(){

    }

    public Attendee(User user, String userId){
        this.age = user.getAge();
        this.bio = user.getBio();
        this.city = user.getCity();
        this.first = user.getFirst();
        this.last = user.getLast();
        this.gender = user.getGender();
        this.username = user.getUsername();
        this.authId = user.getAuthId();
        this.userId = userId;
    }

    public User toUser(){
        User user = new User();
        user.setAge(this.age);
        user.setBio(this.bio);
        user.setCity(this.city);
        user.setFirst(this.first);
        user.setLast(this.last);
        user.setGender(this.gender);
        user.setUsername(this.username);
        user.setAuthId(this.authId);
        return user;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
