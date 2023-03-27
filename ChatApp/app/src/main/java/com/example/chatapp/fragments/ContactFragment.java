package com.example.chatapp.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

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


import com.example.chatapp.Adapter.ContactAdapter;
import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.example.chatapp.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactFragment extends Fragment {
    public ContactFragment() {
    }
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ContactAdapter contactAdapter;
    RecyclerView rvListContact;
    SearchView action_search;
    ArrayList<Users> listContact = new ArrayList<>();
    FirebaseDatabase mDatabase;
    DatabaseReference mFriendReference, mDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_contact, container, false);
        setControl(mView);
        setEvent();
        return mView;
    }

    private void setControl(View mView) {
        action_search = (SearchView) mView.findViewById(R.id.action_search);
        action_search.clearFocus();
        rvListContact = (RecyclerView) mView.findViewById(R.id.rvListContact);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendReference = FirebaseDatabase.getInstance().getReference().child("Friends");

        /* Khởi tạo đối tượng Adapter*/
        contactAdapter = new ContactAdapter(getContext(), listContact);
        rvListContact.setAdapter(contactAdapter);
//        contactAdapter.notifyDataSetChanged();

        /* Tạo ngăn cách giữa 2 đối tượng*/
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvListContact.addItemDecoration(itemDecoration);
        /* Khởi tạo một LinearLayout và gán vào RecycleView */
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvListContact.setLayoutManager(layoutManager);
    }

    private void setEvent() {

            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listContact.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Users users = dataSnapshot.getValue(Users.class);
                        mAuth = FirebaseAuth.getInstance();
                        mUser = mAuth.getCurrentUser();
                        if (mUser != null && !users.getEmail().equals(mUser.getEmail()) ) {
                            users.setUserID(dataSnapshot.getKey());
                            listContact.add(users);
                        }

                    }
                    contactAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        // Xử lý sự kiện tìm kiếm
        action_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contactAdapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                contactAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}