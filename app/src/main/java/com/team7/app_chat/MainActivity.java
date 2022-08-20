package com.team7.app_chat;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.team7.app_chat.adapters.ViewPagerAdapter;
import com.team7.app_chat.databinding.ActivityMainBinding;
import com.team7.app_chat.ui.contacts.ContactsFragment;
import com.team7.app_chat.ui.chat.ChatRoomFragment;
import com.team7.app_chat.ui.home.HomeFragment;
import com.team7.app_chat.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}