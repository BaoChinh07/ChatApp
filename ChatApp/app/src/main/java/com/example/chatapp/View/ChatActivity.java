package com.example.chatapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Models.Message;
import com.example.chatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    FirebaseRecyclerOptions<Message> optionsMessage;
    FirebaseRecyclerAdapter<Message, MessageAdapter> messageAdapter;

    Toolbar chat_toolbar;
    RecyclerView rvMessage;
    EditText edtInputMessage;
    ImageView imageViewSendImage, imageViewSendMessage;
    CircleImageView civAvatarUserChat, civOnline, civOffline;
    TextView tvUserNameToolChat, tvUserOnl_OffChat;
    String userID, avatarURL, avatarBox, userName, dateTime, statusActivity;
    String avatarUserListChat, userNameListChat, lastMessage;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserReference, mFriendsReference, mSmsReference, mChatReference;
    StorageReference mStorageReference;
    FirebaseStorage storage;
    Date currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setControl();
        setEvent();

    }

    private void setControl() {
        chat_toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        edtInputMessage = (EditText) findViewById(R.id.edtInputMessage);
        imageViewSendImage = (ImageView) findViewById(R.id.imageViewSendImage);
        imageViewSendMessage = (ImageView) findViewById(R.id.imageViewSendMessage);
        rvMessage = (RecyclerView) findViewById(R.id.rvMessage);
        rvMessage.setLayoutManager(new LinearLayoutManager(this));
        tvUserNameToolChat = (TextView) findViewById(R.id.tvUserNameToolChat);
        tvUserOnl_OffChat = (TextView) findViewById(R.id.tvUserOnl_OffChat);
        civAvatarUserChat = (CircleImageView) findViewById(R.id.civAvatarUserChat);
        civOnline = (CircleImageView) findViewById(R.id.civOnline);
        civOffline = (CircleImageView) findViewById(R.id.civOffline);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mSmsReference = FirebaseDatabase.getInstance().getReference().child("Messages");
        mFriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        mChatReference = FirebaseDatabase.getInstance().getReference().child("Chats");
        storage = FirebaseStorage.getInstance();
        mStorageReference = storage.getReference().child("profilePic/default_avatar.png");
        userID = getIntent().getStringExtra("userID");
    }

    private void setEvent() {
        loadInformationUserChat(userID);
        loadAvatarBox();
        loadBoxChat(userID);
        loadSMS();

        imageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendSMS();
                createChatBox();
            }
        });
    }

    private void loadBoxChat(String userID) {
        mUserReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("profilePic")) {
                        avatarUserListChat = snapshot.child("profilePic").getValue().toString();
                    } else {
                        mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                avatarURL = uri.toString();
                            }
                        });
                    }
                    if (snapshot.hasChild("userName")) {
                        userNameListChat = snapshot.child("userName").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void createChatBox() {
        mSmsReference.child(mUser.getUid()).child(userID)
                .orderByChild("datetime")
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        lastMessage = snapshot1.child("sms").getValue().toString();
                    }
                }
                HashMap hashMap = new HashMap();
                hashMap.put("avatarUserListChat", avatarUserListChat);
                hashMap.put("userNameListChat",userNameListChat);
                hashMap.put("lastMessage", lastMessage);
                mChatReference.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            mChatReference.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    Toast.makeText(ChatActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadAvatarBox() {
        mUserReference.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("profilePic")) {
                        avatarBox = snapshot.child("profilePic").getValue().toString();
                    } else {
                        mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                avatarBox = uri.toString();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSMS() {
        optionsMessage = new FirebaseRecyclerOptions.Builder<Message>().setQuery(mSmsReference.child(mUser.getUid()).child(userID), Message.class).build();
        messageAdapter = new FirebaseRecyclerAdapter<Message, MessageAdapter>(optionsMessage) {
            @Override
            protected void onBindViewHolder(@NonNull MessageAdapter holder, int position, @NonNull Message model) {
                if (model.getUserID().equals(mUser.getUid())) {
                    holder.tvSmsUserOne.setVisibility(View.GONE);
                    holder.civAvatarUserOne.setVisibility(View.GONE);
                    holder.tvTimeMessageUserOne.setVisibility(View.GONE);
                    holder.tvSmsUserTwo.setVisibility(View.VISIBLE);
                    holder.civAvatarUserTwo.setVisibility(View.VISIBLE);
                    holder.tvTimeMessageUserTwo.setVisibility(View.VISIBLE);

                    holder.tvSmsUserTwo.setText(model.getSms());
                    holder.tvTimeMessageUserTwo.setText(model.getDatetime());
                    Picasso.get().load(avatarBox).into(holder.civAvatarUserTwo);

                } else {
                    holder.tvSmsUserOne.setVisibility(View.VISIBLE);
                    holder.civAvatarUserOne.setVisibility(View.VISIBLE);
                    holder.tvTimeMessageUserOne.setVisibility(View.VISIBLE);
                    holder.tvSmsUserTwo.setVisibility(View.GONE);
                    holder.civAvatarUserTwo.setVisibility(View.GONE);
                    holder.tvTimeMessageUserTwo.setVisibility(View.GONE);

                    holder.tvSmsUserOne.setText(model.getSms());
                    holder.tvTimeMessageUserOne.setText(model.getDatetime());
                    Picasso.get().load(avatarURL).into(holder.civAvatarUserOne);

                }
            }

            @NonNull
            @Override
            public MessageAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_single_sms, parent, false);
                return new MessageAdapter(view);
            }
        };
        messageAdapter.startListening();
        rvMessage.setAdapter(messageAdapter);
    }

    private void SendSMS() {
        String sms = edtInputMessage.getText().toString();
        currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm a");
        dateTime = simpleDateFormat.format(currentTime);

        if (sms.isEmpty()) {
            Toast.makeText(this, "Tin nhắn không được để trống", Toast.LENGTH_SHORT).show();
        } else {
            HashMap hashMap = new HashMap();
            hashMap.put("sms", sms);
            hashMap.put("status", "Đã gửi");
            hashMap.put("userID", mUser.getUid());
            hashMap.put("datetime", dateTime);
            mSmsReference.child(userID).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        mSmsReference.child(mUser.getUid()).child(userID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    edtInputMessage.setText(null);
                                    Toast.makeText(ChatActivity.this, "Đã gửi tin nhắn", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

        }
    }

    private void loadInformationUserChat(String userID) {
        mUserReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("profilePic")) {
                        avatarURL = snapshot.child("profilePic").getValue().toString();
                    } else {
                        mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                avatarURL = uri.toString();
                            }
                        });
                    }
                    userName = snapshot.child("userName").getValue().toString().trim();
                    statusActivity = snapshot.child("statusActivity").getValue().toString();

                    Picasso.get().load(avatarURL).into(civAvatarUserChat);
                    tvUserNameToolChat.setText(userName);
                    tvUserOnl_OffChat.setText(statusActivity);

                    if (statusActivity.trim().equals("Online")) {
                        civOnline.setVisibility(View.VISIBLE);
                        civOffline.setVisibility(View.GONE);
                    } else {
                        civOnline.setVisibility(View.GONE);
                        civOffline.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void statusActivity(String statusActivity) {
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("statusActivity", statusActivity);
        mUserReference.updateChildren(hashMap);
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
    protected void onStop() {
        super.onStop();
        statusActivity("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statusActivity("Offline");
    }
}