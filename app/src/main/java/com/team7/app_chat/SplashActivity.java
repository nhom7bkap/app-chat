package com.team7.app_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.User;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = "SlashActivity";
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkAuth();
        userRepository = new UserRepository();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuth();
    }

    public void checkAuth() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (currentUser != null) {
                        userRepository.get(currentUser.getUid()).addOnSuccessListener(user -> {
                            CurrentUser.user.setId(currentUser.getUid());
                            CurrentUser.user.setEmail(currentUser.getEmail());
                            Intent it;
                            if (user.isFirstTime()) {
                                it = new Intent(SplashActivity.this, SetupProfileActivity.class);
                            } else {
                                it = new Intent(SplashActivity.this, MainActivity.class);
                            }
                            startActivity(it);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(SplashActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "CurrentUser Error: " + e.getMessage());
                        }).addOnCompleteListener(new OnCompleteListener<User>() {
                            @Override
                            public void onComplete(@NonNull Task<User> task) {
                                Log.e(TAG, "User " + task.getResult().getFullName());
                            }
                        });
                    } else {
                        startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    }
                }
            }
        };
        thread.start();
    }

}