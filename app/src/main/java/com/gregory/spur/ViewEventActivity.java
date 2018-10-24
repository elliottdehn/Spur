package com.gregory.spur;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.gregory.spur.domain.Event;
import com.gregory.spur.services.EventService;

public class ViewEventActivity extends AppCompatActivity implements OnCompleteListener<DocumentSnapshot> {

    private static final String EXTRA_EVENT_ID = "com.gregory.spur.event_id";
    private static final String TAG = "ViewEventActivity";
    private String mEventId;
    private EventService mEventService;
    private Event mEvent;
    private TextView mEventTitle;
    private TextView mEventDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        mEventService = new EventService();
        mEventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        getEventInfo();

        mEventTitle = findViewById(R.id.event_title);
        mEventDescription = findViewById(R.id.event_description);
    }

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                Event event = document.toObject(Event.class);
                mEvent = event;
                String title = event.getName();
                String desc = event.getDesc();
                mEventTitle.setText(title);
                mEventDescription.setText(desc);
            } else {
                Log.d(TAG, "No such document");
            }
        } else {
            Log.d(TAG, "get failed with ", task.getException());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_edit:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                intent.putExtra("id", mEventId);
                GeoPoint location = mEvent.getLoc();
                intent.putExtra("lat", location.getLatitude());
                intent.putExtra("long", location.getLongitude());
                intent.putExtra("name", mEvent.getName());
                intent.putExtra("desc", mEvent.getDesc());
                intent.putExtra("start", mEvent.getStart());
                intent.putExtra("end", mEvent.getEnd());
                intent.putExtra("romantic", mEvent.isRomantic());
                intent.putExtra("min", mEvent.getMin());
                intent.putExtra("max", mEvent.getMax());
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void getEventInfo(){
        if(mEventId != null){
            mEventService.getEvent(mEventId, this);
        } else {
            Log.d(TAG, "No event id provided to getEventInfo()");
            Toast.makeText(getApplicationContext(), "No event found", Toast.LENGTH_SHORT);
        }
    }

    public static Intent newIntent(Context packageContext, String eventId){
        Intent intent = new Intent(packageContext, ViewEventActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        return intent;
    }
}
