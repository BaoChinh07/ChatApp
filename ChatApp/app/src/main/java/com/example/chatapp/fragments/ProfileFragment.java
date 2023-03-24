package com.example.chatapp.fragments;


import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.example.chatapp.SignInActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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


public class ProfileFragment extends Fragment {
    Toolbar toolbar_profile;
    private static final int MY_REQUEST_CODE = 100;
    private View mView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    Toolbar toolbarProfile;
    Button btnLogOut, btnUpdateProfile;
    ImageButton btnUpdateAvatar;
    TextView tvUserName, tvEmail, tvDescribe, tvGender;
    CircleImageView civAvatar;
    String describe, userName, gender;
    Uri uri;
    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        setControl(mView);
        getProfileUser();
        setEvent();
        return mView;
    }

    // Hàm khởi tạo các biến
    private void setControl(View mView) {
        toolbar_profile = (Toolbar) mView.findViewById(R.id.toolbar_profile);
        btnUpdateProfile = (Button) mView.findViewById(R.id.btnUpdateProfile);
        btnUpdateAvatar = (ImageButton) mView.findViewById(R.id.btnUpdateAvatar);
        btnLogOut = (Button) mView.findViewById(R.id.btnLogOut);
        tvDescribe = (TextView) mView.findViewById(R.id.tvDescribe);
        tvUserName = (TextView) mView.findViewById(R.id.tvUserName);
        tvEmail = (TextView) mView.findViewById(R.id.tvEmail);
        tvGender = (TextView) mView.findViewById(R.id.tvGender);
        civAvatar = (CircleImageView) mView.findViewById(R.id.civAvatar);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        mStorageReference = FirebaseStorage.getInstance().getReference();
        civAvatar.setTag("man");
    }

    //Hàm lấy thông tin Profile
    private void getProfileUser() {
        mFirebaseDatabase.getReference().child("Users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.default_avatar).into(civAvatar);
                tvDescribe.setText(users.getDescribe());
                tvUserName.setText(users.getUserName());
                tvEmail.setText(users.getEmail());
                tvGender.setText(users.getGender());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // Hàm xử lý sự kiện
    private void setEvent() {

        //Xử lý toolbar
        toolbar_profile.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar,menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_notifications:
                        Toast.makeText(getContext(), "Chọn thông báo", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_logout:
                        mAuth.signOut();
                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        //Nút Cập nhật
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUpdateProfileDialog(Gravity.CENTER);
            }
        });

        //Nút Đăng xuất
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDialog(Gravity.CENTER);
            }
        });

        //Nút cập nhật Avatar
        btnUpdateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openGallery();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, MY_REQUEST_CODE);
            }
        });
    }

    private void openConfirmDialog(int gravity) {
        final Dialog dialog = new Dialog(getActivity());
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
                    mAuth.signOut();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
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


    //Mở Dialog cập nhật thông tin
    private void openUpdateProfileDialog(int gravity) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.update_profile_dialog);
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
            } else radWoman.setChecked(true);
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
                    tvDescribe.setText(edtDescribe.getText().toString().trim());
                    tvUserName.setText(edtUpdateUserName.getText().toString().trim());
                    //Xét giới tính
                    if (radMan.isChecked()) {
                        tvGender.setText(radMan.getText().toString().trim());
                    } else {
                        tvGender.setText(radWoman.getText().toString().trim());
                    }
                    if (tvGender.getText().toString().equals("Nữ") && civAvatar.getTag() == null) {
                        civAvatar.setImageResource(R.drawable.default_avatar_woman);
                    }
                    //Điều kiện tên người dùng
                    if (tvUserName.getText().toString().trim().equals(""))
                        Toast.makeText(getActivity(), "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show();
                    describe = edtDescribe.getText().toString().trim();
                    userName = edtUpdateUserName.getText().toString().trim();
                    if (radMan.isChecked()) {
                        gender = radMan.getText().toString().trim();
                    } else {
                        gender = radWoman.getText().toString().trim();
                    }
                    HashMap<String, Object> obj = new HashMap<>();
                    obj.put("describe", describe);
                    obj.put("userName", userName);
                    obj.put("gender", gender);
                    mFirebaseDatabase.getReference().child("Users").child(mAuth.getUid()).updateChildren(obj);
                    Toast.makeText(getActivity(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    //Xử lý kết quả trả về từ hành động startActivityForResult

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null) {
            Uri uri = data.getData();
            civAvatar.setImageURI(uri);
            final StorageReference reference = mStorageReference.child("profilePic").child(mAuth.getUid());
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mFirebaseDatabase.getReference().child("Users").child(mAuth.getUid()).child("profilePic").setValue(uri.toString());
                        }
                    });
                }
            });
        }
    }
}