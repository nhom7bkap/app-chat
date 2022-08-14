package com.team7.app_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team7.app_chat.models.User;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingActivity extends AppCompatActivity {
    private TextView textViewEmail;
    StorageReference storageReference;
    FirebaseAuth firebaseProfile;

    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        textViewEmail = findViewById(R.id.txtEmail);
        btnLogin = findViewById(R.id.btnLogout);
        firebaseProfile = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        showUserProfile();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        StorageReference profileRef = storageReference.child("users").child(firebaseProfile.getCurrentUser().getUid()).child("profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                Picasso.get().load(uri).fit().centerCrop().into(profileImage);
            Handler handler = new Handler(Looper.getMainLooper());
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.execute(() -> {
                try{
                    InputStream url = new URL(uri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(url);
                    handler.post(() -> ((ImageView) findViewById(R.id.imageView4)).setImageBitmap(bitmap));
                } catch(Exception e){

                }
            });
        });
    }

    private void showUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        } else {
            String email = user.getEmail();
            textViewEmail.setText(email);
        }
    }



    public void btnUD(View view) {
        Intent intent = new Intent(this, UpdateProfileActivity.class);
        startActivity(intent);
    }


}