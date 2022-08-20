package com.team7.app_chat.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.team7.app_chat.R;
import com.team7.app_chat.adapters.ViewPagerAdapter;
import com.team7.app_chat.databinding.ActivityMainBinding;
import com.team7.app_chat.ui.chat.ChatRoomFragment;
import com.team7.app_chat.ui.contacts.ContactsFragment;
import com.team7.app_chat.ui.settings.SettingsFragment;


public class HomeFragment extends Fragment {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavBar;
    private ViewPager2 viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bottomNavBar = view.findViewById(R.id.bottomNavBar);
        bottomNavBar.setOnNavigationItemSelectedListener(item -> navigation(item));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.addFragment(new ContactsFragment());
        viewPagerAdapter.addFragment(new ChatRoomFragment());
        viewPagerAdapter.addFragment(new SettingsFragment());

        viewPager = view.findViewById(R.id.viewPagerMain);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavBar.setSelectedItemId(R.id.menu_contacts);
                        break;
                    case 1:
                        bottomNavBar.setSelectedItemId(R.id.menu_chat_room);
                        break;
                    case 2:
                        bottomNavBar.setSelectedItemId(R.id.menu_settings);
                        break;
                }
            }
        });
        return view;
    }

    private boolean navigation(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_contacts:
                viewPager.setCurrentItem(0);
                return true;
            case R.id.menu_chat_room:
                viewPager.setCurrentItem(1);
                return true;
            case R.id.menu_settings:
                viewPager.setCurrentItem(2);
                return true;
            default:
                return false;
        }
    }
}