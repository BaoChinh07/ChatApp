package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.Friends;
import com.example.chatapp.R;
import com.example.chatapp.View.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.ViewHolder {
    public CircleImageView civAvatarItemFriend, civItemFriendOnline, civItemFriendOffline;
    public TextView tvItemFriendName, tvItemFriendDescribe;
    public FriendAdapter(@NonNull View itemView) {
        super(itemView);
        civAvatarItemFriend = (CircleImageView) itemView.findViewById(R.id.civAvatarItemFriend);
        civItemFriendOnline = (CircleImageView) itemView.findViewById(R.id.civItemFriendOnline);
        civItemFriendOffline = (CircleImageView) itemView.findViewById(R.id.civItemFriendOffline);
        tvItemFriendName = (TextView) itemView.findViewById(R.id.tvItemFriendName);
        tvItemFriendDescribe = (TextView) itemView.findViewById(R.id.tvItemFriendDescribe);
    }
}
