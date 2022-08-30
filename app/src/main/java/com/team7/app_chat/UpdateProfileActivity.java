package com.team7.app_chat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team7.app_chat.Util.FirestoreRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.components.ProgressButton;
import com.team7.app_chat.models.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText dateTime_in, editTextName, editTextAddress;

    TextInputEditText edtAddress, edtFullname;
    String textFullName, textAddress;
    String textDob;
    RadioGroup radioButton;
    RadioButton female_btn, male_btn;
    ImageView profileImage;
    FirebaseAuth firebaseProfile;
    FirebaseFirestore fStore;
    UserRepository userRepository;
    StorageReference storageReference;
    private ProgressButton progressButton;
    private User user;
    private Context mCtx;
    private ActivityResultLauncher<String[]> permissionContract;
    private ActivityResultLauncher<Uri> takePhotoContract;
    private ActivityResultLauncher<String> pickImageContract;

    private Uri sourceUri;
    private boolean isCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        createContract();
        firebaseProfile = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mCtx = getApplicationContext();
        userRepository = new UserRepository();
        user = CurrentUser.user;
        female_btn = findViewById(R.id.female_btn);
        male_btn = findViewById(R.id.male_btn);
        radioButton = findViewById(R.id.radioGenderGroup);
        storageReference = FirebaseStorage.getInstance().getReference();
        editTextName = findViewById(R.id.userName);
        profileImage = findViewById(R.id.imgAvatar);
        edtAddress = findViewById(R.id.userAddress);
        edtFullname = findViewById(R.id.userName);
        editTextAddress = findViewById(R.id.userAddress);
        dateTime_in = findViewById(R.id.dateTime);
        progressButton = new ProgressButton(UpdateProfileActivity.this, findViewById(R.id.btnUpdate), "Update");
        showProfile();
        findViewById(R.id.imgAvatar).setOnClickListener(view -> {
            pickImageContract();
        });
        findViewById(R.id.btnPickImg).setOnClickListener(view -> {
            captureImageContract();
        });
        dateTime_in.setInputType(InputType.TYPE_NULL);
        dateTime_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDiaLog(dateTime_in);
            }

            private void showDateTimeDiaLog(final EditText dateTime_in) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        dateTime_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new DatePickerDialog(UpdateProfileActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        findViewById(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressButton.buttonActivated();
                submitForm();
            }
        });
    }

    public void submitForm() {
        if (!validateUser() || !validateDOB()) {
            progressButton.buttonFailed();
            return;
        }
        textFullName = ((EditText) findViewById(R.id.userName)).getText().toString();
        textAddress = ((EditText) findViewById(R.id.userAddress)).getText().toString();
        String date = ((EditText) findViewById(R.id.dateTime)).getText().toString();
        Date birthday = null;
        try {
            birthday = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setFullName(textFullName);
        user.setAddress(textAddress);
        user.setDOB(birthday);
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

        if (sourceUri == null) {
            userRepository.update(user).addOnSuccessListener(unused -> {
                Toast.makeText(UpdateProfileActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(UpdateProfileActivity.this, "Update fails", Toast.LENGTH_SHORT).show();
            });
        } else {
            fileRef.putFile(sourceUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            user.setAvatar(uri.toString());
                            userRepository.update(user).addOnSuccessListener(unused -> {
                                Toast.makeText(UpdateProfileActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(UpdateProfileActivity.this, "Update fails", Toast.LENGTH_SHORT).show();
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
        Intent it;
        it = new Intent(UpdateProfileActivity.this, MainActivity.class);
        startActivity(it);
    }

    private boolean validateUser() {
        textFullName = ((EditText) findViewById(R.id.userName)).getText().toString();
        if (textFullName.isEmpty()) {
            edtFullname.setError("Enter FullName");
            return false;
        } else {
            edtFullname.setError(null);
            return true;
        }
    }

    private boolean validateDOB() {
        String date = ((EditText) findViewById(R.id.dateTime)).getText().toString();
        if (date.isEmpty()) {
            dateTime_in.setError("Enter FullName");
            return false;
        } else {
            dateTime_in.setError(null);
            return true;
        }
    }

    private void showProfile() {
        Glide.with(this).load(user.getAvatar()).into(profileImage);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirestoreRepository<User> repository = new FirestoreRepository<>(User.class, "User");
        repository.get(currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                User user = task.getResult();
                if (user == null) {
                    return;
                } else {
                    textFullName = task.getResult().getFullName();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate = dateFormat.format(task.getResult().getDOB());
                    textDob = strDate;
                    textAddress = task.getResult().getAddress();
                    if (task.getResult().getGender() == 1) {
                        female_btn.setChecked(true);
                        male_btn.setChecked(false);
                    } else {
                        female_btn.setChecked(false);
                        male_btn.setChecked(true);
                    }

                    editTextName.setText(textFullName);
                    editTextAddress.setText(textAddress);
                    dateTime_in.setText(textDob);
                }
            }
        });
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

    private void loadImage(Uri uri) {
        Glide.with(mCtx).load(uri.toString()).into(profileImage);
    }
}