package com.team7.app_chat.ui.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.ContactRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.ContactAdapter;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.RoomChat;
import com.team7.app_chat.models.User;

import java.util.ArrayList;


public class ContactsFragment extends Fragment implements ContactAdapter.ContactSelectListstener {

    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private ContactAdapter contactAdapter;
    private ArrayList<Contact> contactArrayList;
    private FirebaseUser currentUser;
    private View mView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contacts,
                container, false);
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.recyclerView = mView.getRootView().findViewById(R.id.recyclerViewContact);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        this.contactArrayList = new ArrayList<Contact>();
        this.contactAdapter = new ContactAdapter(this.getContext(), contactArrayList,this);
        this.recyclerView.setAdapter(contactAdapter);
        initialFun();
        mView.findViewById(R.id.imAddFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(ContactsFragment.this).navigate(R.id.action_home_to_add_friend);
            }
        });

        return mView;
    }

    public void initialFun() {
        ContactRepository repository = new ContactRepository(currentUser.getUid());
        repository.get().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("FirestoreError", error.getMessage());
                }
                for (DocumentChange doc : value.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        contactArrayList.add(doc.getDocument().toObject(Contact.class));
                    }
                    contactAdapter.notifyDataSetChanged();
                }
            }
        });
//        repository.getContacts(currentUser.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
//            List<Contact> list = queryDocumentSnapshots.getDocuments()
//                    .stream()
//                    .map(documentSnapshot -> {
//                        return documentSnapshot.toObject(Contact.class);
//                    })
//                    .collect(Collectors.toList());
//        });

    }

    @Override
    public void onClick(User user) {
//        RoomChat roomChat = new RoomChat()
    }
}