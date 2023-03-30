package com.example.chatapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCallOutgoingActivity extends AppCompatActivity {
    CircleImageView cirAvatarVideoCalOutGoing;
    TextView tvNameVideoCallOutGoing, tvEmailVideoCallOutGoing;
    FloatingActionButton fabCallEnd;
    String receiveID, friendToken, senderID;

    DatabaseReference mUserReference, responseReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseUser mUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_outgoing);
        setControl();
        setEvent();
    }

    private void setControl() {
        cirAvatarVideoCalOutGoing = findViewById(R.id.cirAvatarVideoCalOutGoing);
        tvNameVideoCallOutGoing = findViewById(R.id.tvNameVideoCallOutGoing);
        tvEmailVideoCallOutGoing = findViewById(R.id.tvEmailVideoCallOutGoing);
        fabCallEnd = findViewById(R.id.fabCallEnd);

        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        receiveID = getIntent().getStringExtra("friendID");
        senderID = mUser.getUid();
    }

    private void setEvent() {
        loadProfileFriend();
    }
    private void loadProfileFriend() {
        mUserReference.child(receiveID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users users = snapshot.getValue(Users.class);
                    Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.default_avatar).into(cirAvatarVideoCalOutGoing);
                    tvNameVideoCallOutGoing.setText(users.getUserName());
                    tvEmailVideoCallOutGoing.setText(users.getEmail());
                } else {
                    Toast.makeText(VideoCallOutgoingActivity.this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}