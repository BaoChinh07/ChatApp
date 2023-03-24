package com.example.chatapp.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.example.chatapp.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CallFragment extends Fragment {
    Toolbar toolbar_call;
    RecyclerView rvListCallHistory;
    ArrayList<Users> listCallHistory = new ArrayList<>();

    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView  = inflater.inflate(R.layout.fragment_call, container, false);
        setControl(mView);
        setEvent();
        return mView;
    }

    private void setControl(View mView) {
        toolbar_call = (Toolbar) mView.findViewById(R.id.toolbar_call);
        rvListCallHistory = (RecyclerView) mView.findViewById(R.id.rvListCallHistory);

        mAuth = FirebaseAuth.getInstance();
    }

    private void setEvent() {
        toolbar_call.addMenuProvider(new MenuProvider() {
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
                        openLogout(Gravity.CENTER);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void openLogout(int gravity) {
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
}