package com.team7.app_chat.ui.chat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.models.RoomChat;
import com.team7.app_chat.models.RoomChats;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;


public class RoomSettingFragment extends Fragment {
  private RoomChatRepository roomRepository;
  private String roomId;
  private ImageView avatar;
  private TextView roomName;
  private ActivityResultLauncher<String[]> permissionContract;
  private ActivityResultLauncher<Uri> takePhotoContract;
  private ActivityResultLauncher<String> pickImageContract;
  private ActivityResultLauncher<Intent> cropImageContract;
  private Uri sourceUri;
  private Uri destinationUri;
  private StorageReference storageRef;
  private boolean isCapture;
  private DocumentReference roomRef;
  private boolean isMod;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    roomRepository = new RoomChatRepository();
    roomId = getArguments().getString("roomId");
    isMod = getArguments().getBoolean("isMod");
    storageRef = FirebaseStorage.getInstance().getReference();
    if(isMod){
      createContract();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View settingView = inflater.inflate(R.layout.fragment_room_setting, container, false);
    avatar = settingView.findViewById(R.id.imgAvatar);
    roomName = settingView.findViewById(R.id.tvRoomName);
    settingView.findViewById(R.id.btnPickImg).setOnClickListener(view -> {
      showDialog();
    });

    settingView.findViewById(R.id.cvMember).setOnClickListener(view -> {
      Bundle bundle = new Bundle();
      bundle.putString("roomId", roomId);
      NavHostFragment.findNavController(this)
          .navigate(R.id.action_roomSettingFragment_to_allMemberFragment, bundle);
    });
    settingView.findViewById(R.id.cvLeave).setOnClickListener(view -> {
      String message = CurrentUser.user.getFullName() + " left the room";
      roomRepository.removeMember(roomId, CurrentUser.user.getId(), message);
      Toast.makeText(getActivity(), "Completed", Toast.LENGTH_SHORT);
      NavHostFragment.findNavController(this)
          .navigate(R.id.action_roomSettingFragment_to_homeFragment);
    });
    CardView cvInfo = settingView.findViewById(R.id.cvInfo);
    ImageButton btnPickImg = settingView.findViewById(R.id.btnPickImg);
    if(isMod){
      cvInfo.setOnClickListener(view -> {
        Bundle bundle = new Bundle();
        bundle.putString("roomId", roomId);
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_roomSettingFragment_to_changeInfoFragment, bundle);
      });
      cvInfo.setVisibility(View.VISIBLE);
      btnPickImg.setVisibility(View.VISIBLE);
    } else{
      cvInfo.setVisibility(View.GONE);
      btnPickImg.setVisibility(View.GONE);
    }

    return settingView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    roomRepository.get().document(roomId).get().addOnSuccessListener(documentSnapshot -> {
      RoomChats room = documentSnapshot.toObject(RoomChats.class);
      roomRef = documentSnapshot.getReference();
      roomName.setText(room.getName());
      Glide.with(this).load(room.getAvatar()).into(avatar);
    });
  }

  private void createContract(){
    cropImageContract = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
      if(result.getResultCode() == Activity.RESULT_OK){
        Toast.makeText(getActivity(), "Updating...", Toast.LENGTH_SHORT).show();
        StorageReference imgRef = storageRef.child("avatars/" + System.currentTimeMillis());
        imgRef.putFile(destinationUri).addOnSuccessListener(taskSnapshot -> {
          imgRef.getDownloadUrl().addOnSuccessListener(uri -> {

            roomRef.update("avatarPath", uri.toString()).addOnSuccessListener(unused -> {
              Toast.makeText(getActivity(), "Completed!", Toast.LENGTH_SHORT).show();
              avatar.setImageURI(destinationUri);
            });
          });
        });
      }
    });

    takePhotoContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), status -> {
      if(status){
        String destinationFileName = System.currentTimeMillis() + ".jpg";
        destinationUri = Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName));
        Intent intent = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1, 1)
            .withMaxResultSize(500,500)
            .getIntent(getContext());
        cropImageContract.launch(intent);
      }
    });
    pickImageContract = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
      String destinationFileName = System.currentTimeMillis() + ".jpg";
      destinationUri = Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName));
      sourceUri = uri;
      Intent intent = UCrop.of(sourceUri, destinationUri)
          .withAspectRatio(1, 1)
          .withMaxResultSize(500,500)
          .getIntent(getContext());
      cropImageContract.launch(intent);
    });

    permissionContract = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        AtomicBoolean status = new AtomicBoolean(true);
        result.forEach((key, val) -> {
          if(!val){
            status.set(false);
          }
        });
        if(!status.get()){
          Toast.makeText(getContext(), "Please allow permission to use this feature!", Toast.LENGTH_SHORT).show();
        } else {
          if(isCapture){
            if(checkCameraPermission()){
              sourceUri = createImageUri();
              takePhotoContract.launch(sourceUri);
            }
          } else {
            if(checkStoragePermission()){
              pickImageContract.launch("image/*");
            }
          }
        }
      }
    });
  }
  private boolean checkStoragePermission(){
    boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    return result && result1;
  }
  private boolean checkCameraPermission(){
    boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    return result && result1;
  }
  private void pickImageContract(){
    String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    isCapture = false;
    permissionContract.launch(permission);

  }
  private void captureImageContract(){
    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    isCapture = true;
    permissionContract.launch(permission);

  }
  private Uri createImageUri(){
    Uri imageCollection;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
      imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
    } else{
      imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    String imageName = String.valueOf(System.currentTimeMillis());

    ContentValues contentValues = new ContentValues();
    contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
    contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
    Uri finalUri = getActivity().getContentResolver().insert(imageCollection, contentValues);
    return finalUri;
  }

  private void showDialog(){
    try {
      String[] imageItems = new String[]{"Take a picture", "Choose from gallery", "Cancel"};
      AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
      builder.setTitle("Select one");
      builder.setItems(imageItems, (dialog, item) ->{
        switch (imageItems[item]){
          case "Take a picture" :
            dialog.dismiss();
            captureImageContract();
            break;
          case "Choose from gallery":
            dialog.dismiss();
            pickImageContract();
            break;
          case "Cancel":
            dialog.dismiss();
            break;
        }
      });
      builder.show();
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}