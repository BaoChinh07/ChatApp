package com.example.chatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.FriendAdapter;
import com.example.chatapp.Models.Friend;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    public FriendsFragment() {
    }

    FriendAdapter friendAdapter;
    ArrayList<Friend> listFriends = new ArrayList<>();
    SearchView action_searchFriend;
    RecyclerView rvListFriend;
    FirebaseAuth mAuth;
    DatabaseReference mFriendReference, mDatabaseReference;
    String friendID;
    FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_friends, container, false);
        setControl(mView);
        setEvent();
        return mView;
    }

    private void setControl(View mView) {
        action_searchFriend = mView.findViewById(R.id.action_searchFriend);
        rvListFriend = mView.findViewById(R.id.rvListFriend);
        mAuth = FirebaseAuth.getInstance();
        mFriendReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUser = mAuth.getCurrentUser();

        friendAdapter = new FriendAdapter(getContext(), listFriends);
        rvListFriend.setAdapter(friendAdapter);
        /* Khởi tạo một LinearLayout và gán vào RecycleView */
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvListFriend.setLayoutManager(layoutManager);
    }

    private void setEvent() {

        loadFriend();

        action_searchFriend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                friendAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }

    private void loadFriend() {
        mFriendReference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFriends.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    friend.setFriendID(dataSnapshot.getKey());
                    listFriends.add(friend);
                }
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}