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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
import java.util.Optional;

public class ChatRoomFragment extends Fragment implements ChatRoomAdapter.IChatScreen {

    private ChatRoomAdapter chatRoomAdapter;
    private List<DocumentSnapshot> list;
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
        userRepository.getChatRoom().orderBy("updatedAt").addSnapshotListener((value, error) -> {
            if(error != null) return;
            for (DocumentChange doc: value.getDocumentChanges()
            ) {
                switch (doc.getType()){
                    case ADDED:
                        DocumentSnapshot element = doc.getDocument();
                        if(!list.contains(element)){
                            list.add(0,element);
                            chatRoomAdapter.notifyItemInserted(0);
                        }
                        break;
                    case MODIFIED:
                        QueryDocumentSnapshot element2 = doc.getDocument();
                        Optional<DocumentSnapshot> optional0 = list.stream().filter(val -> val.getId().equals(element2.getId())).findFirst();
                        if (optional0.isPresent()){
                            int index = list.indexOf(optional0.get());
                            list.remove(index);
                            list.add(0, element2);
                            chatRoomAdapter.notifyItemMoved(index, 0);
                        }
                        break;
                    case REMOVED:
                        QueryDocumentSnapshot element3 = doc.getDocument();
                        Optional<DocumentSnapshot> optional = list.stream().filter(val -> val.getId().equals(element3.getId())).findFirst();
                        if(optional.isPresent()){
                            int index = list.indexOf(optional.get());
                            list.remove(index);
                            chatRoomAdapter.notifyItemRemoved(index);
                        }
                        break;
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