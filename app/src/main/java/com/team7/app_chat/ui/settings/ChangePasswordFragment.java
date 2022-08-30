package com.team7.app_chat.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team7.app_chat.R;
import com.team7.app_chat.components.ProgressButton;
import com.team7.app_chat.ui.contacts.UserProfileFragment;


public class ChangePasswordFragment extends Fragment {
  private TextInputLayout layoutCurrentPass;
  private TextInputLayout layoutNewPass;
  private TextInputLayout layoutConfirmPass;
  private EditText etCurrentPass;
  private EditText etNewPass;
  private EditText etConfirmPass;
private ProgressButton progressButton;
  private FirebaseUser currentUser;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    currentUser = FirebaseAuth.getInstance().getCurrentUser();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_change_password, container, false);

    progressButton = new ProgressButton(view.getContext(), view.findViewById(R.id.btSave), "Update");
    layoutCurrentPass = view.findViewById(R.id.lOldPass);
    layoutNewPass = view.findViewById(R.id.lNewPass);
    layoutConfirmPass = view.findViewById(R.id.lConfirmPass);
    etCurrentPass = view.findViewById(R.id.etOldPass);
    etNewPass = view.findViewById(R.id.etNewPass);
    etConfirmPass = view.findViewById(R.id.etConfirmPass);

    view.findViewById(R.id.btnBack).setOnClickListener(view1 -> {
      NavHostFragment.findNavController(ChangePasswordFragment.this).popBackStack();
    });
    view.findViewById(R.id.btSave).setOnClickListener(v -> {
      progressButton.buttonActivated();
      String currentPass = etCurrentPass.getText().toString();
      String newPass = etNewPass.getText().toString();
      String confirmPass = etConfirmPass.getText().toString();
      if(validate(currentPass, newPass, confirmPass)){
        changePassword(currentPass, newPass, confirmPass);
      }
    });
    return view;
  }



  private boolean validate(String currentPass, String newPass, String confirmPass){
    boolean status = true;
    if(currentPass.trim().length() < 6){
      progressButton.buttonFailed();
      layoutCurrentPass.setError("Mật khẩu phải có ít nhất 6 ký tự");
      status = false;
    } else{
      layoutCurrentPass.setError("");
    }
    if(newPass.length() < 6){
      progressButton.buttonFailed();
      layoutNewPass.setError("Mật khẩu phải có ít nhất 6 ký tự");
      status = false;
    } else{
      layoutNewPass.setError("");
    }
    if(confirmPass.length() < 6){
      progressButton.buttonFailed();
      layoutConfirmPass.setError("Mật khẩu phải có ít nhất 6 ký tự");
      status = false;
    } else if(!confirmPass.equals(newPass)){
      progressButton.buttonFailed();
      layoutConfirmPass.setError("Mật khẩu không khớp");
      status = false;
    } else{
      layoutConfirmPass.setError("");
    }
    return status;
  }
  private void changePassword(String currentPass, String newPass, String confirmPass) {
    AuthCredential credential = EmailAuthProvider.getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), currentPass);
    currentUser.reauthenticate(credential).addOnSuccessListener(runnable -> {
      currentUser.updatePassword(newPass).addOnSuccessListener(unused -> {
        progressButton.buttonFinished();
        NavHostFragment.findNavController(this).popBackStack();
      });

    }).addOnFailureListener(runnable -> {
      progressButton.buttonFailed();
      layoutCurrentPass.setError("Mật khẩu không hợp lệ");
    });
  }
}