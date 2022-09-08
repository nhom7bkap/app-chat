package com.team7.app_chat.ui.contacts;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.StorageReference;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;


public class RoomInfoFragment extends Fragment {
    private ActivityResultLauncher<String[]> permissionContract;
    private ActivityResultLauncher<Uri> takePhotoContract;
    private ActivityResultLauncher<String> pickImageContract;
    private ActivityResultLauncher<Intent> cropImageContract;
    private Uri sourceUri;
    private Uri destinationUri;
    private StorageReference storageRef;
    private ImageView avatar;
    private boolean isCapture;
    private RoomChatRepository roomRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomRepository = new RoomChatRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View infoView = inflater.inflate(R.layout.fragment_room_info, container, false);
        avatar = infoView.findViewById(R.id.imgAvatar);
        TextInputLayout layoutName = infoView.findViewById(R.id.lRoomName);
        infoView.findViewById(R.id.btnPickImg).setOnClickListener(view -> {
            showDialog();
        });
        infoView.findViewById(R.id.btNext).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            if (destinationUri != null) {
                bundle.putString("avatarPath", destinationUri.getPath());
            }
            boolean flag = true;
            String name = ((EditText) infoView.findViewById(R.id.etRoomName)).getText().toString();
            if (name.trim().length() > 0) {
                bundle.putString("name", name.trim());
            } else {
                layoutName.setError("Required");
                flag = false;
            }
            if (flag) {
                NavHostFragment.findNavController(this).navigate(R.id.action_roomInfoFragment_to_createRoomFragment, bundle);
            }
        });
        return infoView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createContract();
    }

    private void createContract() {
        cropImageContract = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                avatar.setImageURI(destinationUri);
            }
        });


        takePhotoContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), status -> {
            if (status) {
                String destinationFileName = System.currentTimeMillis() + ".jpg";
                destinationUri = Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName));
                Intent intent = UCrop.of(sourceUri, destinationUri)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(500, 500)
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
                    .withMaxResultSize(500, 500)
                    .getIntent(getContext());
            cropImageContract.launch(intent);
        });

        permissionContract = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                AtomicBoolean status = new AtomicBoolean(true);
                result.forEach((key, val) -> {
                    if (!val) {
                        status.set(false);
                    }
                });
                if (!status.get()) {
                    Toast.makeText(getContext(), "Please allow permission to use this feature!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isCapture) {
                        if (checkCameraPermission()) {
                            sourceUri = createImageUri();
                            takePhotoContract.launch(sourceUri);
                        }
                    } else {
                        if (checkStoragePermission()) {
                            pickImageContract.launch("image/*");
                        }
                    }
                }
            }
        });
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    private void pickImageContract() {
        String permission[] = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        isCapture = false;
        permissionContract.launch(permission);

    }

    private void captureImageContract() {
        String permission[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        isCapture = true;
        permissionContract.launch(permission);

    }

    private Uri createImageUri() {
        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        String imageName = String.valueOf(System.currentTimeMillis());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri finalUri = getActivity().getContentResolver().insert(imageCollection, contentValues);
        return finalUri;
    }

    private void showDialog() {
        try {
            String imageItems[] = new String[]{"Take a picture", "Choose from gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select one");
            builder.setItems(imageItems, (dialog, item) -> {
                switch (imageItems[item]) {
                    case "Take a picture":
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}