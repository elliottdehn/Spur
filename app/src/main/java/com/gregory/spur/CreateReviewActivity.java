package com.gregory.spur;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

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

    private static final String TARGET = "TARGET";
    private static final String TAG = "CreateReviewActivity";
    private UserService mUserService;
    private String target;
    private String owner;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_create_review);

        mUserService = UserService.getInstance();

        mButton = findViewById(R.id.ButtonSubmitReview);
        mButton.setOnClickListener(this);
        mButton.setEnabled(false);
        mUserService.getLoggedInUser(this, this);

        target = getIntent().getStringExtra(TARGET);
    }

    @Override
    public void onClick(View v) {
        if(internet_connection()) {
            switch (v.getId()) {
                case R.id.ButtonSubmitReview:
                    EditText description = findViewById(R.id.EditTextReviewDescription);
                    String comment = description.getText().toString();
                    Boolean liked = ((Switch) findViewById(R.id.SwitchLike)).isChecked();
                    //create and send review to database

                    Date now = new Date();
                    Timestamp ts = new Timestamp(now);
                    Review review = new Review(owner, target, liked, comment, ts);
                    ReviewService rs = ReviewService.getInstance();
                    rs.createReview(review, this);

                    break;
            }
        } else {
            Toast.makeText(this,"No internet", Toast.LENGTH_SHORT);
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
            // Return to profile view
            setResult(RESULT_OK);
            finish();
        } else {
            Log.e(TAG, "Failed to submit review: ", task.getException());
            Toast.makeText(getApplicationContext(), "Failed to submit review: " + task.getException(), Toast.LENGTH_SHORT).show();
        }
    }
    boolean internet_connection(){
        //Check if connected to internet, output accordingly
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
