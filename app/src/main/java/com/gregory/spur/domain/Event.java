package com.gregory.spur.domain;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Event {

    public static final int NO_MIN = 0;
    public static final int NO_MAX = Integer.MAX_VALUE;

    private DocumentReference creator;
    private String creatorUserame;
    private String name;
    private String desc;
    private Timestamp start;
    private Timestamp end;
    private GeoPoint loc;
    private boolean romantic;
    private double min;
    private double max;
    private String vis;

    public Event() {
    }

    //use this constructor for connectivity demonstration
    public Event(String title, String desc, Timestamp start, Timestamp end, GeoPoint loc) {
        this.name = title;
        this.desc = desc;
        this.start = start;
        this.end = end;
        this.loc = loc;
        this.romantic = false;
        this.min = NO_MIN;
        this.max = NO_MAX;
        this.vis = "ALL";
    }

    //use this constructor once users are set up
    public Event(DocumentReference creator, String title, String desc, Timestamp start, Timestamp end, GeoPoint loc){
        this.creator = creator;
        this.name = title;
        this.desc = desc;
        this.start = start;
        this.end = end;
        this.loc = loc;
        this.romantic = false;
        this.min = NO_MIN;
        this.max = NO_MAX;
    }

    public Event(DocumentReference creator, String title, String desc, Timestamp start, Timestamp end, GeoPoint loc, boolean romantic, double min, double max, String vis, List<DocumentReference> attendees) {
        this.creator = creator;
        this.name = title;
        this.desc = desc;
        this.start = start;
        this.end = end;
        this.loc = loc;
        this.romantic = romantic;
        this.min = min;
        this.max = max;
        this.vis = vis;
    }

    public DocumentReference getCreator() {
        return creator;
    }

    public void setCreator(DocumentReference creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
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

    public String getCreatorUserame() {
        return creatorUserame;
    }

    public void setCreatorUserame(String creatorUserame) {
        this.creatorUserame = creatorUserame;
    }
}