package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.HistoryCall;
import com.example.chatapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryCallAdapter extends RecyclerView.Adapter<HistoryCallAdapter.HistoryCallViewHolder> {
    Context context;
    ArrayList<HistoryCall> listHistoryCall;

    public HistoryCallAdapter(Context context, ArrayList<HistoryCall> listHistoryCall) {
        this.context = context;
        this.listHistoryCall = listHistoryCall;
    }

    @NonNull
    @Override
    public HistoryCallAdapter.HistoryCallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.item_history_call,parent,false);
        return new HistoryCallViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryCallAdapter.HistoryCallViewHolder holder, int position) {
        HistoryCall historyCall = listHistoryCall.get(position);
        if (historyCall == null){
            return;
        } else {
            Picasso.get().load(historyCall.getUserAvatarURL()).placeholder(R.drawable.default_avatar).into(holder.civAvatarItemHistoryCall);
            holder.tvItemHistoryCallName.setText(historyCall.getUserName());
            holder.tvCallTime.setText(historyCall.getCallTime().toString().trim());
            if (historyCall.getStatusCall().equals("MakeCall")) {
                holder.imageViewCallMake.setVisibility(View.VISIBLE);
                holder.imageViewCallReceive.setVisibility(View.GONE);
                holder.imageViewCallMissed.setVisibility(View.GONE);
            } else if (historyCall.getStatusCall().equals("ReceiveCall")) {
                holder.imageViewCallMake.setVisibility(View.GONE);
                holder.imageViewCallReceive.setVisibility(View.VISIBLE);
                holder.imageViewCallMissed.setVisibility(View.GONE);
            } else if (historyCall.getStatusCall().equals("MissedCall")) {
                holder.imageViewCallMake.setVisibility(View.GONE);
                holder.imageViewCallReceive.setVisibility(View.GONE);
                holder.imageViewCallMissed.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewCallMake.setVisibility(View.GONE);
                holder.imageViewCallReceive.setVisibility(View.GONE);
                holder.imageViewCallMissed.setVisibility(View.GONE);
            }
            if (historyCall.getTypeCall().equals("VideoCall")) {
                holder.imageViewVideoCall.setVisibility(View.VISIBLE);
                holder.imageViewVoiceCall.setVisibility(View.GONE);
            } else if (historyCall.getTypeCall().equals("VoiceCall")) {
                holder.imageViewVideoCall.setVisibility(View.GONE);
                holder.imageViewVoiceCall.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewVideoCall.setVisibility(View.GONE);
                holder.imageViewVoiceCall.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return listHistoryCall.size();
    }

    public static class HistoryCallViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civAvatarItemHistoryCall;
        TextView tvItemHistoryCallName, tvCallTime;
        ImageView imageViewCallMake, imageViewCallReceive, imageViewCallMissed, imageViewVideoCall, imageViewVoiceCall;
        public HistoryCallViewHolder(@NonNull View itemView) {
            super(itemView);
            civAvatarItemHistoryCall = itemView.findViewById(R.id.civAvatarItemHistoryCall);
            tvItemHistoryCallName = itemView.findViewById(R.id.tvItemHistoryCallName);
            tvCallTime = itemView.findViewById(R.id.tvCallTime);
            imageViewCallMake = itemView.findViewById(R.id.imageViewCallMake);
            imageViewCallReceive = itemView.findViewById(R.id.imageViewCallReceive);
            imageViewCallMissed = itemView.findViewById(R.id.imageViewCallMissed);
            imageViewVideoCall = itemView.findViewById(R.id.imageViewVideoCall);
            imageViewVoiceCall = itemView.findViewById(R.id.imageViewVoiceCall);
        }
    }
}
