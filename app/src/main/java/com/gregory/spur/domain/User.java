package com.gregory.spur.domain;

import java.util.ArrayList;
import java.util.List;

public class User {

    private double age;
    private String bio;
    private String city;
    private String first;
    private String last;
    private String gender;
    private String username;
    private String authId;
    private List<String> attendingEvents = new ArrayList<>();

    public User(){

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

    public List<String> getAttendingEvents() {
        return attendingEvents;
    }

    public void setAttendingEvents(List<String> attendingEvents) {
        this.attendingEvents = attendingEvents;
    }

    public void addAttendingEvent(String eventId){
        this.attendingEvents.add(eventId);
    }
}
