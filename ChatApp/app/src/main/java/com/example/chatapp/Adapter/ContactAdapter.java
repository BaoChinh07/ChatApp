package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.example.chatapp.View.ViewItemContactActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {
        Context context;
        ArrayList<Users> listContacts;
        ArrayList<Users> listFilterContacts;

    public ContactAdapter(Context context, ArrayList<Users> listContacts) {
        this.context = context;
        this.listContacts = listContacts;
        this.listFilterContacts = listContacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Users users = listContacts.get(position);
        if (users != null) {
            Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.default_avatar).into(holder.civAvatarItemContact);
            holder.tvItemContactName.setText(users.getUserName());
            holder.tvItemContactEmail.setText(users.getEmail());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userID = listContacts.get(holder.getAdapterPosition()).getUserID();
                    Intent intent = new Intent(holder.itemView.getContext(), ViewItemContactActivity.class);
                    intent.putExtra("userID",userID);
                    context.startActivity(intent);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listContacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civAvatarItemContact;
        TextView tvItemContactName, tvItemContactEmail;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            civAvatarItemContact = itemView.findViewById(R.id.civAvatarItemContact);
            tvItemContactName = itemView.findViewById(R.id.tvItemContactName);
            tvItemContactEmail = itemView.findViewById(R.id.tvItemContactEmail);
        }
    }


    /* Khởi tọa Filter phục vụ searchView*/
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    listContacts = listFilterContacts;
                } else {
                    ArrayList<Users> list = new ArrayList<>();
                    for (Users users : listFilterContacts) {
                        if (users.getEmail().toString().toLowerCase().trim().equals(strSearch.toLowerCase().trim())) {
                            list.add(users);
                        }
                    }
                    listContacts = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listContacts;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listContacts = (ArrayList<Users>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
