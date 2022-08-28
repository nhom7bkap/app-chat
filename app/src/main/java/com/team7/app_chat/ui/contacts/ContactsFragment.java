package com.team7.app_chat.ui.contacts;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.team7.app_chat.R;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.Util.ContactRepository;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.ContactAdapter;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.RoomChats;
import com.team7.app_chat.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ContactsFragment extends Fragment implements ContactAdapter.INavChat {

    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private ContactAdapter contactAdapter;
    private List<DocumentSnapshot> contactList;
    private User currentUser;
    private DocumentReference currentUserRef;
    private View mView;
    private UserRepository userRepository;
    private RoomChatRepository roomRepository;
    private User userContact;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository();
        roomRepository = new RoomChatRepository();
        this.mAuth = FirebaseAuth.getInstance();
        currentUser = CurrentUser.user;
        userRepository.getDocRf(currentUser.getId()).addSnapshotListener((value, error) -> {
            currentUserRef = value.getReference();
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contacts,
                container, false);
        this.recyclerView = mView.getRootView().findViewById(R.id.recyclerViewContact);
        mView.findViewById(R.id.imAddFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ContactsFragment.this).navigate(R.id.action_home_to_add_friend);
            }
        });

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(view.getContext(), contactList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(contactAdapter);


        userRepository.getDocRf(currentUser.getId()).get().addOnSuccessListener(documentSnapshot -> {
            documentSnapshot.getReference().collection("contacts").addSnapshotListener((value, error) -> {
                if (error != null) return;
                for (DocumentChange doc : value.getDocumentChanges()) {
                    switch (doc.getType()) {
                        case ADDED:
                            int count = (int) contactList.stream().filter(val -> val.getId().equals(doc.getDocument().getId())).count();
                            if (count == 0) contactList.add(doc.getDocument());
                            break;
                        case REMOVED:
                            DocumentSnapshot element = contactList.stream().filter(val -> val.getId().equals(doc.getDocument().getId())).findFirst().get();
                            contactList.remove(element);
                            break;
                    }
                    contactAdapter.notifyDataSetChanged();
                }
            });
            documentSnapshot.getReference().collection("friend_request").addSnapshotListener((value, error) -> {
                if (error != null) return;
                int count = value.size();
                TextView tvCount = view.findViewById(R.id.tvRequestCount);
                if (count > 0) {
                    tvCount.setText(String.valueOf(count));
                    tvCount.setVisibility(View.VISIBLE);
                } else {
                    tvCount.setVisibility(View.GONE);
                }
            });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void goToChat(DocumentSnapshot doc) {
        DocumentReference user = doc.toObject(Contact.class).getUser();
        userRepository.getChatRoom().get().addOnSuccessListener(snapshot -> {
            List<String> myRoom = snapshot.getDocuments().stream().map(value -> value.getId()).collect(Collectors.toList());
            user.collection("chatRoom").get().addOnSuccessListener(snapshot1 -> {
                List<String> friendRoom = snapshot1.getDocuments().stream().map(value -> value.getId()).collect(Collectors.toList());
                myRoom.retainAll(friendRoom);
                for (String id : myRoom) {
                    roomRepository.get().document(id).get().addOnSuccessListener(documentSnapshot -> {
                        RoomChats room = documentSnapshot.toObject(RoomChats.class);
                        if (room.getName() == null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("id", documentSnapshot.getId());
                            NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_home_to_chat, bundle);
                        }
                    });
                }
                Bundle bundle = new Bundle();
                bundle.putString("userId", user.getId());
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_home_to_chat, bundle);
            });
        });

    }

    public void showBottomSheet(DocumentSnapshot doc) {
        final Dialog dialog = new Dialog(mView.getContext());
        Contact contact = doc.toObject(Contact.class);
        contact.getUser().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userContact = value.toObject(User.class);
                ((TextView) dialog.findViewById(R.id.contactName)).setText(userContact.getFullName());
            }
        });
        if (contact.getNickName() != null)
            ((TextView) dialog.findViewById(R.id.contactName)).setText(contact.getNickName());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_detail_contact);

        LinearLayout viewLayout = dialog.findViewById(R.id.layoutViewProfile);
        LinearLayout editLayout = dialog.findViewById(R.id.layoutEditContact);
        LinearLayout deleteLayout = dialog.findViewById(R.id.layoutDelete);


        viewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ContactRepository(currentUser.getId()).delete(doc.getId());
                new ContactRepository(doc.getId()).delete(currentUser.getId());
                dialog.dismiss();
                Toast.makeText(mView.getContext(), "Deleted contact", Toast.LENGTH_SHORT).show();

            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onHold(DocumentSnapshot doc) {
        showBottomSheet(doc);
    }
}