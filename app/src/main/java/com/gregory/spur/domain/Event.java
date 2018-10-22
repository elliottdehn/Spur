package com.gregory.spur.domain;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Event {

    private String creator;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private GeoPoint location;
    private boolean romantic;

    public Event(String creator, String title, String description, Date startTime, Date endTime, GeoPoint location, boolean romantic) {
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.romantic = romantic;
    }


    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public boolean isRomantic() {
        return romantic;
    }

    public void setRomantic(boolean romantic) {
        this.romantic = romantic;
    }


}
