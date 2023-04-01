package com.example.chatapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Firebase.FcmNotificationsSender;
import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class VoiceCallOutGoingActivity extends AppCompatActivity {
    CircleImageView cirAvatarVoiceCalOutGoing;
    TextView tvNameVoiceCallOutGoing;
    TextView tvEmailVoiceCallOutGoing;
    FloatingActionButton fabVoiceCallEnd;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserReference, mVoiceCallReference;

    String senderID, receiverID, receiverToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call_out_going);

        setControl();
        setEvent();
    }


    private void setControl() {
        cirAvatarVoiceCalOutGoing = findViewById(R.id.cirAvatarVoiceCalOutGoing);
        tvNameVoiceCallOutGoing = findViewById(R.id.tvNameVoiceCallOutGoing);
        tvEmailVoiceCallOutGoing = findViewById(R.id.tvEmailVoiceCallOutGoing);
        fabVoiceCallEnd = findViewById(R.id.fabVoiceCallEnd);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mVoiceCallReference = FirebaseDatabase.getInstance().getReference().child("VoiceCallComing");

        receiverID = getIntent().getStringExtra("receiverID");
        senderID = mUser.getUid();
    }

    private void setEvent() {
        loadSenderProfile();
        sendVoiceCallInvitation();
        checkResponse();

        fabVoiceCallEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceCallEnd();
            }
        });
    }

    private void voiceCallEnd() {
        HashMap hashMap = new HashMap();
        hashMap.put("key", receiverID);
        hashMap.put("response", "no");
        mVoiceCallReference.child(senderID).child(receiverID).child("response").updateChildren(hashMap);
        Toast.makeText(this, "Kết thúc cuộc gọi", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(VoiceCallOutGoingActivity.this, ChatActivity.class);
        intent.putExtra("userID", receiverID);
        startActivity(intent);
        finish();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVoiceCallReference.child(senderID).child(receiverID).removeValue();
            }
        }, 1000);
    }


    private void loadSenderProfile() {
        mUserReference.child(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users users = snapshot.getValue(Users.class);
                    Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.default_avatar).into(cirAvatarVoiceCalOutGoing);
                    tvNameVoiceCallOutGoing.setText(users.getUserName());
                    tvEmailVoiceCallOutGoing.setText(users.getEmail());
                } else {
                    Toast.makeText(VoiceCallOutGoingActivity.this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendVoiceCallInvitation() {
        mUserReference.child(receiverID).child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    receiverToken = snapshot.getValue().toString().trim();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        HashMap hashMap = new HashMap<>();
        hashMap.put("key", senderID + receiverID);
        hashMap.put("response", "wait_confirm");
        mVoiceCallReference.child(senderID).child(receiverID).child("response").updateChildren(hashMap);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(receiverToken, "VoiceCall", senderID, getApplicationContext(), VoiceCallOutGoingActivity.this);
                fcmNotificationsSender.sendNotifications();
            }
        }, 1000);
    }

    private void checkResponse() {
        mVoiceCallReference.child(senderID).child(receiverID).child("response").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.exists()){
                        String key = snapshot.child("key").getValue().toString().trim();
                        String response = snapshot.child("response").getValue().toString().trim();

                        if (response.equals("yes")) {
                            joinMeeting(key);
                        } else if (response.equals("no")) {
                            Intent intent = new Intent(VoiceCallOutGoingActivity.this, ChatActivity.class);
                            intent.putExtra("userID", receiverID);
                            startActivity(intent);
                            finish();
                        } else {
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void joinMeeting(String key) {
        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(key)
                    .setFeatureFlag("welcomepage.enabled", false)
                    .setFeatureFlag("prejoinpage.enabled", false)
                    .setVideoMuted(true)
                    .build();
            JitsiMeetActivity.launch(VoiceCallOutGoingActivity.this, options);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}