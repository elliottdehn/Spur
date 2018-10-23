package com.gregory.spur.domain;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class Review {
    private DocumentReference author;
    private DocumentReference target;
    private boolean like;
    private String description;
    private Date written;

    public Review(DocumentReference author, boolean like, DocumentReference target, String description, Date written) {
        this.author = author;
        this.like = like;
        this.target = target;
        this.description = description;
        this.written = written;
    }

    public DocumentReference getAuthor() {
        return author;
    }

    public void setAuthor(DocumentReference author) {
        this.author = author;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public DocumentReference getTarget() {
        return target;
    }

    public void setTarget(DocumentReference target) {
        this.target = target;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getWritten() {
        return written;
    }

    public void setWritten(Date written) {
        this.written = written;
    }
}
