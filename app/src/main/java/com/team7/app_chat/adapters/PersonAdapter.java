package com.team7.app_chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.User;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder>{
  private List<DocumentSnapshot> list;
  private Context context;
  private UserRepository repository;

  private IPerson callback;

  public interface IPerson{
    void remove(DocumentSnapshot friend);
  }

  public PersonAdapter(List<DocumentSnapshot> list, IPerson callback) {
    this.list = list;
    repository = new UserRepository();
    this.callback = callback;
  }
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    context = parent.getContext();
    View item = LayoutInflater.from(context).inflate(R.layout.item_person, parent, false);
    return new ViewHolder(item);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Contact friend = list.get(position).toObject(Contact.class);
    String id = friend.getUser().getId();
    repository.getDocRf(id).get().addOnSuccessListener(documentSnapshot -> {
      User user = documentSnapshot.toObject(User.class);
      Glide.with(context).load(user.getAvatar()).into(holder.avatar);
      holder.name.setText(user.getFullName());
      holder.layout.setOnClickListener(view -> {
        callback.remove(list.get(position));
      });
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
      layout = itemView.findViewById(R.id.layoutPersonItem);
    }
  }
}
