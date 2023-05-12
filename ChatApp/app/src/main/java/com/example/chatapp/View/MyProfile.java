package com.example.chatapp.View;

import static com.example.chatapp.MainActivity.MY_REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Login.SignInActivity;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.example.chatapp.Utilities.Utilities;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {
    Toolbar toolbar_myProfile;
    CircleImageView civAvatar;
    ImageButton btnUpdateAvatar;
    Button btnUpdateProfile, btnLogOut;
    TextView tvUserName, tvEmail, tvDescribe, tvGender;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserReference;
    FirebaseStorage storage;
    StorageReference mStorageReference, mDefaultAvatarReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        setControl();
        setEvent();
    }


    private void setControl() {
        toolbar_myProfile = findViewById(R.id.toolbar_myProfile);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnUpdateAvatar = (ImageButton) findViewById(R.id.btnUpdateAvatar);
        btnLogOut = findViewById(R.id.btnLogOut);
        tvDescribe = findViewById(R.id.tvDescribe);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvGender = findViewById(R.id.tvGender);
        civAvatar = findViewById(R.id.civAvatar);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storage = FirebaseStorage.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDefaultAvatarReference = storage.getReference().child("profilePic/default_avatar.png");
    }

    private void setEvent() {
        actionToolbar();
        getProfileUser();

        btnUpdateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUpdateProfileDialog(Gravity.CENTER);
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDialog(Gravity.CENTER);
            }
        });
    }

    private void actionToolbar() {
        setSupportActionBar(toolbar_myProfile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Hồ sơ cá nhân");
        toolbar_myProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }

    private void getProfileUser() {
        mUserReference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.default_avatar).into(civAvatar);
                        tvDescribe.setText(user.getDescribe());
                        tvUserName.setText(user.getUserName());
                        tvEmail.setText(user.getEmail());
                        tvGender.setText(user.getGender());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, MY_REQUEST_CODE);
    }
    //Xử lý kết quả trả về từ hành động startActivityForResult

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                long timestamp = System.currentTimeMillis();
                String fileName = "avatar_" + timestamp;
                Uri uri = data.getData();
                civAvatar.setImageURI(uri);
                final StorageReference reference = mStorageReference.child("profilePic").child(mUser.getUid()).child(fileName);
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mUserReference.child(mUser.getUid()).child("profilePic").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        }
    }

    private void openUpdateProfileDialog(int gravity) {

        final Dialog dialog = new Dialog(MyProfile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_profile);
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
            //Ánh xạ
            EditText edtDescribe = dialog.findViewById(R.id.edtDescribe);
            EditText edtUpdateUserName = dialog.findViewById(R.id.edtUpdateUserName);
            RadioButton radMan = dialog.findViewById(R.id.radMan);
            RadioButton radWoman = dialog.findViewById(R.id.radWoman);
            Button btnSaveNewProfile = dialog.findViewById(R.id.btnSaveNewProfile);
            Button btnCancel = dialog.findViewById(R.id.btnCancel);

            //Ánh xạ thông tin Profile cũ lên Dialog
            edtDescribe.setText(tvDescribe.getText().toString().trim());
            edtUpdateUserName.setText(tvUserName.getText().toString().trim());
            if (tvGender.getText().toString().trim().equals("Nam")) {
                radMan.setChecked(true);
            } else
                radWoman.setChecked(true);
            //Xử lý sự kiện nút "Hủy"
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            //Xử lý sự kiện nút "Lưu"
            btnSaveNewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Điều kiện tên người dùng
                    if (edtUpdateUserName.toString().isEmpty()) {
                        Toast.makeText(MyProfile.this, "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show();
                    } else {
                        String userName = edtUpdateUserName.getText().toString().trim();
                        String describe = edtDescribe.getText().toString().trim();
                        String gender;
                        if (radMan.isChecked()) {
                            gender = radMan.getText().toString().trim();
                        } else
                            gender = radWoman.getText().toString().trim();
                        HashMap<String, Object> hashMap = new HashMap();
                        hashMap.put("userName", userName);
                        hashMap.put("describe", describe);
                        hashMap.put("gender", gender);
                        mUserReference.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(MyProfile.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        dialog.show();
    }

    private void openConfirmDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_dialog);
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
            Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
            Button btnCancelConfirm = dialog.findViewById(R.id.btnCancelConfirm);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mUser != null) {
                        mUserReference.child(mUser.getUid()).child("fcmToken").removeValue();
                    }
                    mAuth.signOut();
                    Intent intent = new Intent(MyProfile.this, SignInActivity.class);
                    startActivity(intent);
                    Toast.makeText(MyProfile.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    statusActivity("Offline", mUserReference.child(mUser.getUid()));
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

    public void statusActivity(String statusActivity, DatabaseReference reference) {
        reference = mUserReference.child(mUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("statusActivity", statusActivity);
        reference.updateChildren(hashMap);
    }

}