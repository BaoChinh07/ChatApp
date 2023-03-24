package com.example.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp.Adapter.FriendAdapter;
import com.example.chatapp.Models.Friends;
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

public class FriendsFragment extends Fragment {
    public FriendsFragment() {
    }

    FriendAdapter friendAdapter;
    ArrayList<Friends> listFriends = new ArrayList<>();
    Toolbar toolbar_friend;
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
        toolbar_friend = mView.findViewById(R.id.toolbar_friend);
        action_searchFriend = mView.findViewById(R.id.action_searchFriend);
        rvListFriend = mView.findViewById(R.id.rvListFriend);
        mAuth = FirebaseAuth.getInstance();
        mFriendReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUser = mAuth.getCurrentUser();

        friendAdapter = new FriendAdapter(getContext(), listFriends);
        rvListFriend.setAdapter(friendAdapter);
        /* Tạo ngăn cách giữa 2 đối tượng*/
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvListFriend.addItemDecoration(itemDecoration);
        /* Khởi tạo một LinearLayout và gán vào RecycleView */
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvListFriend.setLayoutManager(layoutManager);
    }

    private void setEvent() {

        loadFriend();
        toolbar_friend.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu);
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
                    Friends friends = dataSnapshot.getValue(Friends.class);
                    mAuth = FirebaseAuth.getInstance();
                    mUser = mAuth.getCurrentUser();
                    friends.setFriendID(dataSnapshot.getKey());
                    listFriends.add(friends);
                }
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}