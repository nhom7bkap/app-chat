package com.team7.app_chat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team7.app_chat.ui.settings.SettingsFragment;

public class ChangePassActivity extends AppCompatActivity {
    private EditText edtNewPass , edtConfPass;
    private Button btnChange;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        initUi();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChangePassword();
            }
        });
    }

    private void initUi(){
        progressDialog = new ProgressDialog(this);
        edtNewPass = findViewById(R.id.formPassword);
        edtConfPass = findViewById(R.id.formRePassword);
        btnChange = findViewById(R.id.btnChange);
    }
    private void onClickChangePassword(){
        String strPass = edtNewPass.getText().toString().trim();
        String strCfPass = edtConfPass.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.updatePassword(strPass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangePassActivity.this, "Change Password Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangePassActivity.this, SettingsFragment.class);
                                startActivity(intent);

                            }
                        }
                    });

    }
}