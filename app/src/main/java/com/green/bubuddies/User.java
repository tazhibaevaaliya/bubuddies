package com.green.bubuddies;

// User object. Is sent to the database when account is created. Holds the user's important authentication information.
public class User {
    public String username, email, uid;

    public User() {

    }

    public User(String username, String email, String uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
    }
}