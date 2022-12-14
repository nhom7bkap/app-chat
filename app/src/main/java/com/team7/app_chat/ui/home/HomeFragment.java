package com.team7.app_chat.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.MainActivity;
import com.team7.app_chat.R;
import com.team7.app_chat.SetupProfileActivity;
import com.team7.app_chat.SignInActivity;
import com.team7.app_chat.SplashActivity;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.ViewPagerAdapter;
import com.team7.app_chat.databinding.ActivityMainBinding;
import com.team7.app_chat.models.User;
import com.team7.app_chat.ui.chat.ChatRoomFragment;
import com.team7.app_chat.ui.contacts.ContactsFragment;
import com.team7.app_chat.ui.settings.SettingsFragment;

import java.util.List;


public class HomeFragment extends Fragment {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavBar;
    private ViewPager2 viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            new UserRepository().getByEmail(currentUser.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) return;
                    try {
                        User user = value.getDocuments().get(0).toObject(User.class);
                        switch (value.getDocumentChanges().get(0).getType()) {
                            case ADDED:
                            case MODIFIED:
                                user.setId(value.getDocuments().get(0).getId());
                                CurrentUser.user = user;
                                break;
                        }
                    } catch (Exception e) {
                        Exception a = e;
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bottomNavBar = view.findViewById(R.id.bottomNavBar);
        bottomNavBar.setOnNavigationItemSelectedListener(item -> navigation(item));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.addFragment(new ChatRoomFragment());
        viewPagerAdapter.addFragment(new ContactsFragment());
        viewPagerAdapter.addFragment(new SettingsFragment());

        viewPager = view.findViewById(R.id.viewPagerMain);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavBar.setSelectedItemId(R.id.menu_chat_room);
                        break;
                    case 1:
                        bottomNavBar.setSelectedItemId(R.id.menu_contacts);
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
            case R.id.menu_chat_room:
                viewPager.setCurrentItem(0);
                return true;
            case R.id.menu_contacts:
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