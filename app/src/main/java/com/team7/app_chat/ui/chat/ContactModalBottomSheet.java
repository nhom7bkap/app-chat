package com.team7.app_chat.ui.chat;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.RoomChats;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ContactModalBottomSheet extends BottomSheetDialogFragment {
  private UserRepository repository;
  private RoomChatRepository roomRepository;
  private DocumentSnapshot friendSnapshot;
  private DocumentReference userRef;

  private INavBottomSheet callback;

  public interface INavBottomSheet{
    void navigateToChat(Bundle bundle);
  }

  public ContactModalBottomSheet(DocumentSnapshot friendSnapshot, INavBottomSheet callback) {
    this.friendSnapshot = friendSnapshot;
    repository = new UserRepository();
    roomRepository = new RoomChatRepository();
    userRef = friendSnapshot.toObject(Contact.class).getUser();
    this.callback = callback;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View bsView = inflater.inflate(R.layout.bottom_sheet_contact_modal, container, false);
    LinearLayout bottomSheet = bsView.findViewById(R.id.bsFriend);
    BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    bsView.findViewById(R.id.llChat).setOnClickListener(view -> goToChat());
    bsView.findViewById(R.id.llViewInfo).setOnClickListener(view -> {
      UserInformationBottomSheet informationBottomSheet = new UserInformationBottomSheet(friendSnapshot.getId(), callback);
      informationBottomSheet.show(getActivity().getSupportFragmentManager(), null);
      dismiss();
    });

    Contact friend = friendSnapshot.toObject(Contact.class);
    LinearLayout unblock = bsView.findViewById(R.id.llUnBlock);
    LinearLayout block = bsView.findViewById(R.id.llBlock);
    if(friend.isBlocked()){
      block.setVisibility(View.GONE);
      unblock.setVisibility(View.VISIBLE);
      unblock.setOnClickListener(view -> {
        repository.blockFriend(userRef.getId(), false)
            .addOnSuccessListener(unused -> {
              if(getContext() != null) Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
              dismiss();
            });
        dismiss();
      });
    } else {
      unblock.setVisibility(View.GONE);
      block.setVisibility(View.VISIBLE);
      block.setOnClickListener(view -> {
        repository.blockFriend(userRef.getId(), true)
            .addOnSuccessListener(unused -> {
              if(getContext() != null) Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
              dismiss();
            });
      });
    }

    bsView.findViewById(R.id.llRemove).setOnClickListener(view -> {
      repository.removeFriend(userRef.getId());
      if(getContext() != null) Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
      dismiss();
    });
    return bsView;
  }
  @RequiresApi(api = Build.VERSION_CODES.N)
  private void goToChat(){
    DocumentReference user = friendSnapshot.toObject(Contact.class).getUser();

    repository.getChatRoom().get().addOnSuccessListener(snapshot -> {
      List<String> myRoom = snapshot.getDocuments().stream().map(value -> value.getId()).collect(Collectors.toList());
      user.collection("chatRooms").get().addOnSuccessListener(snapshot1 -> {
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
              goToChatWithoutRoom(user.getId());
            }
          });
        }
        if (myRoom.size() == 0){
          goToChatWithoutRoom(user.getId());
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