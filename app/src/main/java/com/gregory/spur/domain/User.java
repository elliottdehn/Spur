package com.gregory.spur.domain;

public class User {

    private double age;
    private String bio;
    private String city;
    private String first;
    private String last;
    private String gender;
    private String username;
    private String authId;

    public User(){

    }

    public boolean isValid(){
        if (authId != null && bio != null && city != null
                && first != null && last != null && gender != null && username != null && age > 0.0){
            return true;
        } else {
            return false;
        }
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
}
