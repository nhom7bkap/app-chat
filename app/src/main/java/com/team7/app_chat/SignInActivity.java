package com.team7.app_chat;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team7.app_chat.Util.Helper;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.components.ProgressButton;
import com.team7.app_chat.components.ProgressDialogActivity;
import com.team7.app_chat.models.User;


public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuth";
//    private ProgressDialogActivity PDA;
    private ProgressButton progressButton;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        view = findViewById(R.id.signInButton);
        progressButton = new ProgressButton(SignInActivity.this, findViewById(R.id.signInButton), "Sign In");
//        PDA = new ProgressDialogActivity(this);
//        PDA.setCancelable(false);
//        PDA.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressButton.buttonActivated();
                signIn();
            }
        });
    }

    public void signIn() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = ((EditText) findViewById(R.id.formEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.formPassword)).getText().toString();
        if (Helper.checkSignIn(email, password)) {
//            PDA.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                new UserRepository().get(user.getUid()).addOnSuccessListener(new OnSuccessListener<User>() {
                                    @Override
                                    public void onSuccess(User user) {
                                        Intent it;
                                        if (user.isFirstTime())
                                            it = new Intent(SignInActivity.this, SetupProfileActivity.class);
                                        else
                                            it = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(it);
                                    }
                                });
//                                PDA.dismiss();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                progressButton.buttonFailed();
//                                PDA.dismiss();
                            }
                        }
                    });
        } else {
            progressButton.buttonFailed();
            Toast.makeText(SignInActivity.this, "Email or Password not match !",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void signUp(View view) {
        Intent it = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(it);
    }
}