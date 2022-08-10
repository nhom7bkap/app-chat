package com.team7.app_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team7.app_chat.Util.FirestoreRepository;
import com.team7.app_chat.Util.Helper;
import com.team7.app_chat.models.User;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuth";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void SignIn(View view) {
        Intent it = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(it);
    }

    public void onSignUp(View view) {
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
                    ur.setEmail(user.getEmail());
                    ur.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-app-4aa49.appspot.com/o/user-avatar-pngrepo-com.png?alt=media&token=a7945b66-2094-4d39-a846-ca084e2e1c99");
                    FirestoreRepository<User> repository = new FirestoreRepository<>(User.class,"User");
                    repository.create(ur,user.getUid());
                    //                                updateUI(user);
                    Intent it = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(it);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
//                                        Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignUpActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                updateUI(null);
                }
            }).addOnFailureListener(e -> {
                String error = e.getMessage();
                Log.e(TAG,error);
            });
        } else {
            Toast.makeText(SignUpActivity.this, "Email or Password not match !",
                    Toast.LENGTH_SHORT).show();
        }


    }
}