package com.gregory.spur;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gregory.spur.domain.Event;
import com.gregory.spur.domain.User;
import com.gregory.spur.services.UserService;

import java.util.Map;

public class CreateUserActivity extends AppCompatActivity {

    private static final String TAG = "CreateUserActivity";
    private static final String EXTRA_AUTH_ID = "auth_id";
    private static final String EXTRA_USER_ID = "user_id";

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mUserName;
    private EditText mBio;
    private EditText mAge;
    private EditText mCity;
    private RadioButton mMale;
    private RadioButton mFemale;
    private Button mSave;

    private String mAuthId;
    private String mUserId;
    private UserService mUserService;
    private User mUser;

    private View.OnClickListener saveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(internet_connection()) {
                saveProfile();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        Toast.makeText(getApplicationContext(), "Your profile is missing some information, please edit it", Toast.LENGTH_LONG).show();

        mUserService = UserService.getInstance();

        mAuthId = getIntent().getStringExtra(EXTRA_AUTH_ID);
        mUserId = getIntent().getStringExtra(EXTRA_USER_ID);

        mFirstName = findViewById(R.id.first_name_edittext);
        mLastName = findViewById(R.id.last_name_edittext);
        mUserName = findViewById(R.id.username_edittext);
        mBio = findViewById(R.id.bio_edittext);
        mAge = findViewById(R.id.age_edittext);
        mCity = findViewById(R.id.city_edittext);
        mMale = findViewById(R.id.male_radio_button);
        mFemale = findViewById(R.id.female_radio_button);

        mSave = findViewById(R.id.save_button);
        mSave.setOnClickListener(saveClickListener);

        getUserData();
    }

    public static Intent newIntent(Context packageContext, String authId, String userId) {
        Intent intent = new Intent(packageContext, CreateUserActivity.class);
        intent.putExtra(EXTRA_AUTH_ID, authId);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    private void getUserData(){
        if(mUserId != null){
            mUserService.getUser(mUserId, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            mUser = user;
                            autoFillUI();
                        } else {
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } else {
            Log.e(TAG, "No user id provided to getEventInfo()");
            Toast.makeText(getApplicationContext(), "User data not found", Toast.LENGTH_LONG).show();
        }
    }

    private void saveProfile(){
        User data = readFromUI();
        if (mUserService.isValid(data)){
            mUserService.updateUser(mUserId, data);
            Intent intent = MapsActivity.newIntent(getApplicationContext(), mUserId);
            startActivity(intent);
        } else {
            Log.e(TAG, "User data not valid, aborting update");
            Toast.makeText(getApplicationContext(), "User data not valid", Toast.LENGTH_LONG).show();
        }
    }

    private User readFromUI(){
        User user = new User();
        user.setFirst(mFirstName.getText().toString());
        user.setLast(mLastName.getText().toString());
        user.setUsername(mUserName.getText().toString());
        user.setBio(mBio.getText().toString());
        String ageText = mAge.getText().toString();
        double age = Double.parseDouble(ageText);
        user.setAge(age);
        user.setCity(mCity.getText().toString());
        user.setAuthId(mAuthId);

        if (mMale.isChecked()){
            user.setGender("male");
        } else if (mFemale.isChecked()){
            user.setGender("female");
        }

        return user;
    }

    private void autoFillUI(){
        mFirstName.setText(mUser.getFirst());
        mLastName.setText(mUser.getLast());
        mUserName.setText(mUser.getUsername());
        mBio.setText(mUser.getBio());
        double age = mUser.getAge();
        mAge.setText(Double.toString(age));
        mCity.setText(mUser.getCity());
        String gender = mUser.getGender();
        if (gender != null){
            if (gender.toLowerCase() == "male") {
                mMale.setChecked(true);
            } else if (gender.toLowerCase() == "female") {
                mFemale.setChecked(true);
            }
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
