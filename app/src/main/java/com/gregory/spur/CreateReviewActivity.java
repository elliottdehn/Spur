package com.gregory.spur;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.R;
import com.gregory.spur.services.UserService;

public class CreateReviewActivity extends AppCompatActivity implements View.OnClickListener, OnSuccessListener<QuerySnapshot>, OnFailureListener {

    public static final String TARGET = "TARGET";
    private String target;
    private String owner;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button mButton = findViewById(R.id.ButtonSubmitReview);
        mButton.setOnClickListener(this);
        mButton.setEnabled(false);
        UserService us = new UserService();
        us.getLoggedInUser(this, this);

        target = savedInstanceState.getString(TARGET);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ButtonSubmitReview:
                EditText description = findViewById(R.id.EditTextDescription);
                String comment = description.getText().toString();
                Boolean liked = ((Switch) findViewById(R.id.SwitchLike)).isChecked();
                //create and send review to database
                break;
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d("Error", "Failed to fetch logged in user");
    }

    @Override
    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
        owner = queryDocumentSnapshots.getDocuments().get(0).getId();
        mButton.setEnabled(true);
    }
}
