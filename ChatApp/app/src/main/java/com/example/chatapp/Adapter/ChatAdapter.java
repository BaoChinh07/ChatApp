package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.Chat;
import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.example.chatapp.View.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> implements Filterable {
    Context context;
    ArrayList<Chat> listChats;
    ArrayList<Chat> listFilterChatts;

    public ChatAdapter(Context context, ArrayList<Chat> listChats) {
        this.context = context;
        this.listChats = listChats;
        this.listFilterChatts = listChats;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = listChats.get(position);
        if (chat == null) {
            return;
        } else {
            Picasso.get().load(chat.getProfilePic()).into(holder.civAvatarItemChat);
            holder.tvItemChatName.setText(chat.getUserName());
            holder.tvLastMessage.setText(chat.getLastMessage());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userID = listChats.get(holder.getAdapterPosition()).getFriendID();
                    Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                    intent.putExtra("userID", userID);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listChats.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView civAvatarItemChat;
        public TextView tvItemChatName, tvLastMessage;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            civAvatarItemChat = (CircleImageView) itemView.findViewById(R.id.civAvatarItemChat);
            tvItemChatName = (TextView) itemView.findViewById(R.id.tvItemChatName);
            tvLastMessage = (TextView) itemView.findViewById(R.id.tvLastMessage);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    listChats = listFilterChatts;
                } else {
                    ArrayList<Chat> list = new ArrayList<>();
                    for (Chat chat : listFilterChatts) {
                        if (chat.getUserName().toString().toLowerCase().trim().contains(strSearch.toLowerCase().trim())) {
                            list.add(chat);
                        }
                    }
                    listChats = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listChats;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listChats = (ArrayList<Chat>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
