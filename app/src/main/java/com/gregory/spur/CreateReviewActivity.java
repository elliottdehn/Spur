package com.gregory.spur;

import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.R;
import com.gregory.spur.domain.Review;
import com.gregory.spur.services.ReviewService;
import com.gregory.spur.services.UserService;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class CreateReviewActivity extends AppCompatActivity implements View.OnClickListener, OnSuccessListener<QuerySnapshot>, OnFailureListener, OnCompleteListener {

    public static final String TARGET = "TARGET";
    private UserService mUserService = new UserService();
    private String target;
    private String owner;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_create_review);

        mButton = findViewById(R.id.ButtonSubmitReview);
        mButton.setOnClickListener(this);
        mButton.setEnabled(false);
        UserService us = new UserService();
        us.getLoggedInUser(this, this);

        target = getIntent().getStringExtra(TARGET);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ButtonSubmitReview:
                EditText description = findViewById(R.id.EditTextReviewDescription);
                String comment = description.getText().toString();
                Boolean liked = ((Switch) findViewById(R.id.SwitchLike)).isChecked();
                //create and send review to database

                Date now = new Date();
                Timestamp ts = new Timestamp(now);
                Review review = new Review(owner, target, liked, comment, ts);
                ReviewService rs = new ReviewService();
                rs.createReview(review, this);

                //laucnh the map
                Intent intent = MapsActivity.newIntent(getApplicationContext(), owner);
                startActivity(intent);
                break;
        }
    }

    public static Intent newIntent(Context context, String targetUserPath){
        Intent intent = new Intent(context, CreateReviewActivity.class);
        intent.putExtra(TARGET, targetUserPath);
        return intent;
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d("Error", "Failed to fetch logged in user");
    }

    @Override
    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
        owner = queryDocumentSnapshots.getDocuments().get(0).getId();
        owner = mUserService.createRefToUser(owner).getPath();
        mButton.setEnabled(true);
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if(task.isSuccessful()) {
            //launch the map
            Intent intent = MapsActivity.newIntent(getApplicationContext(), owner);
            startActivity(intent);
        }
    }
}
