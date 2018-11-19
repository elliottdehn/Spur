package com.gregory.spur;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.domain.Review;
import com.gregory.spur.domain.ReviewAdapter;
import com.gregory.spur.domain.User;
import com.gregory.spur.services.ReviewService;
import com.gregory.spur.services.UserService;

import java.util.ArrayList;
import java.util.List;

public class ProfileView extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_USER_ID = "user_id";
    private static final String TAG = "ProfileView";
    private static final int REQUEST_ADD_REVIEW = 1;

    private UserService mUserService;
    private ReviewService mReviewService;
    private String mUserId;
    private User mUser;
    private TextView mUsernameText;
    private TextView mFullNameText;
    private TextView mAgeText;
    private TextView mGenderText;
    private TextView mBioText;
    private Button mAddReviewButton;
    private ListView mReviewsList;
    private ReviewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        mUserService = UserService.getInstance();
        mReviewService = ReviewService.getInstance();

        mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        getUserInfo();

        mUsernameText = findViewById(R.id.user_text);
        mFullNameText = findViewById(R.id.full_name_text);
        mAgeText = findViewById(R.id.age_text);
        mGenderText = findViewById(R.id.gender_text);
        mBioText = findViewById(R.id.bio_text);
        mAddReviewButton = findViewById(R.id.add_review_button);
        mAddReviewButton.setOnClickListener(this);
        mReviewsList = findViewById(R.id.reviews_list);
    }

    @Override
    public void onClick(View v){
        if(v.getId() == mAddReviewButton.getId()){
            String userPath = mUserService.createRefToUser(mUserId).getPath();

            Intent intent = CreateReviewActivity.newIntent(getApplicationContext(), userPath);
            startActivityForResult(intent, REQUEST_ADD_REVIEW);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_REVIEW){
            if(resultCode == RESULT_OK){
                // User successfully reviewed the profile being viewed, update the reviews list
                getReviewsForUser();
            }
        }
    }

    public static Intent newIntent(Context context, String userId){
        Intent intent = new Intent(context, ProfileView.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    private void updateUIWithUser(){
        String username = mUser.getUsername() + "'s profile";
        mUsernameText.setText(username);
        String fullName = mUser.getFirst() + " " + mUser.getLast();
        mFullNameText.setText(fullName);
        mAgeText.setText(Double.toString(mUser.getAge()));
        mGenderText.setText(mUser.getGender());
        mBioText.setText(mUser.getBio());
    }

    private void getReviewsForUser(){
        if(mUserId != null){
            DocumentReference userRef = mUserService.createRefToUser(mUserId);
            String path = userRef.getPath();
            mReviewService.getReviewsAbout(path, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        QuerySnapshot result = task.getResult();
                        List<Review> newReviews = result.toObjects(Review.class);
                        if(newReviews.size() == 0){
                            Log.d(TAG, "No reviews for user " + mUser.getUsername());
                        } else {
                            if(mAdapter == null){
                                mAdapter = new ReviewAdapter(getApplicationContext(), R.layout.content_review_layout, newReviews);
                                mReviewsList.setAdapter(mAdapter);
                            } else {
                                mAdapter.clear();
                                mAdapter.addAll(newReviews);
                            }
                        }
                    } else {
                        Log.e(TAG, "Get reviews for user failed", task.getException());
                    }
                }
            });
        } else {
            Log.e(TAG, "No user id provided to getReviewsForUser()");
        }
    }

    private void getUserInfo(){
        if(mUserId != null){
            mUserService.getUser(mUserId, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot userSnapshot = task.getResult();
                        mUser = userSnapshot.toObject(User.class);
                        getReviewsForUser();
                        updateUIWithUser();
                    } else {
                        Log.e(TAG, "Getting user info failed: ", task.getException());
                    }
                }
            });
        } else {
            Log.e(TAG, "No user id provided to getUserInfo()");
        }
    }
}
