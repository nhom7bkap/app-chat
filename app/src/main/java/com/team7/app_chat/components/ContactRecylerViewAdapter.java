package com.team7.app_chat.components;

import android.annotation.SuppressLint;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.team7.app_chat.R;
import com.team7.app_chat.models.Contact;

import java.util.ArrayList;

public class ContactRecylerViewAdapter extends RecyclerView.Adapter<ContactRecylerViewAdapter.ContactViewHolder> {
    private ArrayList<Contact> contacts;

    public ContactRecylerViewAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contact_item, parent, false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        holder.textViewName.setText(contact.getNickName());
        holder.textViewDesc.setText("Last recent");
        holder.imageView.setImageResource(R.drawable.ic_user_profile_svgrepo_com);
        holder.position = position;
        holder.contact = contact;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        TextView textViewName;
        TextView textViewDesc;
        View root;
        int position;
        Contact contact;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            imageView = itemView.findViewById(R.id.imageContact);
            textViewName = itemView.findViewById(R.id.nameContact);
            textViewDesc = itemView.findViewById(R.id.descContact);
            itemView.setOnClickListener((v) -> {
                Log.d("ViewHolder", "item position: " + position + " contact: " + contact.getNickName());
            });

            itemView.findViewById(R.id.btnLike).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("ViewHolder", "Like button " + contact.getNickName());
                }
            });
        }
    }
}
