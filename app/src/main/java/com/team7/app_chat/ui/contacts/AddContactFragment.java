package com.team7.app_chat.ui.contacts;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.SplashActivity;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.ContactAdapter;
import com.team7.app_chat.adapters.UsersAdapter;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.User;

import java.util.ArrayList;

public class AddContactFragment extends Fragment implements UsersAdapter.SelectListstener {
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private UsersAdapter usersAdapter;
    private ArrayList<User> userArrayList;
    private FirebaseUser currentUser;
    private View mView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_contact,
                container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        this.recyclerView = mView.getRootView().findViewById(R.id.recyclerViewUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));

        userArrayList = new ArrayList<User>();
        usersAdapter = new UsersAdapter(this.getContext(), userArrayList,this);
        recyclerView.setAdapter(usersAdapter);
        initialFun();

        mView.findViewById(R.id.imAFback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AddContactFragment.this).navigate(R.id.action_add_contact_to_home);
            }
        });
        return mView;
    }

    public void initialFun() {
        UserRepository repository = new UserRepository();
        repository.get().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("FirestoreError", error.getMessage());
                }

                for (DocumentChange doc : value.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Log.e("User", doc.getDocument().toObject(User.class).getEmail());
                        userArrayList.add(doc.getDocument().toObject(User.class));
                    }
                    usersAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onItemClicked(User user) {
        Toast.makeText(mView.getContext(), user.getEmail(),
                Toast.LENGTH_SHORT).show();
    }
}