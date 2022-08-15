package com.team7.app_chat.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.adapters.UsersAdapter;
import com.team7.app_chat.models.Contact;

import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment implements UsersAdapter.ItemClickListener {

    private FirebaseAuth mAuth;
    private UsersAdapter usersAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,
                container, false);

        mAuth = FirebaseAuth.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.listContacts);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore.getInstance().collection("User").document(currentUser.getUid()).collection("contacts").get().addOnCompleteListener(task -> {
            List<Contact> list = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Contact contact = document.toObject(Contact.class);
                    list.add(contact);
                }
                usersAdapter = new UsersAdapter(view.getContext(), list);
                recyclerView.setAdapter(usersAdapter);
            }
        });

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}