package com.gregory.spur;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ProfileView extends AppCompatActivity {

    private static final String EXTRA_USER_ID = "user_id";
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        mUserId = getIntent().getStringExtra(EXTRA_USER_ID);


    }

    public static Intent newIntent(Context context, String userId){
        Intent intent = new Intent(context, ProfileView.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }
}
