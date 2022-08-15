package com.team7.app_chat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team7.app_chat.Util.FirestoreRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.components.ProgressButton;
import com.team7.app_chat.models.User;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class SetupProfileActivity extends AppCompatActivity {

    public ImageView avatarImg;
    private ProgressButton progressButton;
    private Uri sourceUri;
    private Uri destinationUri;
    private ActivityResultLauncher<String[]> permissionContract;
    private ActivityResultLauncher<Uri> takePhotoContract;
    private ActivityResultLauncher<String> pickImageContract;
    private UserRepository userRepository;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progressButton = new ProgressButton(SetupProfileActivity.this, findViewById(R.id.btnSetupProfile), "Setup Profile");
        userRepository = new UserRepository("User");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRepository.get(currentUser.getUid()).addOnSuccessListener(this, user -> {
            this.user = user;
            this.user.setId(currentUser.getUid());
        });
        initComponents();
    }

    private void initComponents() {
//        Toast.makeText(this, "Completed!", Toast.LENGTH_SHORT).show();
//        avatarImg = findViewById(R.id.imgAvatar);
//
//        takePhotoContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), status -> {
//            if (status) {
//                String destinationFileName = System.currentTimeMillis() + ".jpg";
//                destinationUri = Uri.fromFile(new File(getCacheDir(), destinationFileName));
//                UCrop.of(sourceUri, destinationUri)
//                        .withAspectRatio(1, 1)
//                        .withMaxResultSize(500, 500)
//                        .start(this);
//            }
//        });
//
//        permissionContract = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                AtomicBoolean status = new AtomicBoolean(true);
//                result.forEach((key, val) -> {
//                    if (!val) {
//                        status.set(false);
//                    }
//                });
//                if (!status.get()) {
//                    Toast.makeText(this, "Please allow permission to use this feature!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        pickImageContract = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
//            String destinationFileName = System.currentTimeMillis() + ".jpg";
//            destinationUri = Uri.fromFile(new File(getCacheDir(), destinationFileName));
//            sourceUri = uri;
//            UCrop.of(sourceUri, destinationUri)
//                    .withAspectRatio(1, 1)
//                    .withMaxResultSize(500, 500)
//                    .start(this);
//        });
//
//        ImageButton btnPickImg = findViewById(R.id.btnPickImg);
//        btnPickImg.setOnClickListener(view -> {
//            showDialog();
//        });
        findViewById(R.id.edtBirthday).setOnClickListener(view -> openDatePicker(view));


        findViewById(R.id.btnSkip).setOnClickListener(view -> goToHomePage());
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
        findViewById(R.id.btnSetupProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

//
//    private Uri createImageUri(){
//        Uri imageCollection;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
//        } else{
//            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        }
//        String imageName = String.valueOf(System.currentTimeMillis());
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
//        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        Uri finalUri = getContentResolver().insert(imageCollection, contentValues);
//        return finalUri;
//    }
//    private void pickImageContract(){
//        permissionContract();
//        if(checkCameraPermission()){
//            pickImageContract.launch("image/*");
//        }
//
//    }
//    private void captureImageContract(){
//        permissionContract();
//        if(checkCameraPermission()){
//            sourceUri = createImageUri();
//            takePhotoContract.launch(sourceUri);
//        }
//    }
//    private void permissionContract() {
//        String permission[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        permissionContract.launch(permission);
//    }
//    private void loadImage(Uri uri){
//        avatarImg.setImageURI(uri);
//    }
//    private boolean checkCameraPermission(){
//        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
//        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//        return result && result1;
//    }
//
//    private void showDialog(){
//        try {
//            String imageItems[] = new String[]{"Take a picture", "Choose from gallery", "Cancel"};
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Select one");
//            builder.setItems(imageItems, (dialog, item) ->{
//                switch (imageItems[item]){
//                    case "Take a picture" :
//                        dialog.dismiss();
//                        captureImageContract();
//                        break;
//                    case "Choose from gallery":
//                        dialog.dismiss();
//                        pickImageContract();
//                        break;
//                    case "Cancel":
//                        dialog.dismiss();
//                        break;
//                }
//            });
//            builder.show();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//            final Uri resultUri = UCrop.getOutput(data);
//            loadImage(resultUri);
//        } else if (resultCode == UCrop.RESULT_ERROR) {
//            final Throwable cropError = UCrop.getError(data);
//        }
//    }


    private void submitForm() {
        progressButton.buttonActivated();
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
        userRepository.update(user).addOnSuccessListener(unused -> {
            progressButton.buttonFinished();
            goToHomePage();
        }).addOnFailureListener(e -> {
            progressButton.buttonFailed();
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
}