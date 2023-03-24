package com.example.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

import com.example.chatapp.Adapter.ChatAdapter;
import com.example.chatapp.Adapter.FriendAdapter;
import com.example.chatapp.Models.Chat;
import com.example.chatapp.Models.Friends;
import com.example.chatapp.R;
import com.example.chatapp.SignInActivity;
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

import org.checkerframework.checker.units.qual.C;

public class ChatsFragment extends Fragment {
    SearchView action_searchChat;
    Toolbar toolbar_chats;
    RecyclerView rvListChat;
    FirebaseRecyclerOptions<Chat> optionsChat;
    FirebaseRecyclerAdapter<Chat,ChatAdapter> adapterChat;
    FirebaseAuth mAuth;
    DatabaseReference mChatReference, mUserReference;
    String friendID;
    FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_chats, container, false);
        setControl(mView);
        setEvent();
        return mView;
    }

    private void setControl(View mView) {
        toolbar_chats = (Toolbar) mView.findViewById(R.id.toolbar_chats);
        rvListChat = (RecyclerView) mView.findViewById(R.id.rvListChat);
        action_searchChat = mView.findViewById(R.id.action_searchChat);
        mAuth = FirebaseAuth.getInstance();
        mChatReference = FirebaseDatabase.getInstance().getReference().child("Chats");
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mUser = mAuth.getCurrentUser();
        /* Tạo ngăn cách giữa 2 đối tượng*/
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvListChat.addItemDecoration(itemDecoration);
        /* Khởi tạo một LinearLayout và gán vào RecycleView */
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvListChat.setLayoutManager(layoutManager);
    }
    private void setEvent() {
        loadListChat("");
        toolbar_chats.addMenuProvider(new MenuProvider() {
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
                    case R.id.action_logout:
                        mAuth.signOut();
                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void loadListChat(String s) {
        Query query = mChatReference.child(mUser.getUid()).orderByChild("userName").startAt(s).endAt(s+"\uf8ff");
        optionsChat = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(query,Chat.class).build();
        adapterChat = new FirebaseRecyclerAdapter<Chat, ChatAdapter>(optionsChat) {
            @Override
            protected void onBindViewHolder(@NonNull ChatAdapter holder, int position, @NonNull Chat model) {
                Picasso.get().load(model.getProfilePic()).into(holder.civAvatarItemChat);
                holder.tvItemChatName.setText(model.getUserName());
                holder.tvLastMessage.setText(model.getLastMessage());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("userID", getRef(holder.getAdapterPosition()).getKey().toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ChatAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);
                return new ChatAdapter(view);
            }
        };
        adapterChat.startListening();
        rvListChat.setAdapter(adapterChat);
    }
}