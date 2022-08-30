package com.team7.app_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.User;

import java.util.List;

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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (currentUser != null) {
                         userRepository.getByEmail(currentUser.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                            try {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                User user = list.get(0).toObject(User.class);
                                user.setId(currentUser.getUid());
                                user.setEmail(currentUser.getEmail());
                                CurrentUser.user = user;
                                Intent it;
                                if (user.isFirstTime()) {
                                    it = new Intent(SplashActivity.this, SetupProfileActivity.class);
                                } else {
                                    it = new Intent(SplashActivity.this, MainActivity.class);
                                }
                                startActivity(it);
                            }catch (Exception e){
                                Exception a = e;
                            }

                        });
                    } else {
                        CurrentUser.user = new User();
                        startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    }
                }
            }
        };
        thread.start();
    }

}