package com.gregory.spur.domain;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.List;

public class Event {

    public static final int NO_MIN = 0;
    public static final int NO_MAX = Integer.MAX_VALUE;

    private DocumentReference creator;
    private String title;
    private String desc;
    private Date start;
    private Date end;
    private GeoPoint loc;
    private boolean romantic;
    private double min;
    private double max;
    private String vis;
    private List<DocumentReference> attendees;

    public Event() {
    }

    public Event(DocumentReference creator, String title, String desc, Date start, Date end,
                 GeoPoint loc, boolean romantic, double min, double max, String vis,
                 List<DocumentReference> attendees) {
        this.creator = creator;
        this.title = title;
        this.desc = desc;
        this.start = start;
        this.end = end;
        this.loc = loc;
        this.romantic = romantic;
        this.min = min;
        this.max = max;
        this.vis = vis;
        this.attendees = attendees;
    }

    public static int getNoMin() {
        return NO_MIN;
    }

    public static int getNoMax() {
        return NO_MAX;
    }

    public DocumentReference getCreator() {
        return creator;
    }

    public void setCreator(DocumentReference creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public GeoPoint getLoc() {
        return loc;
    }

    public void setLoc(GeoPoint loc) {
        this.loc = loc;
    }

    public boolean isRomantic() {
        return romantic;
    }

    public void setRomantic(boolean romantic) {
        this.romantic = romantic;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public String getVis() {
        return vis;
    }

    public void setVis(String vis) {
        this.vis = vis;
    }

    public List<DocumentReference> getAttendees() {
        return attendees;
    }

}