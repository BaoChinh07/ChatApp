package com.example.chatapp.Models;

public class Chat {
    private String userName, profilePic, lastMessage,friendID;
    private long timestamp;

    public Chat() {
    }

    public Chat(String userName, String profilePic, String lastMessage, String friendID, long timestamp) {
        this.userName = userName;
        this.profilePic = profilePic;
        this.lastMessage = lastMessage;
        this.friendID = friendID;
        this.timestamp = timestamp;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
