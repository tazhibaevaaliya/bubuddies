package com.green.bubuddies;

// Profile object. Holds the user's profile information.
public class Profile {
    public String major, aboutMe, graduationYear, uid, picture;

    public Profile(String uid) {
        this.major = "";
        this.aboutMe = "";
        this.graduationYear = "";
        this.uid = uid;
        this.picture = "https://firebasestorage.googleapis.com/v0/b/bubuddies-3272b.appspot.com/o/default.png?alt=media&token=68367ffc-1526-4d42-9ea2-63cd55b5ef52";
    }
}