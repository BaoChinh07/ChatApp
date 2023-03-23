package com.example.chatapp.Models;

public class Message {
    private String sms, status, userID, datetime;

    public Message() {
    }

    public Message(String sms, String status, String userID, String datetime) {
        this.sms = sms;
        this.status = status;
        this.userID = userID;
        this.datetime = datetime;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
