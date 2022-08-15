package com.team7.app_chat.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.team7.app_chat.R;

public class ProgressButton {
    private CardView cardView;
    private ConstraintLayout constraintLayout;
    private TextView textView;
    private ProgressBar progressBar;
    private String btnText;

    Animation fade_in;

    public ProgressButton(Context ct, View view,String btnTitle) {

        this.cardView = view.findViewById(R.id.cardView);
        this.constraintLayout = view.findViewById(R.id.constraintLayout);
        this.textView = view.findViewById(R.id.btnTextView);
        this.btnText = btnTitle;
        textView.setText(btnText);
        this.progressBar = view.findViewById(R.id.progessBar);
    }

    public void buttonActivated() {
        progressBar.setVisibility(View.VISIBLE);
        textView.setText("Please wait...");
    }

    public void buttonFailed() {
        constraintLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        textView.setText(btnText);
    }

    public void buttonFinished() {
//        constraintLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        textView.setText("Done");
    }
}
