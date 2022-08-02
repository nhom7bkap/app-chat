package com.team7.app_chat;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.team7.app_chat.Util.FirestoreRepository;
import com.team7.app_chat.Util.Identifiable;
import com.team7.app_chat.models.User;
import com.team7.app_chat.ui.main.SectionsPagerAdapter;
import com.team7.app_chat.databinding.ActivityMainBinding;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Write a message to the database

        FirestoreRepository FR = new FirestoreRepository(new User().getClass(), "User");
        User u = new User();
        u.setId(2);
        u.setUserName("phong");
        u.setEmail("phong123@gmail.com");
        u.setPhone("0123456789");
        u.setPassword("phong123");
        u.setConfirmPassword("phong123");
        u.setFirstName("phong");
        u.setLastName("hoang");
        u.setGender(1);
        u.setDOB(new Date());
        u.setAddress("ha noi");
        u.setImage("abc.png");
        u.setVerification(true);
        u.setType(1);
        u.setStatus(1);
        u.setCreated_at(new Date());
        u.setUpdated_at(new Date());
        
        Log.e("error", u.getEntityKey());

        FR.create(u);

//        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference("message");
//                boolean connected = snapshot.getValue(Boolean.class);
//                if (connected) {
//                    myRef.setValue("Hello, World!");
//
//                    myRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            // This method is called once with the initial value and again
//                            // whenever data at this location is updated.
//                            String value = dataSnapshot.getValue(String.class);
//                            Log.e("Info", "Value is: " + value);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError error) {
//                            // Failed to read value
//                            Log.e("Info", "Failed to read value.", error.toException());
//                        }
//                    });
//                    Log.d("ConnectDB", "connected");
//                } else {
//                    Log.d("ConnectDB", "not connected");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("ConnectDB", "Listener was cancelled");
//            }
//        });


//        myRef.child("message").addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value = snapshot.getValue(String.class);
//                Log.e("message", "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Info", "Failed to read value.", error.toException());
//            }
//        });


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}