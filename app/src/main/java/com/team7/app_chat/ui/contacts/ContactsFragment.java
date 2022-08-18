package com.team7.app_chat.ui.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.components.ContactAdapter;
import com.team7.app_chat.models.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ContactsFragment extends Fragment {

    RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private ContactAdapter contactAdapter;
    ArrayList<Contact> contactArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts,
                container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        this.recyclerView = view.getRootView().findViewById(R.id.recyclerViewContact);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        contactArrayList = new ArrayList<Contact>();
        contactAdapter = new ContactAdapter(this.getContext(), contactArrayList);
        recyclerView.setAdapter(contactAdapter);

        UserRepository repository = new UserRepository();
        repository.getContacts(currentUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        return view;
    }

}