package com.team7.app_chat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team7.app_chat.R;
import com.team7.app_chat.models.User;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<User> listUser;
    private SelectListstener listener;

    public UsersAdapter(Context context, ArrayList<User> listUser, SelectListstener listener) {
        this.context = context;
        this.listUser = listUser;
        this.listener = listener;
    }

    public interface SelectListstener {
        void onItemClicked(User user);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_friend, parent, false);
        return new UserViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = listUser.get(position);

        holder.textViewName.setText(user.getFullName());
        holder.textViewDesc.setText(user.getEmail());
        String path = user.getAvatar();
        if (path != null) {
            Glide.with(context).load(path).into(holder.imageView);
        }
        holder.position = position;
        holder.user = user;
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(listUser.get(position));
                holder.btnAdd.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    @Override
    public void onClick(View view) {

    }


    public class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewDesc;
        ImageView btnAdd;
        int position;
        User user;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageUser);
            textViewName = itemView.findViewById(R.id.nameUser);
            textViewDesc = itemView.findViewById(R.id.descUser);
            btnAdd = itemView.findViewById(R.id.btnAddFriend);
        }
    }
}
