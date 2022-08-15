package com.team7.app_chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.team7.app_chat.R;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.User;
import com.team7.app_chat.ui.contacts.ItemContainerUser;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ItemContainerBinding> {

    private List<Contact> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public UsersAdapter(Context context, List<Contact> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ItemContainerBinding onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_container_user, parent, false);
        return new ItemContainerBinding(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ItemContainerBinding holder, int position) {
        String nickName = mData.get(position).getNickName();
//        holder.imageContact.setImageResource(animal);
        holder.nameContact.setText(nickName);
        holder.descContact.setText("Recenly");
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ItemContainerBinding extends RecyclerView.ViewHolder implements View.OnClickListener {
        RoundedImageView imageContact;
        TextView nameContact;
        TextView descContact;

        ItemContainerBinding(View itemView) {
            super(itemView);
            imageContact = itemView.findViewById(R.id.imageContact);
            nameContact = itemView.findViewById(R.id.nameContact);
            descContact = itemView.findViewById(R.id.descContact);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Contact getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
