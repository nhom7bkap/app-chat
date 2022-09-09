package com.team7.app_chat.ui.chat;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.RoomChats;
import com.team7.app_chat.models.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


public class UserInformationBottomSheet extends BottomSheetDialogFragment {
  private String userId;
  private DocumentReference userRef;
  private DocumentReference currentUserRef;
  private UserRepository repository;
  private RoomChatRepository roomRepository;

  private ContactModalBottomSheet.INavBottomSheet callback;

  private ImageView avatar;
  private ImageView gender;
  private TextView name;
  private TextView email;
  private TextView birthday;
  private Button btBlock;
  private Button btUnBlock;
  private Button btRemove;
  private Button btAdd;
  private LinearLayout llUnBlock;
  private LinearLayout llBlock;
  private LinearLayout llRemove;
  private LinearLayout llAdd;
  private LinearLayout llChat;


  public UserInformationBottomSheet(String userId, ContactModalBottomSheet.INavBottomSheet callback) {
    this.userId = userId;
    repository = new UserRepository();
    roomRepository = new RoomChatRepository();
    currentUserRef = repository.getDocRf(CurrentUser.user.getId());
    if(callback != null) this.callback = callback;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View uiView = inflater.inflate(R.layout.bottom_sheet_user_information, container, false);

    avatar = uiView.findViewById(R.id.ivAvatar);
    name = uiView.findViewById(R.id.tvName);
    email = uiView.findViewById(R.id.tvEmail);
    birthday = uiView.findViewById(R.id.tvBirthday);
    btBlock = uiView.findViewById(R.id.btBlock);
    btUnBlock = uiView.findViewById(R.id.btUnblock);
    btRemove = uiView.findViewById(R.id.btRemove);
    btAdd = uiView.findViewById(R.id.btAdd);
    llBlock = uiView.findViewById(R.id.llBlock);
    llUnBlock = uiView.findViewById(R.id.llUnBlock);
    llRemove = uiView.findViewById(R.id.llRemove);
    llAdd = uiView.findViewById(R.id.llAdd);
    llChat = uiView.findViewById(R.id.llChat);

    if(callback != null){
      llChat.setVisibility(View.VISIBLE);
      uiView.findViewById(R.id.btChat).setOnClickListener(view -> goToChat());
    } else{
      llChat.setVisibility(View.GONE);
    }

    btRemove.setOnClickListener(view -> {
      repository.removeFriend(userId);
      Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
    });
    btAdd.setOnClickListener(view -> {
      repository.addFriendRequest(userId);
      Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
    });

    return uiView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    currentUserRef.collection("friends").document(userId)
      .addSnapshotListener((value, error) -> {
        if(error != null) return;
        if(value.exists()){
          showAsFriend(value);
        } else{
          showAsStranger();
        }
      });
  }

  private void showAsStranger() {
    llChat.setVisibility(View.GONE);
    repository.getDocRf(userId).get().addOnSuccessListener(snapshot -> {
      llAdd.setVisibility(View.VISIBLE);
      llUnBlock.setVisibility(View.GONE);
      llBlock.setVisibility(View.GONE);
      llRemove.setVisibility(View.GONE);
      User user = snapshot.toObject(User.class);
      setValues(user);
    });
  }

  private void showAsFriend(DocumentSnapshot value) {
    Contact friend = value.toObject(Contact.class);
    llAdd.setVisibility(View.GONE);
    llRemove.setVisibility(View.VISIBLE);
    if(friend.isBlocked()){
      llUnBlock.setVisibility(View.VISIBLE);
      llBlock.setVisibility(View.GONE);

      btUnBlock.setOnClickListener(v -> {
        repository.blockFriend(value.getId(), false);
        dismiss();
      });
    } else{
      llUnBlock.setVisibility(View.GONE);
      llBlock.setVisibility(View.VISIBLE);

      btBlock.setOnClickListener(v -> {
        repository.blockFriend(value.getId(), true);
        dismiss();
      });
    }
    userRef = friend.getUser();
    userRef.get().addOnSuccessListener(snapshot -> {
      User user = snapshot.toObject(User.class);
      setValues(user);
    });
  }

  private void setValues(User user){
    if(getContext() != null) {
      Glide.with(getContext()).load(user.getAvatar()).into(avatar);
    }

    name.setText(user.getFullName());
    email.setText(user.getEmail());
    birthday.setText(new SimpleDateFormat("dd/MM/yyyy").format(user.getDOB()));
  }



  private void goToChat(){

    repository.getChatRoom().get().addOnSuccessListener(snapshot -> {
      List<String> myRoom = snapshot.getDocuments().stream().map(value -> value.getId()).collect(Collectors.toList());
      userRef.collection("chatRooms").get().addOnSuccessListener(snapshot1 -> {
        List<String> friendRoom = snapshot1.getDocuments().stream().map(value -> value.getId()).collect(Collectors.toList());
        myRoom.retainAll(friendRoom);
        AtomicBoolean status = new AtomicBoolean(true);
        for (String id : myRoom){
          roomRepository.get().document(id).get().addOnSuccessListener(documentSnapshot -> {
            RoomChats room = documentSnapshot.toObject(RoomChats.class);
            if(room.getName() == null){
              status.set(false);
              Bundle bundle = new Bundle();
              bundle.putString("id", documentSnapshot.getId());
              callback.navigateToChat(bundle);
              dismiss();
            } else if(myRoom.indexOf(documentSnapshot.getId()) == myRoom.size() - 1 && status.get()){
              goToChatWithoutRoom(userRef.getId());
            }
          });
        }
        if (myRoom.size() == 0){
          goToChatWithoutRoom(userRef.getId());
        }
      });
    });
  }

  public void goToChatWithoutRoom(String userId){
    Bundle bundle = new Bundle();
    bundle.putString("userId", userId);
    callback.navigateToChat(bundle);
    dismiss();
  }
}