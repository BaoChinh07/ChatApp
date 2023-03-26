package com.example.chatapp.Models;

public class Requests {
    String userName, profilePic, userID;

    public Requests() {
    }

    public Requests(String userName, String profilePic, String userID) {
        this.userName = userName;
        this.profilePic = profilePic;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
