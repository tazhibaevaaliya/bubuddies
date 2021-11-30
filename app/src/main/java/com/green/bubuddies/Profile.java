package com.green.bubuddies;

// Profile object. Holds the user's profile information.
public class Profile {
    public String major, aboutMe, name, graduationYear, uid;

    public Profile(String uid) {
        this.major = "";
        this.aboutMe = "";
        this.name = "";
        this.graduationYear = "";
        this.uid = uid;
    }
}