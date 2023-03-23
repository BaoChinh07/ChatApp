package com.example.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.FriendAdapter;
import com.example.chatapp.MainActivity;
import com.example.chatapp.Models.Friends;
import com.example.chatapp.R;
import com.example.chatapp.View.ChatActivity;
import com.example.chatapp.View.ViewSingleFriendActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    SearchView action_searchFriend;
    RecyclerView rvListFriend;
    FirebaseRecyclerOptions<Friends> options;
    FirebaseRecyclerAdapter<Friends, FriendAdapter> friendsAdapter;
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
        /* Tạo ngăn cách giữa 2 đối tượng*/
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvListFriend.addItemDecoration(itemDecoration);
        /* Khởi tạo một LinearLayout và gán vào RecycleView */
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvListFriend.setLayoutManager(layoutManager);
    }

    private void setEvent() {
//        loadFriends();
        loadFriend("");
    }

    private void loadFriend(String s) {
        Query query = mFriendReference.child(mUser.getUid()).orderByChild("userName").startAt(s).endAt(s+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query,Friends.class).build();
        friendsAdapter = new FirebaseRecyclerAdapter<Friends, FriendAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendAdapter holder, int position, @NonNull Friends model) {
                Picasso.get().load(model.getProfilePic()).into(holder.civAvatarItemFriend);
                holder.tvItemFriendName.setText(model.getUserName());
                holder.tvItemFriendDescribe.setText(model.getDescribe());

                mDatabaseReference.child(model.getFriendID()).child("statusActivity").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getValue().toString().equals("Online")) {
                                holder.civItemFriendOnline.setVisibility(View.VISIBLE);
                                holder.civItemFriendOffline.setVisibility(View.GONE);
                            } else {
                                holder.civItemFriendOnline.setVisibility(View.GONE);
                                holder.civItemFriendOffline.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), ViewSingleFriendActivity.class);
                        intent.putExtra("friendID", getRef(holder.getAdapterPosition()).getKey().toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FriendAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
                return new FriendAdapter(mView);
            }
        };
        friendsAdapter.startListening();
        rvListFriend.setAdapter(friendsAdapter);
    }

}