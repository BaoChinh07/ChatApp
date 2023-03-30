package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class    SignUpActivity extends AppCompatActivity {
    Button btnSignUp;
    EditText edtUserName, edtPassword, edtEmail, edtConfirmPassword;
    TextView tvClickToSignIn;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;

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
        database = FirebaseDatabase.getInstance();
        edtUserName = findViewById(R.id.edtUserName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
    }
    public void setEvent(){
        ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setTitle("Đang tạo tài khoản");
        dialog.setMessage("Chúng tôi đang tạo tài khoản cho bạn");
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtUserName.getText().toString().isEmpty() && !edtEmail.getText().toString().isEmpty()
                        && !edtPassword.getText().toString().isEmpty()
                        && !edtConfirmPassword.getText().toString().isEmpty()
                        && edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
                    dialog.show();
                    mAuth.fetchSignInMethodsForEmail(edtEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
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
                                                            if(task.isSuccessful()){
                                                                String userID = task.getResult().getUser().getUid();
                                                                String statusActivity = "Offline";
                                                                Users user = new Users(edtUserName.getText().toString(), edtEmail.getText().toString(), edtPassword.getText().toString(),userID,statusActivity);
                                                                database.getReference().child("Users").child(userID).setValue(user);
                                                                Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Lỗi khi kiểm tra email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                 else {
                     if (edtUserName.getText().toString().isEmpty() || edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty() || edtConfirmPassword.getText().toString().isEmpty()){
                         Toast.makeText(SignUpActivity.this, "Vui lòng nhập đầy đủ thông tin đăng ký!", Toast.LENGTH_SHORT).show();
                     }
                     if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
                         Toast.makeText(SignUpActivity.this, "Mật khẩu xác nhận không đúng, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                     }

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