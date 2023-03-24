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

import com.example.chatapp.Models.Friends;
import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.example.chatapp.View.ChatActivity;
import com.example.chatapp.View.ViewSingleFriendActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> implements Filterable {
    Context context;
    ArrayList<Friends> listFriends;
    ArrayList<Friends> listFilterContacts;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;

    public FriendAdapter(Context context, ArrayList<Friends> listFriends) {
        this.context = context;
        this.listFriends = listFriends;
        this.listFilterContacts = listFriends;
    }
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.item_friend,parent,false);
        return new FriendViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friends friends = listFriends.get(position);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        if (friends ==null) {
            return;
        } else {
            Picasso.get().load(friends.getProfilePic()).into(holder.civAvatarItemFriend);
            holder.tvItemFriendName.setText(friends.getUserName());
            holder.tvItemFriendDescribe.setText(friends.getDescribe());

            mDatabaseReference.child(friends.getFriendID()).child("statusActivity").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    String friendID = listFriends.get(holder.getAdapterPosition()).getFriendID();
                    Intent intent = new Intent(holder.itemView.getContext(), ViewSingleFriendActivity.class);
                    intent.putExtra("userID", friendID);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return listFriends.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView civAvatarItemFriend, civItemFriendOnline, civItemFriendOffline;
        public TextView tvItemFriendName, tvItemFriendDescribe;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            civAvatarItemFriend = (CircleImageView) itemView.findViewById(R.id.civAvatarItemFriend);
            civItemFriendOnline = (CircleImageView) itemView.findViewById(R.id.civItemFriendOnline);
            civItemFriendOffline = (CircleImageView) itemView.findViewById(R.id.civItemFriendOffline);
            tvItemFriendName = (TextView) itemView.findViewById(R.id.tvItemFriendName);
            tvItemFriendDescribe = (TextView) itemView.findViewById(R.id.tvItemFriendDescribe);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    listFriends = listFilterContacts;
                } else {
                    ArrayList<Friends> list = new ArrayList<>();
                    for (Friends friends : listFilterContacts) {
                        if (friends.getUserName().toString().toLowerCase().trim().contains(strSearch.toLowerCase().trim())) {
                            list.add(friends);
                        }
                    }
                    listFriends = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listFriends;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listFriends = (ArrayList<Friends>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
