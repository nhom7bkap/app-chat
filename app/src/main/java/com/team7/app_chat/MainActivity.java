package com.team7.app_chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.User;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
       loadUser();
    }

    public void loadUser(){
        String id = FirebaseAuth.getInstance().getUid();
        new UserRepository().get(id).addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                user.setId(id);
                CurrentUser.user = user;
            }
        });
    }
}