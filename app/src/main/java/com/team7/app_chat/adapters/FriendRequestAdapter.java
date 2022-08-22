package com.team7.app_chat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team7.app_chat.R;
import com.team7.app_chat.models.FriendRequest;
import com.team7.app_chat.models.User;

import java.util.ArrayList;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> implements View.OnClickListener {
private Context context;
private ArrayList<FriendRequest> friendRequests;
private SelectListstener listener;

public FriendRequestAdapter(Context context, ArrayList<FriendRequest> friendRequests, SelectListstener listener) {
        this.context = context;
        this.friendRequests = friendRequests;
        this.listener = listener;
        }

public interface SelectListstener {
    void onAccept(FriendRequest friendRequest, int position);
    void onDeAccept(FriendRequest friendRequest, int position);
}

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_friend_request, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        FriendRequest friendRequest = friendRequests.get(position);
        friendRequest.getSender().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);
                holder.textViewName.setText(user.getFullName());
                holder.textViewDesc.setText(user.getEmail());
                Glide.with(context).load(user.getAvatar()).into(holder.imageView);
            }
        });
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAccept(friendRequests.get(position),position);
            }
        });
        holder.btnDeAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeAccept(friendRequests.get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    @Override
    public void onClick(View view) {

    }


public class FriendRequestViewHolder extends RecyclerView.ViewHolder {
    RoundedImageView imageView;
    TextView textViewName;
    TextView textViewDesc;
    ImageView btnAccept;
    ImageView btnDeAccept;

    public FriendRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageUser);
        textViewName = itemView.findViewById(R.id.nameUser);
        textViewDesc = itemView.findViewById(R.id.descUser);
        btnAccept = itemView.findViewById(R.id.btnAccept);
        btnDeAccept = itemView.findViewById(R.id.btnUnAccept);
    }
}
}
