package com.team7.app_chat.ui.contacts;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.ContactAdapter;
import com.team7.app_chat.adapters.PersonAdapter;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.Member;
import com.team7.app_chat.models.Message;
import com.team7.app_chat.models.RoomChats;
import com.team7.app_chat.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddMemberFragment extends Fragment implements PersonAdapter.IPerson, ContactAdapter.INavChat {
  private UserRepository repository;
  private List<DocumentSnapshot> contactList;
  private List<DocumentSnapshot> personList;
  private ContactAdapter adapter;
  private PersonAdapter personAdapter;
  private User currentUser;
  private RecyclerView recyclerView;
  private RecyclerView rvPerson;
  private RoomChatRepository roomRepository;
  private DocumentReference currentUserRef;
  private RoomChats chatRoom;
  private Uri avatarUri;
  private StorageReference storageRef;
  private View mView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    repository = new UserRepository();
    currentUser = CurrentUser.user;
    roomRepository = new RoomChatRepository();
    storageRef = FirebaseStorage.getInstance().getReference();
    repository.getDocRf(currentUser.getId()).addSnapshotListener((value, error) -> {
      currentUserRef = value.getReference();
    });

    chatRoom = new RoomChats();
    Bundle args = getArguments();
    chatRoom.setPublic(args.getBoolean("status"));
    if(args.getString("avatarPath") != null){
      avatarUri = Uri.fromFile(new File(args.getString("avatarPath")));
      String fileName = String.valueOf(System.currentTimeMillis());
      StorageReference imgRef = storageRef.child("avatars/" + fileName);
      imgRef.putFile(avatarUri).addOnSuccessListener(taskSnapshot -> {
        imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
          chatRoom.setAvatar(uri.toString());
        });
      });
    } else {
      chatRoom.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-app-f1361.appspot.com/o/avatars%2Fgroup_default.png?alt=media&token=f3e65e3d-7510-4a09-9496-62d649bb366a");
    }
    chatRoom.setName(args.getString("name"));

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_add_member, container, false);
    recyclerView = mView.findViewById(R.id.rvFriend);
    rvPerson = mView.findViewById(R.id.rvPerson);
    mView.findViewById(R.id.btCreate).setOnClickListener(view -> {
      if(personList.size() < 2){
        Toast.makeText(getContext(), "Choose at least 2 person", Toast.LENGTH_SHORT).show();
      } else{
        roomRepository.get().add(chatRoom).addOnSuccessListener(documentReference -> {
          Map<String, Object> room = new HashMap<>();
          room.put("room", documentReference);
          room.put("updatedAt", new Date());
          for (DocumentSnapshot doc: personList) {
            Contact contact = doc.toObject(Contact.class);
            String id = contact.getUser().getId();
            Member member = new Member();
            member.setMod(false);
            member.setUser(repository.getDocRf(id));
            documentReference.collection("members").document(id).set(member);
            contact.getUser().collection("chatRoom").document(documentReference.getId()).set(room);
          }
          Member me = new Member();
          me.setMod(true);
          me.setUser(currentUserRef);
          documentReference.collection("members").document(currentUser.getId()).set(me);
          currentUserRef.collection("chatRoom").document(documentReference.getId()).set(room);

          Message message = new Message();
          message.setText(currentUser.getFullName() + " created chat room");
          message.setNotify(true);
          message.setCreatedDate(new Date());
          message.setSendBy(currentUserRef);
          documentReference.collection("messages").add(message).addOnSuccessListener(reference -> {
            documentReference.update("lastMessage", reference);
            Bundle bundle = new Bundle();
            bundle.putString("id", documentReference.getId());
            NavHostFragment.findNavController(this).navigate(R.id.action_createRoomFragment_to_chatFragment, bundle);
          });
        });
      }
    });
    return mView;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);
    repository.getDocRf(currentUser.getId()).get().addOnSuccessListener(documentSnapshot -> {
      documentSnapshot.getReference().collection("contacts").get().addOnSuccessListener(snapshot -> {
        contactList = snapshot.getDocuments();
        adapter = new ContactAdapter(mView.getContext(),contactList, this);
        recyclerView.setAdapter(adapter);
      });
    });

    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
    float dpWidth = displayMetrics.widthPixels / displayMetrics.density - 40;
    int itemNumb = (int) (dpWidth / 70);
    personList =  new ArrayList<>();
    personAdapter = new PersonAdapter(personList, this);
    rvPerson.setLayoutManager(new GridLayoutManager(getContext(), itemNumb));
    rvPerson.setAdapter(personAdapter);
  }

  @Override
  public void remove(DocumentSnapshot friend) {
    int index = personList.indexOf(friend);
    personList.remove(index);
    personAdapter.notifyItemRemoved(index);
    index = contactList.indexOf(friend);
    contactList.add(friend);
    adapter.notifyItemRemoved(index);
  }

  @Override
  public void goToChat(DocumentSnapshot doc) {
    if(!personList.contains(doc)){
      personList.add(doc);
      personAdapter.notifyDataSetChanged();
      contactList.remove(doc);
      adapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onHold(DocumentSnapshot doc) {

  }
}