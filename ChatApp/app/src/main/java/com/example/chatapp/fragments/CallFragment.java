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
        rvListCallHistory = (RecyclerView) mView.findViewById(R.id.rvListCallHistory);

        mAuth = FirebaseAuth.getInstance();
    }

    private void setEvent() {
    }

}