package edu.school21.sockets.models;

import java.util.Date;

public class Message {
    private long identifier;
    private User author;
    private long roomIdentifier;
    private String text;
    private Date dateTime;

    public long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User auth) {
        this.author = auth;
    }

    public long getRoomIdentifier() {
        return roomIdentifier;
    }

    public void setRoomIdentifier(long roomIdentifier) {
        this.roomIdentifier = roomIdentifier;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return author.getUsername() +
                ": " + text;
    }
}
