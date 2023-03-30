package com.example.chatapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewSingleFriendActivity extends AppCompatActivity {
    Toolbar toolbar_singleFriend;
    CircleImageView civAvatarSingleFriend, civSingleFriendOnline, civSingleFriendOffline;
    TextView tvDescribeSingleFriend, tvUserNameSingleFriend, tvEmailSingleFriend, tvGenderSingleFriend;
    Button btnSendMessage, btnUnfriend, btnBackInViewSingleFriend;

    String friendID, profilePicURL, userName, email, gender, describe, statusActivity, currentStatus = "friend";

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mFirebaseDatabase;
    FirebaseStorage storage;
    StorageReference mStorageReference;
    DatabaseReference mFriendReference, mDataReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_friend);
        setControl();
        callInformationFriend(friendID);
        SetEvent();

    }


    private void setControl() {
        toolbar_singleFriend = findViewById(R.id.toolbar_singleFriend);
        civAvatarSingleFriend = findViewById(R.id.civAvatarSingleFriend);
        civSingleFriendOnline = findViewById(R.id.civSingleFriendOnline);
        civSingleFriendOffline = findViewById(R.id.civSingleFriendOffline);
        tvDescribeSingleFriend = findViewById(R.id.tvDescribeSingleFriend);
        tvUserNameSingleFriend = findViewById(R.id.tvUserNameSingleFriend);
        tvEmailSingleFriend = findViewById(R.id.tvEmailSingleFriend);
        tvGenderSingleFriend = findViewById(R.id.tvGenderSingleFriend);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnUnfriend = findViewById(R.id.btnUnfriend);
        btnBackInViewSingleFriend = findViewById(R.id.btnBackInViewSingleFriend);


        friendID = getIntent().getStringExtra("userID");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFriendReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        mDataReference = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    private void SetEvent() {

        btnBackInViewSingleFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSendMessage(friendID);
            }
        });

        btnUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unFriend(friendID);
            }
        });
    }

    private void btnSendMessage(String friendID) {
        Intent intent = new Intent(ViewSingleFriendActivity.this, ChatActivity.class);
        intent.putExtra("userID", friendID).toString();
        startActivity(intent);
    }

    private void unFriend(String friendID) {
        mFriendReference.child(mUser.getUid()).child(friendID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFriendReference.child(friendID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                openConfirmUnfriendDialog(Gravity.CENTER);
                            }
                        }
                    });
                }
            }
        });
    }

    private void openConfirmUnfriendDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_unfriend_dialog);
        Window window = (Window) dialog.getWindow();
        if (window == null) {
            return;
        } else {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            window.setAttributes(windowAttributes);

            if (Gravity.CENTER == gravity) {
                dialog.setCancelable(true);
            } else {
                dialog.setCancelable(false);
            }
            Button btnConfirm = dialog.findViewById(R.id.btnConfirmUnfriend);
            Button btnCancelConfirm = dialog.findViewById(R.id.btnCancelConfirmUnfriend);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Toast.makeText(ViewSingleFriendActivity.this, "Hủy kết bạn thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ViewSingleFriendActivity.this, ViewItemContactActivity.class);
                    intent.putExtra("userID", friendID);
                    startActivity(intent);
                }
            });
            btnCancelConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    private void callInformationFriend(String friendID) {

        mFriendReference.child(mUser.getUid()).child(friendID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userName = snapshot.child("userName").getValue().toString();
                    email = snapshot.child("email").getValue().toString();

                    //Thông tin ảnh đại diện
                    if (snapshot.hasChild("profilePic")) {
                        profilePicURL = snapshot.child("profilePic").getValue().toString();
                    } else {
                        mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                profilePicURL = uri.toString();
                            }
                        });
                    }
                    //Thông tin mô tả
                    if (snapshot.hasChild("describe")) {
                        describe = snapshot.child("describe").getValue().toString();
                    } else {
                        describe = "";
                    }
                    //Thông tin giới tính
                    if (snapshot.hasChild("gender")) {
                        gender = snapshot.child("gender").getValue().toString();
                    } else {
                        gender = "";
                    }

                    Picasso.get().load(profilePicURL).placeholder(R.drawable.default_avatar).into(civAvatarSingleFriend);
                    tvDescribeSingleFriend.setText(describe);
                    tvUserNameSingleFriend.setText(userName);
                    tvEmailSingleFriend.setText(email);
                    tvGenderSingleFriend.setText(gender);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDataReference.child(friendID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    statusActivity = snapshot.child("statusActivity").getValue().toString();
                }
                if (statusActivity.trim().equals("Online")) {
                    civSingleFriendOnline.setVisibility(View.VISIBLE);
                    civSingleFriendOffline.setVisibility(View.GONE);
                } else {
                    civSingleFriendOnline.setVisibility(View.GONE);
                    civSingleFriendOffline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void statusActivity(String statusActivity) {
        mDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("statusActivity", statusActivity);
        mDataReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusActivity("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        statusActivity("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statusActivity("Offline");
    }
}