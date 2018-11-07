package com.gregory.spur;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.domain.User;
import com.gregory.spur.services.UserService;

public class ProfileView extends AppCompatActivity {

    private static final String EXTRA_USER_ID = "user_id";
    private static final String TAG = "ProfileView";

    private UserService mUserService = new UserService();
    private String mUserId;
    private User mUser;
    private TextView mUsernameText;
    private TextView mFullNameText;
    private TextView mAgeText;
    private TextView mGenderText;
    private EditText mBioText;
    private Button mAddReviewButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        getUserInfo();

        mUsernameText = findViewById(R.id.user_text);
        mFullNameText = findViewById(R.id.full_name_text);
        mAgeText = findViewById(R.id.age_text);
        mGenderText = findViewById(R.id.gender_text);
        mBioText = findViewById(R.id.bio_text);
        mAddReviewButton = findViewById(R.id.add_review_button);
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

    private void getUserInfo(){
        if(mUserId != null){
            mUserService.getUser(mUserId, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot userSnapshot = task.getResult();
                        mUser = userSnapshot.toObject(User.class);
                        updateUIWithUser();
                    } else {
                        Log.e(TAG, "Getting user info failed: ", task.getException());
                    }
                }
            });
        } else {
            Log.e(TAG, "No current user id provided");
        }
    }
}
