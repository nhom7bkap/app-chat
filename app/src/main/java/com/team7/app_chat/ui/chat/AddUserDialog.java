package com.team7.app_chat.ui.chat;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.Util.UserRepository;

import java.util.List;


public class AddUserDialog extends DialogFragment {
  private UserRepository repository;
  private RoomChatRepository roomRepository;
  private String roomId;
  private TextInputLayout layoutEmail;

  public AddUserDialog(String roomId) {
    this.roomId = roomId;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    repository = new UserRepository();
    roomRepository = new RoomChatRepository();
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View dialog = inflater.inflate(R.layout.dialog_add_user, container);
    dialog.findViewById(R.id.btAdd).setOnClickListener(view -> {
      String email = ((EditText) dialog.findViewById(R.id.etEmail)).getText().toString();
      layoutEmail = dialog.findViewById(R.id.lEmail);

      repository.getByEmail(email).get().addOnSuccessListener(snapshot -> {
        List<DocumentSnapshot> list = snapshot.getDocuments();
        if(list.size() > 0){
          checkUser(list.get(0));
          ((TextInputLayout) dialog.findViewById(R.id.lEmail)).setError("");
//
        } else{
          ((TextInputLayout) dialog.findViewById(R.id.lEmail)).setError("This user does not exist");
        }
      });
    });
    return dialog;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void checkUser(DocumentSnapshot doc){
    DocumentReference roomRef = roomRepository.get().document(roomId);
    roomRef.collection("members")
        .whereEqualTo("user", doc.getReference())
        .get().addOnSuccessListener(snapshot -> {
          if(snapshot.size() > 0){
            layoutEmail.setError("This user already in this room");
          } else{
            roomRepository.addMember(roomId, doc);
            Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
            dismiss();
          }
        });
  }
}