package com.team7.app_chat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.components.ProgressButton;
import com.team7.app_chat.models.User;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SetupProfileActivity extends AppCompatActivity {

    private ProgressButton progressButton;
    private UserRepository userRepository;
    private User user;
    private TextInputLayout editName;
    private EditText edtBirthday;
    private ImageView imgAvatar;
    private Context mCtx;
    FirebaseAuth firebaseProfile;
    StorageReference storageReference;

    private ActivityResultLauncher<String[]> permissionContract;
    private ActivityResultLauncher<Uri> takePhotoContract;
    private ActivityResultLauncher<String> pickImageContract;

    private Uri sourceUri;
    private boolean isCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        createContract();
        Toolbar toolbar = findViewById(R.id.toolbar);
        edtBirthday = findViewById(R.id.edtBirthday);
        editName = findViewById(R.id.editName);
        imgAvatar = findViewById(R.id.imgAvatar);
        mCtx = getApplicationContext();
        firebaseProfile = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progressButton = new ProgressButton(SetupProfileActivity.this, findViewById(R.id.btnSetupProfile), "Setup Profile");
        userRepository = new UserRepository();
        user = CurrentUser.user;
        initComponents();
//        findViewById(R.id.btnPickImg).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(openGalleryIntent, 1000);
//            }
//        });
        imgAvatar.setOnClickListener(view -> {
            pickImageContract();
        });
        findViewById(R.id.btnPickImg).setOnClickListener(view -> {
            captureImageContract();
        });
    }

    private void initComponents() {

        findViewById(R.id.edtBirthday).setOnClickListener(view -> openDatePicker(view));


//        findViewById(R.id.btnSkip).setOnClickListener(view -> goToHomePage());
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
        findViewById(R.id.btnSetupProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }


    private boolean validateName() {
        String val = ((TextInputLayout) findViewById(R.id.editName)).getEditText().getText().toString();
        if (val.isEmpty()) {
            editName.setError("Enter FullName");
            return false;
        } else {
            editName.setError(null);
            return true;
        }
    }

    private boolean validateDOB() {
        String date = ((EditText) findViewById(R.id.edtBirthday)).getText().toString();
        if (date.isEmpty()) {
            edtBirthday.setError("Enter Birthday");
            return false;
        } else {
            edtBirthday.setError(null);
            return true;
        }
    }

    private void submitForm() {
        progressButton.buttonActivated();
        if (!validateName() || !validateDOB()) {
            progressButton.buttonFailed();
            return;
        }
        String fullname = ((TextInputLayout) findViewById(R.id.editName)).getEditText().getText().toString();
        String date = ((EditText) findViewById(R.id.edtBirthday)).getText().toString();
        Date birthday = null;
        try {
            birthday = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setFullName(fullname);
        user.setDOB(birthday);
        user.setFirstTime(false);
        if (user.getAvatar() == null) {
            user.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-app-4aa49.appspot.com/o/user-pngrepo-com.png?alt=media&token=e3dc0dd4-61d2-47e7-88f3-f727ed8b18e8");
        }
        RadioGroup radioButton = findViewById(R.id.radioGenderGroup);
        switch (radioButton.getCheckedRadioButtonId()) {
            case R.id.female_btn:
                user.setGender(1);
                break;
            case R.id.male_btn:
                user.setGender(2);
                break;
            default:
                user.setGender(0);
                break;
        }
        String fileName = String.valueOf(System.currentTimeMillis());
        final StorageReference fileRef = storageReference.child("users/" + fileName);
        fileRef.putFile(sourceUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        user.setAvatar(uri.toString());
                        userRepository.update(user).addOnSuccessListener(unused -> {
                            Toast.makeText(mCtx, "Completed!", Toast.LENGTH_SHORT).show();
                            progressButton.buttonFinished();
                            goToHomePage();
                        }).addOnFailureListener(e -> {
                            progressButton.buttonFailed();
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void openDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

        DatePickerDialog.OnDateSetListener listener = (datePicker, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            ((EditText) view).setText(fmt.format(calendar.getTime()));
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog pickerDialog = new DatePickerDialog(SetupProfileActivity.this, style, listener, year, month, day);
        pickerDialog.show();
    }

    private void goToHomePage() {
        Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void createContract() {

        takePhotoContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), status -> {
            if (status) {
                loadImage(sourceUri);
            }
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
                    Toast.makeText(mCtx, "Please allow permission to use this feature!", Toast.LENGTH_SHORT).show();
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

        pickImageContract = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            sourceUri = uri;
            loadImage(sourceUri);
        });
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(mCtx, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(mCtx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(mCtx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(mCtx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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
        Uri finalUri = mCtx.getContentResolver().insert(imageCollection, contentValues);
        return finalUri;
    }

    private void loadImage(Uri uri){
        Glide.with(mCtx).load(uri.toString()).into(imgAvatar);
    }
}