package com.team7.app_chat;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team7.app_chat.Util.FirestoreRepository;
import com.team7.app_chat.Util.Helper;
import com.team7.app_chat.adapters.ViewPagerAdapter;
import com.team7.app_chat.databinding.ActivityMainBinding;
import com.team7.app_chat.models.User;
import com.team7.app_chat.ui.contacts.ContactsFragment;
import com.team7.app_chat.ui.home.HomeFragment;
import com.team7.app_chat.ui.settings.SettingsFragment;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavBar;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        bottomNavBar = findViewById(R.id.bottomNavBar);
        bottomNavBar.setOnNavigationItemSelectedListener(item -> navigation(item));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.addFragment(new ContactsFragment());
        viewPagerAdapter.addFragment(new HomeFragment());
        viewPagerAdapter.addFragment(new SettingsFragment());

        viewPager = findViewById(R.id.viewPagerMain);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0:
                        bottomNavBar.setSelectedItemId(R.id.navigation_contacts);
                        break;
                    case 1:
                        bottomNavBar.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 2:
                        bottomNavBar.setSelectedItemId(R.id.navigation_settings);
                        break;
                }
            }
        });



    }

    private boolean navigation(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_contacts:
                viewPager.setCurrentItem(0);
                return true;
            case R.id.navigation_home:
                viewPager.setCurrentItem(1);
                return true;
            case R.id.navigation_settings:
                viewPager.setCurrentItem(2);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}