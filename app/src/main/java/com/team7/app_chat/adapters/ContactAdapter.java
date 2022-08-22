package com.team7.app_chat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team7.app_chat.R;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.FriendRequest;
import com.team7.app_chat.models.User;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private Context context;
    private ArrayList<Contact> listContacts;
    private ContactSelectListstener listener;

    public ContactAdapter(Context context, ArrayList<Contact> listContacts, ContactSelectListstener liststener) {
        this.context = context;
        this.listContacts = listContacts;
        this.listener = liststener;
    }

    public interface ContactSelectListstener {
        void onClick(User user);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_user, parent, false);
        return new ContactViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = listContacts.get(position);
        DocumentReference dof = contact.getUser();
        dof.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);
                holder.textViewName.setText(user.getFullName());
                holder.textViewDesc.setText("Last recently");
                Glide.with(context).load(user.getAvatar()).into(holder.imageView);
                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onClick(user);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listContacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        TextView textViewName;
        TextView textViewDesc;
        LinearLayout linearLayout;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageContact);
            textViewName = itemView.findViewById(R.id.nameContact);
            textViewDesc = itemView.findViewById(R.id.descContact);
            linearLayout = itemView.findViewById(R.id.contactLayout);
//            itemView.findViewById(R.id.btnLike).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d("ViewHolder", "Like button " + contact.getNickName());
//                }
//            });
        }
    }
}
