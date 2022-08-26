package com.team7.app_chat.ui.chat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.UsersAdapter;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomFragment extends Fragment implements UserRepository.IContactCallback {

    private FirebaseAuth mAuth;
    private UsersAdapter usersAdapter;
    private final List<Contact> mListContact = new ArrayList<>();
    private View mView;

    Button bottomsheet;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room,
                container, false);
        mView = view.getRootView();

        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    private void showDialog() {

        final Dialog dialog = new Dialog(mView.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);

        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout shareLayout = dialog.findViewById(R.id.layoutShare);
        LinearLayout uploadLayout = dialog.findViewById(R.id.layoutUpload);
        LinearLayout printLayout = dialog.findViewById(R.id.layoutPrint);

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(mView.getContext(), "Edit is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(mView.getContext(), "Share is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        uploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(mView.getContext(), "Upload is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        printLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(mView.getContext(), "Print is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.e("currentUser", currentUser.getEmail());
        } else {
            Log.e("currentUser", currentUser.getEmail());
        }
//        List<Contact> lstContact = new ArrayList<>();
//        UserRepository repository = new UserRepository();
//       repository.getContactsWithCallback(currentUser.getUid());
//        new UserRepository( this).getContactsWithCallback(currentUser.getUid());
    }


    @Override
    public void loadContact(List<Contact> list) {
        // Nó trả về listcontact ở đây
        this.mListContact.addAll(list);
//        List<Contact> a = list;
    }
}