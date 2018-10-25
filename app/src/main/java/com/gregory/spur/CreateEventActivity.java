package com.gregory.spur;

import android.content.Context;
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

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_LONGITUDE = "long";
    public static final String EXTRA_LATITUDE = "lat";
    public static final String EXTRA_ID = "id";
    private static final int REQUEST_CODE_CREATE_EVENT = 0;
    public static final String TAG = "CreateEventActivity";

    private Button mButtonCreate;
    private Button mButtonUpdate;
    private EditText mTitle;
    private EditText mDescription;
    private DatePicker mDatePicker;
    private TimePicker mStartTime;
    private TimePicker mEndTime;


    private String mEventId;
    private double mLatitude;
    private double mLongitude;
    private EventService mEventService = new EventService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //activity is being launched with
        //an id + lat/long (update)
        //or a lat/long (create)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mButtonUpdate = findViewById(R.id.ButtonUpdate);
        mButtonCreate = findViewById(R.id.ButtonCreate);
        mTitle = findViewById(R.id.EditTextName);
        mDescription = findViewById(R.id.EditTextDescription);
        mDatePicker = findViewById(R.id.DatePickerDate);
        mStartTime = findViewById(R.id.TimePickerStartTime);
        mEndTime = findViewById(R.id.TimePickerEndTime);
        Bundle extras = getIntent().getExtras();

        // If an event id is provided, we are updating an event
        mEventId = (String) extras.get(EXTRA_ID);
        mLatitude = (Double) extras.get(EXTRA_LATITUDE);
        mLongitude = (Double) extras.get(EXTRA_LONGITUDE);

        if (mEventId != null) {
            mEventService.getEvent(mEventId, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                Event event = document.toObject(Event.class);
                                autofillUI(event);
                            } else {
                                Log.e(TAG, "Document with Id " + mEventId + " doesn't exist");
                            }
                        } else {
                            Log.e(TAG, "Failed to get event for Id: ", task.getException());
                        }
                }
            });
            mButtonCreate.setVisibility(View.GONE);
            mButtonUpdate.setOnClickListener(this);
        } else {
            // Otherwise we are creating an event
            mButtonUpdate.setVisibility(View.GONE);
            mButtonCreate.setOnClickListener(this);
        }
    }

    public static Intent newIntent(Context packageContext, double lat, double longitude, String eventId){
        Intent intent = new Intent(packageContext, CreateEventActivity.class);
        intent.putExtra(EXTRA_ID, eventId);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, longitude);
        return intent;
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

    private void autofillUI(Event event){
        mTitle.setText(event.getName());
        mDescription.setText(event.getDesc());

        Timestamp startTime = event.getStart();
        Timestamp endTime = event.getEnd();
        Date startDate = startTime.toDate();
        Date endDate = endTime.toDate();

        int year = startDate.getYear();
        int month = startDate.getMonth();
        int day = startDate.getDay();
        mDatePicker.updateDate(year, month, day);

        mStartTime.setHour(startDate.getHours());
        mStartTime.setMinute(startDate.getMinutes());
        mEndTime.setHour(endDate.getHours());
        mEndTime.setMinute(endDate.getMinutes());
    }

    private Event readEventFromUI(){
        String eventTitle = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        Date start = getDateFromDatePicker(mDatePicker);
        int startHour = mStartTime.getHour();
        int startMinute = mStartTime.getMinute();
        Date end = getDateFromDatePicker(mDatePicker);
        int endHour = mEndTime.getHour();
        int endMinute = mEndTime.getMinute();
        Date startMash = mashDateTime(start, startHour, startMinute);
        Date endMash = mashDateTime(end, endHour, endMinute);
        Timestamp startTimeStamp = new Timestamp(startMash);
        Timestamp endTimeStamp = new Timestamp(endMash);
        GeoPoint loc = new GeoPoint(mLatitude,mLongitude);

        Event event = new Event(eventTitle, description, startTimeStamp, endTimeStamp, loc);
        return event;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ButtonCreate:
                Event newEvent = readEventFromUI();
                mEventService.createEvent(newEvent);
                Intent creationData = new Intent();
                creationData.putExtra("event_created", true);
                setResult(RESULT_OK, creationData);
                finish();
                break;
            case R.id.ButtonUpdate:
                Event modifiedEvent = readEventFromUI();
                mEventService.updateEvent(mEventId, modifiedEvent);
                Intent modificationData = new Intent();
                modificationData.putExtra("event_modified", true);
                setResult(RESULT_OK, modificationData);
                finish();
                break;
        }
    }
}
