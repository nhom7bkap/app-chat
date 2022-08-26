package com.team7.app_chat.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.ChatRoomAdapter;
import com.team7.app_chat.models.RoomChat;
import com.team7.app_chat.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomFragment extends Fragment implements ChatRoomAdapter.IChatScreen {

    private ChatRoomAdapter chatRoomAdapter;
    private List<DocumentReference> list;
    private View mView;
    private RecyclerView recyclerView;
    private User currentUser;
    private RoomChatRepository roomChatRepository;
    private UserRepository userRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomChatRepository = new RoomChatRepository();
        userRepository = new UserRepository();
        currentUser = CurrentUser.user;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chat_room,
                container, false);
        recyclerView = mView.findViewById(R.id.recyclerViewChatRoom);
        loadRoomChat();
        return mView;
    }

    public void loadRoomChat() {
        Log.e("load", "Room");
        list = new ArrayList<>();
        chatRoomAdapter = new ChatRoomAdapter(getContext(), list, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatRoomAdapter);
        userRepository.getChatRoom().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                snapshot.getDocuments();
                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    switch ((dc.getType())) {
                        case ADDED:
                            Log.e("Room", "Room change");
                            list.add(dc.getDocument().toObject(RoomChat.class).getRoom());
                            chatRoomAdapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });

    }


    @Override
    public void goToChatScreen(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_home_to_chat, bundle);
    }
}