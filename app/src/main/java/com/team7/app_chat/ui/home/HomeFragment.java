package com.team7.app_chat.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.SignInActivity;
import com.team7.app_chat.Util.FirestoreRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.UsersAdapter;
import com.team7.app_chat.components.ProgressButton;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements UserRepository.IContactCallback {

    private FirebaseAuth mAuth;
    private UsersAdapter usersAdapter;
    private final List<Contact> mListContact = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,
                container, false);

        mAuth = FirebaseAuth.getInstance();

        ((TextView) view.findViewById(R.id.textHome)).setText("Hello world");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.e("currentUser", currentUser.getEmail());
        } else {
            Log.e("currentUser", currentUser.getEmail());
        }
//        List<Contact> lstContact = new ArrayList<>();
//        UserRepository repository = new UserRepository();
//       repository.getContactsWithCallback(currentUser.getUid());
//        new UserRepository( this).getContactsWithCallback(currentUser.getUid());
    }


    @Override
    public void loadContact(List<Contact> list) {
        // Nó trả về listcontact ở đây
        this.mListContact.addAll(list);
//        List<Contact> a = list;
    }
}