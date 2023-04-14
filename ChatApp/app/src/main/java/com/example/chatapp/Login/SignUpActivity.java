package com.example.chatapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class    SignUpActivity extends AppCompatActivity {
    Button btnSignUp;
    EditText edtUserName, edtPassword, edtEmail, edtConfirmPassword;
    TextView tvClickToSignIn;
    ProgressDialog dialog;
     FirebaseAuth mAuth;
    DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setControl();
        setEvent();
    }
    public void setControl(){
        btnSignUp = findViewById(R.id.btnSignUp);
        tvClickToSignIn = findViewById(R.id.tvClickToSignIn);
        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        edtUserName = findViewById(R.id.edtUserName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setTitle("Đang tạo tài khoản");
        dialog.setMessage("Chúng tôi đang tạo tài khoản cho bạn");
    }
    public void setEvent(){
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtUserName.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập Tên người dùng", Toast.LENGTH_SHORT).show();
                } else if (edtEmail.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();
                } else if (edtPassword.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập Mật khẩu", Toast.LENGTH_SHORT).show();
                } else if (edtConfirmPassword.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập Xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
                } else if (!edtConfirmPassword.getText().toString().trim().equals(edtPassword.getText().toString().trim())) {
                    Toast.makeText(SignUpActivity.this, "Xác nhận mật khẩu không chính xác, vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.show();
                    mAuth.fetchSignInMethodsForEmail(edtEmail.getText().toString().trim()).
                            addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        SignInMethodQueryResult result = task.getResult();
                                        boolean emailExists = result.getSignInMethods().size() > 0;
                                        if (emailExists) {
                                            dialog.dismiss();
                                            Toast.makeText(SignUpActivity.this, "Email đã được sử dụng, vui lòng sử dụng email khác", Toast.LENGTH_SHORT).show();
                                        } else {
                                            mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim())
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            dialog.dismiss();
                                                            if (task.isSuccessful()) {
                                                                String userID = task.getResult().getUser().getUid();
                                                                Users users = new Users(edtUserName.getText().toString().trim(), edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim(), userID, "Offline");
                                                                mUserReference.child(userID).setValue(users);
                                                                Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(SignUpActivity.this, "Lỗi khi kiểm tra email", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                }
            }
        });

        // Ấn chữ đăng nhập
        tvClickToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvClickToSignIn.clearFocus();
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}