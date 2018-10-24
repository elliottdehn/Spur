package com.gregory.spur;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreateEventActivity extends AppCompatActivity {
    Button mbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);


        mbutton = (Button) findViewById(R.id.ButtonCreate);


    }
}
