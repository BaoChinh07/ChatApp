package com.example.chatapp.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.ViewHolder {
    public CircleImageView civAvatarItemChat;
    public TextView tvItemChatName, tvLastMessage;

    public ChatAdapter(@NonNull View itemView) {
        super(itemView);
        civAvatarItemChat = (CircleImageView) itemView.findViewById(R.id.civAvatarItemChat);
        tvItemChatName = (TextView) itemView.findViewById(R.id.tvItemChatName);
        tvLastMessage = (TextView) itemView.findViewById(R.id.tvLastMessage);
    }
}
