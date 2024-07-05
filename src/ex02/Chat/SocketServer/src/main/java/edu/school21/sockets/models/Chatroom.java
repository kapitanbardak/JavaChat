package edu.school21.sockets.models;

import java.util.Date;

public class Chatroom {
    private long identifier;
    private User owner;
    private String name;

    public Chatroom() {}

    public Chatroom(long id, String chName, User owner) {
        identifier = id;
        name = chName;
        this.owner = owner;
    }

    public long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
