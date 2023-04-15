package com.example.chatapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.MainActivity;
import com.example.chatapp.Models.HistoryCallModel;
import com.example.chatapp.Models.Message;
import com.example.chatapp.Models.Users;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    String userID, avatarURL, userName, dateTime, statusActivity;
    String lastMessage,  myAvatar, myName, myUserEmail;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserReference, mFriendsReference, mSmsReference, mChatReference, mHistoryCallReference;
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
        chat_toolbar = findViewById(R.id.chat_toolbar);
        edtInputMessage = findViewById(R.id.edtInputMessage);
        imageViewSendImage = findViewById(R.id.imageViewSendImage);
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage);
        rvMessage = findViewById(R.id.rvMessage);
        rvMessage.setLayoutManager(new LinearLayoutManager(this));
        tvUserNameToolChat = findViewById(R.id.tvUserNameToolChat);
        tvUserOnl_OffChat = findViewById(R.id.tvUserOnl_OffChat);
        civAvatarUserChat = findViewById(R.id.civAvatarUserChat);
        civOnline = findViewById(R.id.civOnline);
        civOffline = findViewById(R.id.civOffline);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mSmsReference = FirebaseDatabase.getInstance().getReference().child("Messages");
        mFriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        mChatReference = FirebaseDatabase.getInstance().getReference().child("Chats");
        mHistoryCallReference = FirebaseDatabase.getInstance().getReference().child("HistoryCall");
        storage = FirebaseStorage.getInstance();
        mStorageReference = storage.getReference().child("profilePic/default_avatar.png");
        userID = getIntent().getStringExtra("userID");
        setSupportActionBar(chat_toolbar);
    }

    private void setEvent() {
        actionToolBar();
        loadInformationUserChat(userID);
        loadMyProfile();
        loadSMS();

        civAvatarUserChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnClickAvatar(userID);
            }
        });

        imageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendSMS();
                createChatBox();
            }
        });

    }

    private void actionToolBar() {
        setSupportActionBar(chat_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chat_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadInformationUserChat(String userID) {
        mUserReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users users = snapshot.getValue(Users.class);
                    avatarURL = users.getProfilePic();
                    userName = users.getUserName();
                    statusActivity = users.getStatusActivity();

                    Picasso.get().load(avatarURL).placeholder(R.drawable.default_avatar).into(civAvatarUserChat);
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

    private void loadMyProfile() {
        mUserReference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users users = snapshot.getValue(Users.class);
                    myName = users.getUserName();
                    myUserEmail = users.getEmail();
                    myAvatar = users.getProfilePic();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                    holder.tvTimeMessageUserTwo.setVisibility(View.VISIBLE);
                    holder.tvSmsUserTwo.setText(model.getSms());
                    holder.tvTimeMessageUserTwo.setText(model.getDatetime());

                } else {
                    holder.tvSmsUserOne.setVisibility(View.VISIBLE);
                    holder.civAvatarUserOne.setVisibility(View.VISIBLE);
                    holder.tvTimeMessageUserOne.setVisibility(View.VISIBLE);
                    holder.tvSmsUserTwo.setVisibility(View.GONE);
                    holder.tvTimeMessageUserTwo.setVisibility(View.GONE);
                    holder.tvSmsUserOne.setText(model.getSms());
                    holder.tvTimeMessageUserOne.setText(model.getDatetime());
                    Picasso.get().load(avatarURL).placeholder(R.drawable.default_avatar).into(holder.civAvatarUserOne);

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

    private void btnClickAvatar(String userID) {
        mFriendsReference.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Intent intent = new Intent(ChatActivity.this, ViewSingleFriendActivity.class);
                    intent.putExtra("userID", userID).toString();
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ChatActivity.this, ViewItemContactActivity.class);
                    intent.putExtra("userID", userID).toString();
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SendSMS() {
        String sms = edtInputMessage.getText().toString().trim();
        currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
        dateTime = simpleDateFormat.format(currentTime);
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timestamp = date.getTime();

        if (sms.isEmpty()) {
            Toast.makeText(this, "Tin nhắn không được để trống", Toast.LENGTH_SHORT).show();
        } else {
            HashMap hashMap = new HashMap();
            hashMap.put("sms", sms);
            hashMap.put("status", "Đã gửi");
            hashMap.put("userID", mUser.getUid());
            hashMap.put("datetime", dateTime);
            hashMap.put("timestamp", timestamp);
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


    private void createChatBox() {
        mSmsReference.child(mUser.getUid()).child(userID)
                .orderByChild("timestamp")
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                lastMessage = snapshot1.child("sms").getValue().toString();
                            }
                        }
                        HashMap hashMap = new HashMap();
                        hashMap.put("profilePic", avatarURL);
                        hashMap.put("userName", userName);
                        hashMap.put("lastMessage", lastMessage);
                        hashMap.put("friendID", userID);
                        mChatReference.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    HashMap mHashMap = new HashMap();
                                    mHashMap.put("profilePic", myAvatar);
                                    mHashMap.put("userName", myName);
                                    mHashMap.put("lastMessage", lastMessage);
                                    mHashMap.put("friendID", mUser.getUid());
                                    mChatReference.child(userID).child(mUser.getUid()).updateChildren(mHashMap);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_view_profile:
                mFriendsReference.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Intent intent = new Intent(ChatActivity.this, ViewSingleFriendActivity.class);
                            intent.putExtra("userID", userID).toString();
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ChatActivity.this, ViewItemContactActivity.class);
                            intent.putExtra("userID", userID).toString();
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.action_deleteChatbox:
                openDialogConfirmDeleteChatbox(Gravity.CENTER);
                break;
            case R.id.action_voiceCall:
                requestPermissionForVoiceCall();
                break;
            case R.id.action_videoCall:
                requestPermissionForVideoCall();
                break;
            default:
                break;
        }
        return true;
    }

    private void openDialogConfirmDeleteChatbox(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_delete_chatbox_dialog);
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
            Button btnConfirmDeleteChat = dialog.findViewById(R.id.btnConfirmDeleteChat);
            Button btnCancelConfirmDeleteChat = dialog.findViewById(R.id.btnCancelConfirmDeleteChat);

            btnConfirmDeleteChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProgressDialog dialogMessage = new ProgressDialog(ChatActivity.this);
                    dialogMessage.setTitle("Xóa đoạn tin nhắn");
                    dialogMessage.setMessage("Vui lòng đợi...");
                    dialogMessage.show();
                    mSmsReference.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mChatReference.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChatActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                            dialogMessage.dismiss();
                                            dialog.dismiss();
                                            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }

                                    }
                                });
                            }
                        }
                    });
                }
            });

            btnCancelConfirmDeleteChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    private void requestPermissionForVoiceCall() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                String type = "VoiceCall", status = "MakeCall";
                HistoryCallModel historyCallModel = new HistoryCallModel(userID, avatarURL, userName, status, type, mUser.getUid());
                historyCallModel.createHistoryCall();
                Intent intent = new Intent(ChatActivity.this, VoiceCallOutGoingActivity.class);
                intent.putExtra("receiverID", userID);
                startActivity(intent);
                finish();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ChatActivity.this, "Quyền bị từ chối\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\n\nVui lòng bật quyền tại [Setting] > [Permission]")
                .setPermissions(Manifest.permission.RECORD_AUDIO)
                .check();
    }

    private void requestPermissionForVideoCall() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                String type = "VideoCall", status = "MakeCall";
                HistoryCallModel historyCallModel = new HistoryCallModel(userID, avatarURL, userName, status, type, mUser.getUid());
                historyCallModel.createHistoryCall();
                Intent intent = new Intent(ChatActivity.this, VideoCallOutgoingActivity.class);
                intent.putExtra("friendID", userID);
                startActivity(intent);
                finish();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ChatActivity.this, "Quyền bị từ chối\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\n\nVui lòng bật quyền tại [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .check();
    }

}