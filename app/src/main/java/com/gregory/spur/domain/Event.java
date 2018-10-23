package com.gregory.spur.domain;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Event {

    public static final int NO_MIN = 0;
    public static final int NO_MAX = Integer.MAX_VALUE;

    private String creator;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private GeoPoint location;
    private boolean romantic;
    private int min;
    private int max;
    private String visibility;

    public Event(String creator, String title, String description, Date startTime, Date endTime,
                 GeoPoint location, boolean romantic, int min, int max, String visibility) {
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.romantic = romantic;
        this.min = min;
        this.max = max;
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
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
