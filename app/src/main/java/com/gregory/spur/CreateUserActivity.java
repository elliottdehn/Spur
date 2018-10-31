package com.gregory.spur;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.gregory.spur.domain.User;

public class CreateUserActivity extends AppCompatActivity {

    private static final String EXTRA_AUTH_ID = "auth_id";
    private static final String EXTRA_USER_ID = "user_id";

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mUserName;
    private EditText mAge;
    private EditText mCity;
    private RadioButton mMale;
    private RadioButton mFemale;

    private String mAuthId;
    private String mUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        Toast.makeText(getApplicationContext(), "Your profile is missing some information, please edit it", Toast.LENGTH_LONG).show();

        mAuthId = getIntent().getStringExtra(EXTRA_AUTH_ID);
        mUserId = getIntent().getStringExtra(EXTRA_USER_ID);

        mFirstName = findViewById(R.id.first_name_edittext);
        mLastName = findViewById(R.id.last_name_edittext);
        mUserName = findViewById(R.id.username_edittext);
        mAge = findViewById(R.id.age_edittext);
        mCity = findViewById(R.id.city_edittext);
        mMale = findViewById(R.id.male_radio_button);
        mFemale = findViewById(R.id.female_radio_button);
    }

    public static Intent newIntent(Context packageContext, String authId, String userId) {
        Intent intent = new Intent(packageContext, CreateUserActivity.class);
        intent.putExtra(EXTRA_AUTH_ID, authId);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    private User readFromUI(){
        return new User();
    }
}
