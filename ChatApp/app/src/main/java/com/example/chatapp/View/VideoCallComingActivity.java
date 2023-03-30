package com.example.chatapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCallComingActivity extends AppCompatActivity {
    CircleImageView cirAvatarVideoCalComing;
    TextView tvNameVideoCalComing, tvEmailVideoCallComing;
    FloatingActionButton fabDeclineCall, fabAcceptVideoCall;
    DatabaseReference referenceCaller, mUserReference;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String senderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_coming);
        setControl();
        setEvent();
    }

    private void setControl() {
        cirAvatarVideoCalComing = findViewById(R.id.cirAvatarVideoCalComing);
        tvNameVideoCalComing = findViewById(R.id.tvNameVideoCalComing);
        tvEmailVideoCallComing = findViewById(R.id.tvEmailVideoCallComing);
        fabDeclineCall = findViewById(R.id.fabDeclineCall);
        fabAcceptVideoCall = findViewById(R.id.fabAcceptVideoCall);

        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");

        senderID = getIntent().getStringExtra("userID");
    }

    private void setEvent() {
        loadSenderProfile();
    }

    private void loadSenderProfile() {
        {
            mUserReference.child(senderID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Users users = snapshot.getValue(Users.class);
                        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.default_avatar).into(cirAvatarVideoCalComing);
                        tvNameVideoCalComing.setText(users.getUserName());
                        tvEmailVideoCallComing.setText(users.getEmail());
                    } else {
                        Toast.makeText(VideoCallComingActivity.this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}