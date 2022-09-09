package com.team7.app_chat.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.models.RoomChats;
import com.team7.app_chat.ui.contacts.UserProfileFragment;


public class ChangeInfoFragment extends Fragment {
    private DocumentReference roomRef;
    private RoomChatRepository roomRepository;
    private String roomId;
    private TextInputLayout layoutName;
    private EditText edName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomRepository = new RoomChatRepository();
        roomId = getArguments().getString("roomId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View infoView = inflater.inflate(R.layout.fragment_change_info, container, false);
        layoutName = infoView.findViewById(R.id.lRoomName);
        edName = infoView.findViewById(R.id.etRoomName);
        infoView.findViewById(R.id.btUpdate).setOnClickListener(view -> {
            String name = edName.getText().toString().trim();
            updateData(name);
        });
        infoView.findViewById(R.id.btnBackChat7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ChangeInfoFragment.this).popBackStack();
            }
        });
        return infoView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomRepository.get().document(roomId).get().addOnSuccessListener(documentSnapshot -> {
            roomRef = documentSnapshot.getReference();
            RoomChats room = documentSnapshot.toObject(RoomChats.class);
            edName.setText(room.getName());
        });
    }

    private void updateData(String name) {
        if (name.length() == 0) {
            layoutName.setError("Required");
        }
        roomRepository.get().whereEqualTo(name, name).get().addOnSuccessListener(snapshot -> {
            if (snapshot.size() > 0 && !snapshot.getDocuments().get(0).getId().equals(roomRef.getId())) {
                layoutName.setError("This name already exists");
            } else {
                roomRef.update("name", name);
                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            }
        });
    }
}