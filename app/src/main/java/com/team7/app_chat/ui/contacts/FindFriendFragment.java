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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.SignInActivity;
import com.team7.app_chat.Util.ContactRepository;
import com.team7.app_chat.Util.FriendRequestRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.UsersAdapter;
import com.team7.app_chat.models.FriendRequest;
import com.team7.app_chat.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FindFriendFragment extends Fragment implements UsersAdapter.SelectListstener {
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FriendRequestRepository requestRepository;
    private UserRepository userRepository;
    private UsersAdapter usersAdapter;
    private ArrayList<User> userArrayList;
    private FirebaseUser currentUser;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private View mView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_find_friend,
                container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRepository = new UserRepository();
        mProgressBar = mView.findViewById(R.id.findFriendProgressBar);
        mTextView = mView.findViewById(R.id.txtFindFriendNotFound);
        requestRepository = new FriendRequestRepository(currentUser.getUid());
        this.recyclerView = mView.getRootView().findViewById(R.id.recyclerViewUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        userArrayList = new ArrayList<User>();
        usersAdapter = new UsersAdapter(this.getContext(), userArrayList, this);
        recyclerView.setAdapter(usersAdapter);
        initialFun();
        return mView;
    }

    public void initialFun() {
        userRepository.get().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mProgressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    UserRepository repository = new UserRepository();
                    repository.get().addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.e("FirestoreError", error.getMessage());
                            }

                            for (DocumentChange doc : value.getDocumentChanges()) {
                                User user = doc.getDocument().toObject(User.class);
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    ContactRepository contactRepository = new ContactRepository(user.getId());
                                    FriendRequestRepository requestRepository = new FriendRequestRepository(user.getId());
//                    ExecutorService es = Executors.newFixedThreadPool(5);
                                    contactRepository.exists(currentUser.getUid()).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean aBoolean) {
                                            if (!aBoolean) {
                                                requestRepository.exists(currentUser.getUid()).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                                    @Override
                                                    public void onSuccess(Boolean aBoolean) {
                                                        if (!aBoolean) {
                                                            if (!user.getEmail().equals(currentUser.getEmail())) {
                                                                userArrayList.add(doc.getDocument().toObject(User.class));
                                                            }
                                                            usersAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }else if(task.isCanceled()) {
                    recyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTextView.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onItemClicked(User user) {
        FriendRequest friendRequest = new FriendRequest();
        DocumentReference drf = userRepository.getDocRf(currentUser.getUid());
        friendRequest.setSender(drf);
        friendRequest.setSenderId(currentUser.getUid());
        friendRequest.setReceiverId(user.getId());
        friendRequest.setCreated_at(new Date());
        requestRepository.create(friendRequest);
        Toast.makeText(mView.getContext(), "Request has send!",
                Toast.LENGTH_SHORT).show();
    }
}