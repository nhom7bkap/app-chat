package com.team7.app_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team7.app_chat.Util.FirestoreRepository;
import com.team7.app_chat.Util.Helper;
import com.team7.app_chat.components.ProgressButton;
import com.team7.app_chat.models.User;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuth";
     private ProgressButton progressButton;
//    ImageView imgAvatar;
//    FirebaseAuth firebaseProfile;
//    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
//        imgAvatar = findViewById(R.id.imgAvatar);
//        storageReference = FirebaseStorage.getInstance().getReference();
//        firebaseProfile = FirebaseAuth.getInstance();
        progressButton = new ProgressButton(SignUpActivity.this, findViewById(R.id.signUpButton), "Sign Up");
        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressButton.buttonActivated();
                onSignUp();
            }
        });
//        imgAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(openGalleryIntent, 1000);
//            }
//        });
    }

    public void signIn(View view) {
        Intent it = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(it);
    }

    public void onSignUp() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = ((EditText) findViewById(R.id.formEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.formPassword)).getText().toString();
        String rePassword = ((EditText) findViewById(R.id.formRePassword)).getText().toString();
        if (Helper.checkSignUp(email, password, rePassword)) {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    User ur = new User();
                    ur.setFirstTime(true);
                    ur.setEmail(user.getEmail());
                    FirestoreRepository<User> repository = new FirestoreRepository<>(User.class,"User");
                    repository.create(ur,user.getUid());
                    Intent it = new Intent(SignUpActivity.this, SetupProfileActivity.class);
                    startActivity(it);
                } else {
                    progressButton.buttonFailed();
                    Toast.makeText(SignUpActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                updateUI(null);
                }
            }).addOnFailureListener(e -> {
                progressButton.buttonFailed();
                String error = e.getMessage();
                Log.e(TAG,error);
            });
        } else {
            progressButton.buttonFailed();
            Toast.makeText(SignUpActivity.this, "Email or Password not match !",
                    Toast.LENGTH_SHORT).show();
        }


    }

//    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1000) {
//            if (resultCode == Activity.RESULT_OK) {
//                Uri imageUri = data.getData();
//
//                //profileImage.setImageURI(imageUri);
//
//                uploadImageToFirebase(imageUri);
//
//
//            }
//        }
//
//    }
//
//    private void uploadImageToFirebase(Uri imageUri) {
//        // uplaod image to firebase storage
//        final StorageReference fileRef = storageReference.child("users/" + firebaseProfile.getCurrentUser().getUid() + "/profile.jpg");
//        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        Picasso.get().load(uri).into(imgAvatar);
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
}