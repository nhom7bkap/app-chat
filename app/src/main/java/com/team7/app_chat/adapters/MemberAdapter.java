package com.team7.app_chat.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.Member;
import com.team7.app_chat.models.User;
import com.team7.app_chat.ui.chat.ContactModalBottomSheet;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
  private List<DocumentSnapshot> list;
  private Context context;
  private UserRepository repository;
  private RoomChatRepository roomRepository;
  private String roomId;
  private User currentUser;
  private boolean isMod ;
  private IBottomSheet callback;
  private ContactModalBottomSheet.INavBottomSheet navCallback;
  private FragmentActivity fragmentActivity;

  public interface IBottomSheet{
    void show(Member member);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public MemberAdapter(List<DocumentSnapshot> list, String roomId, boolean isMod, IBottomSheet callback, ContactModalBottomSheet.INavBottomSheet navCallback, FragmentActivity fragmentActivity) {
    this.list = list;
    this.roomId = roomId;
    this.isMod = isMod;
    this.callback = callback;
    this.navCallback = navCallback;
    this.fragmentActivity = fragmentActivity;
    repository = new UserRepository();
    roomRepository = new RoomChatRepository();
    currentUser = CurrentUser.user;
  }
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    context = parent.getContext();
    View item = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
    return new ViewHolder(item);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Member member = list.get(position).toObject(Member.class);
    String id = member.getUser().getId();
    if(!id.equals(currentUser.getId())){
      holder.layout.setOnClickListener(view -> {
        if(isMod){
          callback.show(member);
        } else{
//          UserInformationBottomSheet bottomSheet = new UserInformationBottomSheet(id, navCallback);
//          bottomSheet.show(fragmentActivity.getSupportFragmentManager(), null);
        }

      });
    }


    if(member.isMod()){
      holder.name.setTextColor(ContextCompat.getColor(context, R.color.error));
    } else{
      holder.name.setTextColor(ContextCompat.getColor(context, R.color.black));
    }
    repository.getDocRf(id).get().addOnSuccessListener(documentSnapshot -> {
      User user = documentSnapshot.toObject(User.class);
      Glide.with(context).load(user.getAvatar()).into(holder.avatar);
      holder.name.setText(user.getFullName());
    });
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder{
    private ImageView avatar;
    private TextView name;
    private ConstraintLayout layout;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      avatar = itemView.findViewById(R.id.itAvatar);
      name = itemView.findViewById(R.id.itName);
      layout = itemView.findViewById(R.id.layoutContactItem);
    }
  }
}
