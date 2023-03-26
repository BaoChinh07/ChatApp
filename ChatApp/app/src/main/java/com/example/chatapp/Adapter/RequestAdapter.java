package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.Requests;
import com.example.chatapp.R;
import com.example.chatapp.View.ViewItemContactActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    Context context;
    ArrayList<Requests> requestsList;
    public RequestAdapter(Context context, ArrayList<Requests> requestsList) {
        this.context = context;
        this.requestsList = requestsList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.item_notification,parent,false);
        return new RequestViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Requests requests = requestsList.get(position);
        if (requests == null) {
            return;
        } else {
            holder.tvItemRequest.setText(requests.getUserName());
            Picasso.get().load(requests.getProfilePic()).placeholder(R.drawable.default_avatar).into(holder.civAvatarItemRequest);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userID = requestsList.get(holder.getAdapterPosition()).getUserID();
                    Intent intent = new Intent(holder.itemView.getContext(), ViewItemContactActivity.class);
                    intent.putExtra("userID",userID);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civAvatarItemRequest;
        TextView tvItemRequest;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            civAvatarItemRequest = (CircleImageView) itemView.findViewById(R.id.civAvatarItemRequest);
            tvItemRequest = (TextView) itemView.findViewById(R.id.tvItemRequest);
        }
    }
}
