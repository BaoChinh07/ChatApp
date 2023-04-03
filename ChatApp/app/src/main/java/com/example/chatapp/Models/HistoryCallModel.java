package com.example.chatapp.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HistoryCallModel {
    String userID, userAvatar, userName, callTime, status, type, userID2;
    DatabaseReference mHistoryCallReference;

    public HistoryCallModel(String userID, String userAvatar, String userName, String status, String type, String userID2) {
        this.userID = userID;
        this.userAvatar = userAvatar;
        this.userName = userName;
        this.status = status;
        this.type = type;
        this.userID2 = userID2;
    }

    public void createHistoryCall() {
        mHistoryCallReference = FirebaseDatabase.getInstance().getReference().child("HistoryCall");
        Date mCurrentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
        String callTime = simpleDateFormat.format(mCurrentTime);

        HashMap hashMap = new HashMap();
        hashMap.put("userID",userID);
        hashMap.put("userAvatarURL", userAvatar);
        hashMap.put("userName", userName);
        hashMap.put("callTime",callTime);
        hashMap.put("typeCall",type);
        hashMap.put("statusCall",status);

        mHistoryCallReference.child(userID2).push().updateChildren(hashMap);
    }
}
