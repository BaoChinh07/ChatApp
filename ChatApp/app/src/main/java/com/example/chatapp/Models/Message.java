package com.example.chatapp.Models;

public class Message {
    private String message, senderID, receiverID, datetime, type;

    public Message() {
    }

    public Message(String message, String senderID, String receiverID, String datetime, String type) {
        this.message = message;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.datetime = datetime;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
