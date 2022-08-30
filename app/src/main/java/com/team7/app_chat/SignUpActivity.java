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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressButton = new ProgressButton(SignUpActivity.this, findViewById(R.id.signUpButton), "Sign Up");
        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressButton.buttonActivated();
                onSignUp();
            }
        });
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
                    CurrentUser.user.setId(user.getUid());
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
}