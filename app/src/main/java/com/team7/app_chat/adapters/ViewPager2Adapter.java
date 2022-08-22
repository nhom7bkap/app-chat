package com.team7.app_chat.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.team7.app_chat.ui.contacts.FindFriendFragment;
import com.team7.app_chat.ui.contacts.FriendRequestFragment;
import com.team7.app_chat.ui.home.HomeFragment;

import java.util.ArrayList;

public class ViewPager2Adapter extends FragmentStateAdapter {

    public ViewPager2Adapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FindFriendFragment();
            case 1:
                return new FriendRequestFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}