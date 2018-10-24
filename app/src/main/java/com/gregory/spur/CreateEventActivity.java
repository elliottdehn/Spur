package com.gregory.spur;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.gregory.spur.domain.Event;
import com.gregory.spur.services.EventService;

import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity implements OnCompleteListener<DocumentSnapshot>, View.OnClickListener {
    public static final String LONG = "long";
    public static final String LAT = "lat";
    public static final String ID = "id";
    public static final String FIREBASE = "Firebase";

    private Button mButtonCreate;
    private Button mButtonUpdate;

    private String id;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //activity is being launched with
        //an id + lat/long (update)
        //or a lat/long (create)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mButtonUpdate = findViewById(R.id.ButtonUpdate);
        mButtonCreate = findViewById(R.id.ButtonCreate);
        Bundle extras = getIntent().getExtras();
        this.latitude = (double) extras.get(LAT);
        this.longitude = (double) extras.get(LONG);

        if (extras.get("id") != null) {
            this.id = (String) extras.get(ID);
            final EventService es = new EventService();
            es.getEvent(id, this);
            //update an event
            mButtonCreate.setVisibility(View.GONE);
            //prepopulate the fields with existing information
            mButtonUpdate.setOnClickListener(this);
        } else {
            //create an event
            mButtonUpdate.setVisibility(View.GONE);

            mButtonCreate.setOnClickListener(this);
        }
    }

    /**
     *
     * @param datePicker
     * @return a java.util.Date
     */
    private Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    public Date mashDateTime(Date date, int hour, int minute){
        long epoch = date.getTime();
        epoch += 60*minute + 60*60*hour;
        date.setTime(epoch);
        return date;
    }

    @Override

    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if(task.isSuccessful()){
            Event event = task.getResult().toObject(Event.class);

        } else {
            Log.d(FIREBASE, "Failed to grab event when updating it");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ButtonCreate:

                EventService es = new EventService();

                View layout = v.getRootView();
                EditText title = layout.findViewById(R.id.EditTextName);
                EditText desc = layout.findViewById(R.id.EditTextDescription);
                DatePicker date = layout.findViewById(R.id.DatePickerDate);
                TimePicker startTime = layout.findViewById(R.id.TimePickerStartTime);
                TimePicker endTime = layout.findViewById(R.id.TimePickerEndTime);

                String eventTitle = title.getText().toString();
                String description = desc.getText().toString();
                Date start = getDateFromDatePicker(date);
                int startHour = startTime.getHour();
                int startMinute = startTime.getMinute();
                Date end = getDateFromDatePicker(date);
                int endHour = endTime.getHour();
                int endMinute = endTime.getMinute();
                Date startMash = mashDateTime(start, startHour, startMinute);
                Date endMash = mashDateTime(end, endHour, endMinute);
                Timestamp startTimeStamp = new Timestamp(startMash);
                Timestamp endTimeStamp = new Timestamp(endMash);
                GeoPoint loc = new GeoPoint(latitude,longitude);
                //now create an event with:

                Event event = new Event(eventTitle, description, startTimeStamp, endTimeStamp, loc);
                es.createEvent(event);

                break;
            case R.id.ButtonUpdate:
                break;
        }
    }
}
