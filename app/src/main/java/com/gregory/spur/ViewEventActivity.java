package com.gregory.spur;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gregory.spur.domain.Event;
import com.gregory.spur.domain.User;
import com.gregory.spur.services.EventService;
import com.gregory.spur.services.UserService;

import java.util.Objects;

public class ViewEventActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MODIFY_EVENT = 0;
    private static final String EXTRA_EVENT_ID = "event_id";
    private static final String EXTRA_USER_ID = "user_id";
    private static final String TAG = "ViewEventActivity";
    private String mEventId;
    private String mCreatorId;
    private String mUserId;
    private EventService mEventService;
    private UserService mUserService;
    private Event mEvent;
    private TextView mEventTitle;
    private TextView mEventDescription;
    private TextView mEventCreator;
    private Button mDelete;
    private Button mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        mEventService = new EventService();
        mUserService = new UserService();

        mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        mEventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        getEventInfo();

        mEventTitle = findViewById(R.id.event_title);
        mEventDescription = findViewById(R.id.event_description);

        mEventCreator = findViewById(R.id.event_Creator);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if(!isUserEventCreator()){
                    Toast.makeText(getApplicationContext(),
                            "You are not the event creator, cannot delete event", Toast.LENGTH_LONG).show();
                    return true;
                }

                // User chose the delete event option, delete the event from the database
                mEventService.deleteEvent(mEventId, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            // Delete successful, return to map activity
                            Log.d(TAG, "Deleted event " + mEventId);
                            Toast.makeText(getApplicationContext(), "Deleted event " + mEvent.getName(), Toast.LENGTH_SHORT).show();
                            Intent deleteData = new Intent();
                            deleteData.putExtra("event_deleted", true);
                            deleteData.putExtra("deleted_id", mEventId);
                            setResult(RESULT_OK, deleteData);
                            finish();
                        } else {
                            // Delete failed, show generic error to user and log real error
                            Log.e(TAG, "Failed to delete event: ", task.getException());
                        }
                    }
                });

                return true;
            case R.id.action_edit:
                if(!isUserEventCreator()){
                    Toast.makeText(getApplicationContext(),
                            "You are not the event creator, cannot modify event", Toast.LENGTH_LONG).show();
                    return true;
                }

                // User chose edit event option, launch the activity to modify the event
                Intent intent = CreateEventActivity.newIntent(getApplicationContext(),
                        mEvent.getLoc().getLatitude(),
                        mEvent.getLoc().getLongitude(),
                        mEventId,
                        mUserId);
                startActivityForResult(intent, REQUEST_CODE_MODIFY_EVENT);
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

        // Create the menu from the menu.xml layout file
        getMenuInflater().inflate(R.menu.menu, menu);
        mDelete = findViewById(R.id.action_delete);
        mEdit = findViewById(R.id.action_edit);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_MODIFY_EVENT){
            if(data == null){
                return;
            }
            if(data.getBooleanExtra("event_modified", false)){
                getEventInfo();
            }
        }
    }

    private void getEventInfo(){
        if(mEventId != null){
            mEventService.getEvent(mEventId, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Event event = document.toObject(Event.class);
                            mEvent = event;
                            mCreatorId = event.getCreator().getId();
                            getCreatorInfo();
                            String title = event.getName();
                            String desc = event.getDesc();
                            mEventTitle.setText(title);
                            mEventDescription.setText(desc);
                        } else {
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } else {
            Log.e(TAG, "No event id provided to getEventInfo()");
            Toast.makeText(getApplicationContext(), "No event found", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isUserEventCreator(){
        if (mCreatorId == null) {
            Log.e(TAG, "No creator found, cannot check permissions");
            return false;
        }

        if (mUserId == null){
            Log.e(TAG, "No current user found, cannot check permissions");
            return false;
        }

        if (!Objects.equals(mCreatorId.trim(), mUserId.trim())) {
            Log.d(TAG, "Current user: " + mUserId + ", Creator: " + mCreatorId);
            Log.d(TAG, "Creator id doesn't match user id");

            return false;
        } else {
            Log.d(TAG, "Current user is event creator, they can do anything");
            return true;
        }
    }

    private void getCreatorInfo(){
        if(mCreatorId != null){
            mUserService.getUser(mCreatorId, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            mEventCreator.setText(user.getUsername());
                        } else {
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } else {
            Log.e(TAG, "No creator id provided to getCreatorInfo()");
            Toast.makeText(getApplicationContext(), "No event creator found", Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent newIntent(Context packageContext, String eventId, String userId){
        Intent intent = new Intent(packageContext, ViewEventActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }
}
