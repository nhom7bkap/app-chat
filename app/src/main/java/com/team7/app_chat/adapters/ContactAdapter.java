package com.team7.app_chat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.FriendRequest;
import com.team7.app_chat.models.User;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<DocumentSnapshot> list;
    private INavChat iNavChat;
    private Context context;
    private User user;

    public ContactAdapter(Context context, List<DocumentSnapshot> list, INavChat iNavChat) {
        this.context = context;
        this.list = list;
        this.iNavChat = iNavChat;
    }

    public interface INavChat {
        void goToChat(DocumentSnapshot doc);
        void onHold(DocumentSnapshot doc);
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user, parent, false);
        return new ContactViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        DocumentSnapshot doc = list.get(position);
        Contact contact = doc.toObject(Contact.class);

        new UserRepository().getDocRf(contact.getUser().getId()).get().addOnSuccessListener(documentSnapshot -> {
            this.user = documentSnapshot.toObject(User.class);
            String path = user.getAvatar();
            if (path != null) {
                Glide.with(context).load(path).into(holder.imageView);
            }
            holder.textViewName.setText(user.getFullName());
            holder.textViewDesc.setText(user.getEmail());
        });
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                iNavChat.onHold(doc);
                return true;
            }
        });
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNavChat.goToChat(doc);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewDesc;
        LinearLayout linearLayout;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageContact);
            textViewName = itemView.findViewById(R.id.nameContact);
            textViewDesc = itemView.findViewById(R.id.descContact);
            linearLayout = itemView.findViewById(R.id.contactLayout);
        }
    }
}
