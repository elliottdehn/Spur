package com.gregory.spur.domain;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class Review {
    private String author;
    private String target;
    private boolean like;
    private String description;
    private Timestamp written;

    public Review(){

    }

    public Review(String author, String target, boolean like, String description, Timestamp written) {
        this.author = author;
        this.target = target;
        this.like = like;
        this.description = description;
        this.written = written;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getWritten() {
        return written;
    }

    public void setWritten(Timestamp written) {
        this.written = written;
    }
}
