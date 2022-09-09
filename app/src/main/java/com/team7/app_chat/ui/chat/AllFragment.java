package com.team7.app_chat.ui.chat;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.MemberAdapter;
import com.team7.app_chat.models.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AllFragment extends Fragment implements MemberAdapter.IBottomSheet, ContactModalBottomSheet.INavBottomSheet {
  private List<DocumentSnapshot> list;
  private RecyclerView recyclerView;
  private MemberAdapter adapter;
  private String roomId;
  private DocumentReference roomRef;
  private RoomChatRepository roomRepository;
  private UserRepository userRepository;


  public AllFragment(String roomId) {
    this.roomId = roomId;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    roomRepository = new RoomChatRepository();
    userRepository = new UserRepository();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View allView = inflater.inflate(R.layout.fragment_all, container, false);
    recyclerView = allView.findViewById(R.id.rvMember);
    return allView;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @SuppressLint("NotifyDataSetChanged")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    DocumentReference currentUser = userRepository.getDocRf(CurrentUser.user.getId());
    roomRepository.get().document(roomId).collection("members")
        .whereEqualTo("user", currentUser)
        .addSnapshotListener((value, error) -> {
          if(error != null) return;
          if(!value.isEmpty()){
            Member member = value.getDocuments().get(0).toObject(Member.class);
            loadData(member.isMod());
          }
        });
  }
  @RequiresApi(api = Build.VERSION_CODES.N)
  private void loadData(boolean isMod){
    list = new ArrayList<>();
    adapter = new MemberAdapter(list, roomId, isMod, this, this, getActivity());
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);

    roomRepository.get().document(roomId).collection("members")
        .addSnapshotListener((value, error) -> {
          if(error != null) return;
          for (DocumentChange doc : value.getDocumentChanges()){
            switch (doc.getType()){
              case ADDED:
                if(!list.contains(doc.getDocument())) {
                  list.add(doc.getDocument());
                  adapter.notifyItemInserted(list.indexOf(doc.getDocument()));
                };
                break;
              case REMOVED:
                Optional<DocumentSnapshot> stream = list.stream()
                    .filter(val -> val.getId().equals(doc.getDocument().getId()))
                    .findFirst();
                if(stream.isPresent()){
                  int index = list.indexOf(stream.get());
                  list.remove(index);
                  adapter.notifyItemRemoved(index);
                }
                break;
              case MODIFIED:
                Optional<DocumentSnapshot> stream2 = list.stream()
                    .filter(val -> val.getId().equals(doc.getDocument().getId()))
                    .findFirst();
                if(stream2.isPresent()){
                  int index = list.indexOf(stream2.get());
                  list.remove(index);
                  list.add(index, doc.getDocument());
                  adapter.notifyItemChanged(index);
                }
                break;
            }
          }
    });
  }

  @Override
  public void show(Member member) {
    MemberModalBottomSheet bottomSheet = new MemberModalBottomSheet(roomId, member, this);
    bottomSheet.show(getActivity().getSupportFragmentManager(), null);
  }

  @Override
  public void navigateToChat(Bundle bundle) {
    NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_allMemberFragment_to_chatFragment, bundle);
  }
}