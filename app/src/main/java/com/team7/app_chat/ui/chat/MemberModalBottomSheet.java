package com.team7.app_chat.ui.chat;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.models.Member;

public class MemberModalBottomSheet extends BottomSheetDialogFragment {
  private LinearLayout bottomSheet;
  private BottomSheetBehavior bottomSheetBehavior;

  private RoomChatRepository roomRepository;

  private String roomId;
  private Member member;
  private ContactModalBottomSheet.INavBottomSheet callback;

  public MemberModalBottomSheet(String roomId, Member member, ContactModalBottomSheet.INavBottomSheet callback) {
    this.roomId = roomId;
    this.member = member;
    this.callback = callback;
    roomRepository = new RoomChatRepository();
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.member_modal_bottom_sheet, container, false);
    bottomSheet = view.findViewById(R.id.bsMember);
    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    view.findViewById(R.id.llInfo).setOnClickListener(v -> {
      UserInformationBottomSheet bottomSheet = new UserInformationBottomSheet(member.getUser().getId(), callback);
      bottomSheet.show(getActivity().getSupportFragmentManager(), null);
      dismiss();
    });

    if(member.isMod()){
      view.findViewById(R.id.llRemoveAdmin).setVisibility(View.VISIBLE);
      view.findViewById(R.id.llRemoveAdmin).setOnClickListener(v -> {
        roomRepository.changePermission(roomId, member.getUser().getId(), false).addOnSuccessListener(unused -> {
          Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
          dismiss();
        });
      });
    } else{
      view.findViewById(R.id.llAddAdmin).setVisibility(View.VISIBLE);
      view.findViewById(R.id.llAddAdmin).setOnClickListener(v -> {
        roomRepository.changePermission(roomId, member.getUser().getId(), true).addOnSuccessListener(unused -> {
          Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
          dismiss();
        });
      });
    }

    view.findViewById(R.id.llRemoveMember).setOnClickListener(v -> {
      roomRepository.removeMember(roomId, member.getUser().getId(), null).addOnSuccessListener(unused -> {
        Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
        dismiss();
      });
    });
    return view;
  }
}