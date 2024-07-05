package edu.school21.sockets.models;

public class User {
    private long identifier;
    private String username;
    private String pass;

    public User() {}

    public User(long id, String uName, String password) {
        identifier = id;
        username = uName;
        pass = password;
    }

    public long getIdentifier() {
        return identifier;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() { return pass; }

    public void setIdendifier(long id) {
        identifier = id;
    }

    public void setUsername(String str) {
        username = str;
    }

    public void setPassword(String password) {pass = password;}

    @Override
    public String toString() {
        return "User{" +
                "identifier=" + identifier +
                ", username='" + username + '\'' +
                ", password='" + pass + '\'' +
                '}';
    }
}