package com.team7.app_chat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.team7.app_chat.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.team7.app_chat.Util.CurrentUser;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.Message;
import com.team7.app_chat.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static final int TYPE_MESSAGE_RECEIVE = 1;
    private static final int TYPE_MESSAGE_SEND = 2;
    private List<DocumentSnapshot> list;
    private Context context;
    private int member;

    public MessageAdapter(List<DocumentSnapshot> list, Context context, int member) {
        this.list = list;
        this.context = context;
        this.member = member;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MESSAGE_RECEIVE:
                View receive = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message, parent, false);
                return new ReceiveViewHolder(receive);
            case TYPE_MESSAGE_SEND:
                View send = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message, parent, false);
                return new SendViewHolder(send);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = list.get(position).toObject(Message.class);

        if (!message.isNotify()) {
            Date createdDate = message.getCreatedDate();
            if (position > 0) {
                long prevMessTime = list.get(position - 1).toObject(Message.class).getCreatedDate().getTime();
                long diff = createdDate.getTime() - prevMessTime;
                long diffInHour = TimeUnit.MILLISECONDS.toHours(diff);
                if (diffInHour > 1) {
                    holder.time.setVisibility(View.VISIBLE);
                    holder.time.setText(new SimpleDateFormat("MMM dd, yyyy HH:mm").format(createdDate));
                } else {
                    holder.time.setVisibility(View.GONE);
                }
            } else {
                holder.time.setVisibility(View.VISIBLE);
                holder.time.setText(new SimpleDateFormat("MMM dd, yyyy HH:mm").format(createdDate));
            }

            if (message.isFile()) {
                Glide.with(context).load(message.getFileUrl()).into(holder.image);
                holder.message.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
            } else {
                holder.message.setText(message.getText());
                holder.message.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.GONE);
            }

            if (holder instanceof ReceiveViewHolder) {
                String id = message.getSendBy().getId();
                new UserRepository().get(id).addOnSuccessListener(user -> {
                    String path = user.getAvatar();
                    Glide.with(context).load(path).into(((ReceiveViewHolder) holder).avatar);
                    ((ReceiveViewHolder) holder).avatar.setVisibility(View.VISIBLE);
                });
                if (member > 2) {
                    message.getSendBy().get().addOnSuccessListener(documentSnapshot -> {
                        User userChat = documentSnapshot.toObject(User.class);
                        holder.name.setText(userChat.getFullName());
                        holder.name.setVisibility(View.VISIBLE);
                    });
                } else {
                    holder.name.setVisibility(View.GONE);
                }
            }

        } else {
            holder.time.setText(message.getText());
            holder.time.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);
            holder.image.setVisibility(View.GONE);
            if (holder instanceof ReceiveViewHolder) {
                ((ReceiveViewHolder) holder).avatar.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemViewType(int position) {
        User currentUser = CurrentUser.user;
        Message message = list.get(position).toObject(Message.class);
        if (message.getSendBy().getId().equals(currentUser.getId())) {
            return TYPE_MESSAGE_SEND;
        } else {
            return TYPE_MESSAGE_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class SendViewHolder extends MessageViewHolder {
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class ReceiveViewHolder extends MessageViewHolder {
        private ImageView avatar;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatarFriend);
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView time;
        private TextView message;
        private TextView name;
        private ImageView image;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tvTime);
            message = itemView.findViewById(R.id.tvMessage);
            name = itemView.findViewById(R.id.tvName);
            image = itemView.findViewById(R.id.ivImageMessage);
        }
    }
}
