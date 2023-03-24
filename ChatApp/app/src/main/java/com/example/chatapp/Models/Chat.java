package com.example.chatapp.Models;

public class Chat {
    String userName, profilePic, lastMessage,friendID;

    public Chat() {
    }

    public Chat(String userName, String profilePic, String lastMessage, String friendID) {
        this.userName = userName;
        this.profilePic = profilePic;
        this.lastMessage = lastMessage;
        this.friendID = friendID;

    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
