package com.example.chatapp.Models;

import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;

public class HistoryCall {

    String userAvatarURL, userID, userName, statusCall, typeCall, callTime;

    public HistoryCall() {
    }

    public HistoryCall(String userAvatarURL, String userID, String userName, String statusCall, String typeCall, String callTime) {
        this.userAvatarURL = userAvatarURL;
        this.userID = userID;
        this.userName = userName;
        this.statusCall = statusCall;
        this.typeCall = typeCall;
        this.callTime = callTime;
    }

    public String getUserAvatarURL() {
        return userAvatarURL;
    }

    public void setUserAvatarURL(String userAvatarURL) {
        this.userAvatarURL = userAvatarURL;
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

    public String getStatusCall() {
        return statusCall;
    }

    public void setStatusCall(String statusCall) {
        this.statusCall = statusCall;
    }

    public String getTypeCall() {
        return typeCall;
    }

    public void setTypeCall(String typeCall) {
        this.typeCall = typeCall;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public void updateHistoryCall(DatabaseReference reference, HistoryCall value, String id) {
        reference.child(id).push().setValue(value);
    }
}
