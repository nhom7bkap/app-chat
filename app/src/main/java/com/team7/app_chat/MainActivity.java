package com.team7.app_chat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team7.app_chat.Util.FiresBaseRepository;
import com.team7.app_chat.Util.FirestoreRepository;
import com.team7.app_chat.databinding.ActivityMainBinding;
import com.team7.app_chat.models.City;
import com.team7.app_chat.models.User;
import com.team7.app_chat.ui.main.SectionsPagerAdapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
//ádasdasd
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Write a message to the database
/** Test
//        FirestoreRepository FR = new FirestoreRepository(new User().getClass(),"User");
//        User u = new User();
//        u.setKey("LA");
//        u.setUserName("phong");
//        u.setEmail("phong123@gmail.com");
//        u.setPhone("0123456789");
//        u.setPassword("phong123");
//        u.setConfirmPassword("phong123");
//        u.setFirstName("phong");
//        u.setLastName("hoang");
//        u.setGender(1);
//        u.setDOB(new Date().toString());
//        u.setAddress("ha noi");
//        u.setImage("abc.png");
//        u.setVerification(true);
//        u.setType(1);
//        u.setStatus(1);
//        u.setCreated_at(new Date().toString());
//        u.setUpdated_at(new Date().toString());
//
//        Task t = FR.create(u);

//        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        City city = new City("Los Angeles", "CA", "USA",
//                false, 5000000L, Arrays.asList("west_coast", "sorcal"));
//        db.collection("cities").document("abc123").set(city);
**/

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