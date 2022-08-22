package com.team7.app_chat;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.team7.app_chat.Util.FiresBaseRepository;
import com.team7.app_chat.Util.FirestoreRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.models.User;
import com.team7.app_chat.ui.settings.SettingsFragment;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText dateTime_in, editTextName, editTextEmail, editTextAddress;

    String textFullName;
    String textDob;
    RadioGroup radioButton;
    RadioButton female_btn, male_btn;
    String textAddress;
    ImageView profileImage;
    FirebaseAuth firebaseProfile;
    FirebaseUser firebaseUser;
    FirebaseFirestore fStore;
    UserRepository userRepository;
    StorageReference storageReference;
    Button btnUpdate;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        firebaseProfile = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseProfile.getCurrentUser();

        userRepository = new UserRepository();
        userRepository.get(firebaseUser.getUid()).addOnSuccessListener(this, user -> {
            this.user = user;
            this.user.setId(firebaseUser.getUid());
        });
        female_btn = findViewById(R.id.female_btn);
        male_btn = findViewById(R.id.male_btn);
        radioButton = findViewById(R.id.radioGenderGroup);
        storageReference = FirebaseStorage.getInstance().getReference();
        editTextName = findViewById(R.id.userName);
        profileImage = findViewById(R.id.profileImgView);

        btnUpdate = findViewById(R.id.btnUpdate);

        editTextAddress = findViewById(R.id.userAddress);

        StorageReference profileRef = storageReference.child("users").child(firebaseProfile.getCurrentUser().getUid()).child("profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Handler handler = new Handler(Looper.getMainLooper());
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.execute(() -> {
                try {
                    InputStream url = new URL(uri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(url);
                    handler.post(() -> ((ImageView) findViewById(R.id.profileImgView)).setImageBitmap(bitmap));
                } catch (Exception e) {

                }
            });
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });
        showProfile();
        dateTime_in = findViewById(R.id.dateTime);
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
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        dateTime_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new DatePickerDialog(UpdateProfileActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textFullName = ((EditText) findViewById(R.id.userName)).getText().toString();
                textAddress = ((EditText) findViewById(R.id.userAddress)).getText().toString();
                String date = ((EditText) findViewById(R.id.dateTime)).getText().toString();
                Date birthday = null;
                try {
                    birthday = new SimpleDateFormat("dd-MM-yyyy").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                user.setUserName(textFullName);
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
                userRepository.update(user).addOnSuccessListener(unused -> {
                    Toast.makeText(UpdateProfileActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    Toast.makeText(UpdateProfileActivity.this, "Update fails", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void showProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirestoreRepository<User> repository = new FirestoreRepository<>(User.class, "User");
        repository.get(currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                User user = task.getResult();
                if (user == null) {
                    return;
                } else {
                    textFullName = task.getResult().getUserName();
                    textDob = task.getResult().getDOB() + "";
                    textAddress = task.getResult().getAddress();
                    if(task.getResult().getGender() == 1){
                        female_btn.setChecked(true);
                        male_btn.setChecked(false);
                    }else {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);


            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri) {
        // uplaod image to firebase storage
        final StorageReference fileRef = storageReference.child("users/" + firebaseProfile.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
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
}