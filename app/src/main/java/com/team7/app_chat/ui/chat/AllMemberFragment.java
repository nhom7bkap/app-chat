package com.team7.app_chat.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.ViewPagerAdapter;
import com.team7.app_chat.models.Member;


public class AllMemberFragment extends Fragment {
  private ViewPager2 viewPager;
  private TabLayout tabLayout;
  private RoomChatRepository roomRepository;
  private UserRepository userRepository;
  private String roomId;
  private Button btAdd;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    roomRepository = new RoomChatRepository();
    userRepository = new UserRepository();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View memberView = inflater.inflate(R.layout.fragment_all_member, container, false);
    tabLayout = memberView.findViewById(R.id.tabLayout);
    btAdd = memberView.findViewById(R.id.btnAdd);
    btAdd.setOnClickListener(view -> {
      AddUserDialog dialog = new AddUserDialog(roomId);
      dialog.show(getActivity().getSupportFragmentManager(), "aa");
    });

    viewPager = memberView.findViewById(R.id.vpMember);
    ViewPagerAdapter adapter = new ViewPagerAdapter(this);
    roomId = getArguments().getString("roomId");
    adapter.addFragment(new AllFragment(roomId));
    adapter.addFragment(new ModFragment(roomId));
    viewPager.setAdapter(adapter);
    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
      @Override
      public void onPageSelected(int position) {
        super.onPageSelected(position);
        tabLayout.getTabAt(position).select();
      }
    });

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });
    return memberView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    DocumentReference currentUser = userRepository.getDocRf(CurrentUser.user.getId());
    roomRepository.get().document(roomId).collection("members")
        .whereEqualTo("user", currentUser)
        .addSnapshotListener((value, error) -> {
          if(error != null) return;
          if(!value.getDocuments().isEmpty()){
            Member member = value.getDocuments().get(0).toObject(Member.class);
            if(member.isMod()){
              btAdd.setVisibility(View.VISIBLE);
            } else{
              btAdd.setVisibility(View.GONE);
            }
          }
        });
  }
}