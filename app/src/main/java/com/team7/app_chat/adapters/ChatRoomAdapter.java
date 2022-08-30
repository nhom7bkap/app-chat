package com.team7.app_chat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.models.Member;
import com.team7.app_chat.models.Message;
import com.team7.app_chat.models.RoomChats;
import com.team7.app_chat.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {
    private Context context;
    private List<DocumentSnapshot> list;
    private IChatScreen callback;

    public interface IChatScreen {
        void goToChatScreen(String id);
    }

    public ChatRoomAdapter(Context context, List<DocumentSnapshot> list, IChatScreen callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_container_chat_room, parent, false);
        return new ViewHolder(item);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snapshot = list.get(position);
        DocumentReference doc = (DocumentReference) snapshot.get("room");
        doc.addSnapshotListener((value, error) -> {
            if(error != null){
                return;
            }
            assert value != null;
            setData(value, holder);
        });
    }

    @SuppressLint("ResourceAsColor")
    private void setData(DocumentSnapshot doc, ViewHolder holder) {
        RoomChats chatRoom = doc.toObject(RoomChats.class);
        User currentUser = CurrentUser.user;
        if (chatRoom.getLastMessage() != null) {
            chatRoom.getLastMessage().get().addOnSuccessListener(documentSnapshot -> {
                String time;
                Message message = documentSnapshot.toObject(Message.class);
                Date createdDate = message.getCreatedDate();
                long diff = System.currentTimeMillis() - createdDate.getTime();
                long diff_in_hour = TimeUnit.MILLISECONDS.toHours(diff);
                long diff_in_year = TimeUnit.MILLISECONDS.toDays(diff) / 365;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(createdDate);
                if (diff_in_hour < 24) {
                    time = new SimpleDateFormat("HH:mm").format(createdDate);

                } else if (diff_in_hour > 24 && diff_in_year < 1) {
                    time = new SimpleDateFormat("MMM dd").format(createdDate);
                } else {
                    time = new SimpleDateFormat("MMM dd, yyyy").format(createdDate);
                }
                if (message.getViewer() != null && message.getViewer().size() > 0) {
                    boolean seen = message.getViewer().stream().filter(viewer -> viewer.getId().equals(currentUser.getId())).count() == 0;
                    if (seen) {
                        int color = ContextCompat.getColor(context, R.color.black);
                        holder.time.setTextColor(color);
                        holder.message.setTextColor(color);
                        holder.name.setTextColor(color);

                    }
                }
                holder.message.setText(message.getText());
                if (message.getText() != null && !message.isFile()){
                    if (message.getText().length() > 30){
                        holder.message.setText(message.getText().substring(0,30)+"....");
                    }
                }
                holder.time.setText(time);
            });
        }

        if (chatRoom.getName() == null) {
            doc.getReference().collection("members").get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<DocumentSnapshot> memberList = queryDocumentSnapshots.getDocuments();
                memberList.forEach(member -> {
                    member.toObject(Member.class).getUser().get().addOnSuccessListener(user1 -> {
                        if (!user1.getId().equals(currentUser.getId())) {
                            Log.e("tag", "compare");
                            User userChat = user1.toObject(User.class);
                            String fullName = userChat.getFullName();
                            holder.name.setText(fullName);
                            Glide.with(context).load(userChat.getAvatar()).into(holder.avatar);
                        }
                    });
                });
            });
        } else {
            holder.name.setText(chatRoom.getName());
            Glide.with(context).load(chatRoom.getAvatar()).into(holder.avatar);
        }

        holder.layout.setOnClickListener(view -> {
            callback.goToChatScreen(doc.getId());
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private TextView message;
        private TextView time;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.roomAvatar);
            name = itemView.findViewById(R.id.roomName);
            message = itemView.findViewById(R.id.lastMessage);
            time = itemView.findViewById(R.id.lastMessageTime);
            layout = itemView.findViewById(R.id.layoutContactItem);
        }
    }
}
