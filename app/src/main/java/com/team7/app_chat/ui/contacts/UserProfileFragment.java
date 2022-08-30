package com.team7.app_chat.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.User;

public class UserProfileFragment extends Fragment {
    private String friendId;
    private String currentUserId;
    private View mView;
    private User user;
    private Boolean isBlock;
    private Button btnBlock;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendId = getArguments().getString("userId");
        currentUserId = FirebaseAuth.getInstance().getUid();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_user_profiles,
                container, false);
        btnBlock = mView.findViewById(R.id.btnBlock);
        new UserRepository().getDocRf(friendId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) return;
                user = value.toObject(User.class);
                Glide.with(mView).load(user.getAvatar()).into(((ImageView) mView.findViewById(R.id.imageUser)));
                ((TextView) mView.findViewById(R.id.txtName)).setText(user.getFullName());
                ((TextView) mView.findViewById(R.id.txtEmail)).setText(user.getEmail());
            }
        });
        new UserRepository().get().document(currentUserId).collection("contacts").document(friendId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.toObject(Contact.class).isBlocked()){
                    isBlock = documentSnapshot.toObject(Contact.class).isBlocked();
                        btnBlock.setText("UnBlock");
                }
            }
        });


        mView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserProfileFragment.this).popBackStack();
            }
        });

        mView.findViewById(R.id.btnBlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UserRepository().blockFriend(friendId, !isBlock);
                NavHostFragment.findNavController(UserProfileFragment.this).popBackStack();
            }
        });

        mView.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UserRepository().removeFriend(friendId);
                NavHostFragment.findNavController(UserProfileFragment.this).popBackStack();
            }
        });


        return mView;
    }

}
