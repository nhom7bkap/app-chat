package com.team7.app_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
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
import com.team7.app_chat.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText dateTime_in, editTextName, editTextEmail, editTextAddress;

    String textFullName, textDob, textEmail, textAddress;
    ImageView profileImage;
    FirebaseAuth firebaseProfile;
    FirebaseUser firebaseUser;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        firebaseProfile = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseProfile.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        editTextName = findViewById(R.id.userName);
        profileImage = findViewById(R.id.profileImgView);

        btnUpdate = findViewById(R.id.btnUpdate);
        editTextEmail = findViewById(R.id.userEmail);
        editTextAddress = findViewById(R.id.userAddress);

        StorageReference profileRef = storageReference.child("User/" + firebaseProfile.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
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
                if (editTextName.getText().toString().isEmpty() || editTextEmail.getText().toString().isEmpty() || editTextAddress.getText().toString().isEmpty() || dateTime_in.getText().toString().isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "aaa", Toast.LENGTH_SHORT).show();
                    return;
                }
                textEmail = editTextEmail.getText().toString();
                firebaseUser.updateEmail(textEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        DocumentReference documentReference = fStore.collection("User").document(firebaseUser.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("email", textEmail);
                        edited.put("userName", editTextName.getText().toString());
                        edited.put("dob", dateTime_in.getText().toString());
                        edited.put("address", editTextAddress.getText().toString());
                        documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                Toast.makeText(UpdateProfileActivity.this, "Profile update", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateProfileActivity.this, SettingActivity.class);
                                startActivity(intent);
                            }
                        });
                        Toast.makeText(UpdateProfileActivity.this, "Email is changed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
                    textDob = task.getResult().getDOB();
                    textEmail = task.getResult().getEmail();
                    textAddress = task.getResult().getAddress();

                    editTextName.setText(textFullName);
                    editTextAddress.setText(textAddress);
                    editTextEmail.setText(textEmail);
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

                //profileImage.setImageURI(imageUri);

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