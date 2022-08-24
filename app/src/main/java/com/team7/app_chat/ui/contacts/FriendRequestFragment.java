package com.team7.app_chat.ui.contacts;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.ContactRepository;
import com.team7.app_chat.Util.FriendRequestRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.FriendRequestAdapter;
import com.team7.app_chat.adapters.UsersAdapter;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.FriendRequest;
import com.team7.app_chat.models.User;

import java.util.ArrayList;
import java.util.Date;

public class FriendRequestFragment extends Fragment implements FriendRequestAdapter.SelectListstener {
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FriendRequestAdapter requestAdapter;
    private ContactRepository contactRepository;
    private UserRepository userRepository;
    private FriendRequestRepository requestRepository;
    private ArrayList<FriendRequest> requestArrayList;
    private FirebaseUser currentUser;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private View mView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friend_request,
                container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        this.recyclerView = mView.getRootView().findViewById(R.id.recyclerViewRequest);
        mProgressBar = mView.findViewById(R.id.requestProgressBar);
        mTextView = mView.findViewById(R.id.txtRequestNotFound);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        requestArrayList = new ArrayList<FriendRequest>();
        requestAdapter = new FriendRequestAdapter(this.getContext(), requestArrayList, this);
        recyclerView.setAdapter(requestAdapter);
        initialFun();
        return mView;
    }

    public void initialFun() {
        requestRepository = new FriendRequestRepository(currentUser.getUid());
        requestRepository.get().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mProgressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    requestRepository.get().addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.e("FirestoreError", error.getMessage());
                            }

                            for (DocumentChange doc : value.getDocumentChanges()) {
                                FriendRequest fRequest = doc.getDocument().toObject(FriendRequest.class);
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    requestArrayList.add(fRequest);
                                }
                                requestAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                } else {
                    recyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onAccept(FriendRequest friendRequest, int position) {
        requestArrayList.remove(position);
        requestAdapter.notifyDataSetChanged();
        friendRequest.getSender().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);
                contactRepository = new ContactRepository(user.getId());
                userRepository = new UserRepository();
                Contact contact = new Contact();
                // add contact to sender user
                DocumentReference Docf = userRepository.getDocRf(currentUser.getUid());
                contact.setUser(Docf);
                contact.setCreated(new Date());
                contactRepository.create(contact,currentUser.getUid());
                // add contact to current user
                contactRepository = new ContactRepository(currentUser.getUid());
                Docf = userRepository.getDocRf(user.getId());
                contact.setUser(Docf);
                contactRepository.create(contact,user.getId());
                requestRepository = new FriendRequestRepository(currentUser.getUid());
                requestRepository.delete(friendRequest.getSenderId());
            }
        });
        Toast.makeText(mView.getContext(), "Accepted",
                Toast.LENGTH_SHORT).show();
        initialFun();
    }

    @Override
    public void onDeAccept(FriendRequest friendRequest, int position) {
        requestArrayList.remove(position);
        requestAdapter.notifyDataSetChanged();
        friendRequest.getSender().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                requestRepository = new FriendRequestRepository(currentUser.getUid());
                requestRepository.delete(friendRequest.getSenderId());
            }
        });
        Toast.makeText(mView.getContext(), "DeAccept",
                Toast.LENGTH_SHORT).show();
        initialFun();
    }
}