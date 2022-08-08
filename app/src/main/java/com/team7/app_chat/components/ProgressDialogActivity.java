package com.team7.app_chat.components;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.team7.app_chat.R;

public class ProgressDialogActivity extends Dialog {


    public ProgressDialogActivity(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_dialog);
    }


}