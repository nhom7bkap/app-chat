package com.team7.app_chat;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.team7.app_chat.Util.FirestoreRepository;
import com.team7.app_chat.Util.Helper;
import com.team7.app_chat.databinding.ActivityMainBinding;
import com.team7.app_chat.models.User;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();

        actionBar.hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        String encodepass = Helper.encode("phong123");
//        Log.e("Error",encodepass);
//        String decodepass = Helper.decode(encodepass);
//        Log.e("Error",decodepass);
/*
        FirestoreRepository FR = new FirestoreRepository(new User().getClass(), "User");
        User u = new User();
        u.setKey("LA");
        u.setUserName("phong");
        u.setEmail("phong123@gmail.com");
        u.setPassword("phong123");
        u.setConfirmPassword("phong123");
        u.setFirstName("phong");
        u.setLastName("hoang");
        u.setGender(1);
        u.setDOB(new Date().toString());
        u.setAddress("ha noi");
        u.setImage("abc.png");
        u.setVerification(true);
        u.setType(1);
        u.setStatus(1);
        u.setCreated_at(new Date().toString());
        u.setUpdated_at(new Date().toString());

        Task t = FR.create(u);
 */
    }
}