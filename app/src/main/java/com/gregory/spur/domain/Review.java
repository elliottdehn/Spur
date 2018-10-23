package com.gregory.spur.domain;

import java.util.Date;

public class Review {
    private String author;
    private boolean like;
    private String target;
    private String description;
    private Date written;

    public Review(String author, boolean like, String target, String description, Date written) {
        this.author = author;
        this.like = like;
        this.target = target;
        this.description = description;
        this.written = written;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
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
