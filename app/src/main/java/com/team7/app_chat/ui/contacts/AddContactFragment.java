package com.team7.app_chat.ui.contacts;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.SplashActivity;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.ContactAdapter;
import com.team7.app_chat.adapters.UsersAdapter;
import com.team7.app_chat.adapters.ViewPager2Adapter;
import com.team7.app_chat.adapters.ViewPagerAdapter;
import com.team7.app_chat.databinding.ActivityMainBinding;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.User;
import com.team7.app_chat.ui.chat.ChatRoomFragment;
import com.team7.app_chat.ui.settings.SettingsFragment;

import java.util.ArrayList;

public class AddContactFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager2 viewPager;
    private ViewPager2Adapter viewPager2Adapter;
    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);

        mTabLayout = view.findViewById(R.id.tabLayoutAddFriend);
        viewPager = view.findViewById(R.id.viewPagerAddContact);

        viewPager2Adapter = new ViewPager2Adapter(this);

        viewPager.setAdapter(viewPager2Adapter);
        new TabLayoutMediator(mTabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Find Friend");
                    break;
                case 1:
                    tab.setText("Friend Request");
                    break;
            }
        }).attach();
        view.findViewById(R.id.imAFback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AddContactFragment.this).navigate(R.id.action_add_contact_to_home);
            }
        });
        return view;
    }
}