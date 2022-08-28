package com.team7.app_chat.ui.settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team7.app_chat.ChangePassActivity;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.R;
import com.team7.app_chat.SignInActivity;
import com.team7.app_chat.UpdateProfileActivity;
import com.team7.app_chat.models.User;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private View view;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = CurrentUser.user;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings,
                container, false);

        mAuth = FirebaseAuth.getInstance();
        view.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(view.getContext(), SignInActivity.class);
                startActivity(intent);
            }
        });
        showUserProfile();

//        StorageReference profileRef = storageReference.child("users").child(firebaseProfile.getCurrentUser().getUid()).child("profile.jpg");
//        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
////                Picasso.get().load(uri).fit().centerCrop().into(profileImage);
//            Handler handler = new Handler(Looper.getMainLooper());
//            ExecutorService pool = Executors.newSingleThreadExecutor();
//            pool.execute(() -> {
//                try {
//                    InputStream url = new URL(uri.toString()).openStream();
//                    Bitmap bitmap = BitmapFactory.decodeStream(url);
//                    handler.post(() -> ((ImageView) view.findViewById(R.id.imageView4)).setImageBitmap(bitmap));
//                } catch (Exception e) {
//
//                }
//            });
//        });
        view.findViewById(R.id.textView4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ChangePassActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.textView6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void showUserProfile() {
        ((TextView)view.findViewById(R.id.txtEmail)).setText(user.getEmail());
        ImageView imageView = view.findViewById(R.id.profileAvatar);
        Glide.with(view.getContext()).load(user.getAvatar()).into(imageView);
    }

}