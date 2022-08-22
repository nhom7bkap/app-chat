package com.team7.app_chat.Util;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.team7.app_chat.models.User;

public class CurrentUser {
    public static User user;

    static {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        new UserRepository().get(mAuth.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                user = user;
            }
        });
    }
}
