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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.domain.Attendee;
import com.gregory.spur.domain.Event;
import com.gregory.spur.domain.User;
import com.gregory.spur.services.EventService;
import com.gregory.spur.services.UserService;

import java.util.List;
import java.util.Objects;

public class ViewEventActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MODIFY_EVENT = 0;
    private static final String EXTRA_EVENT_ID = "event_id";
    private static final String EXTRA_USER_ID = "user_id";
    private static final String TAG = "ViewEventActivity";
    private String mEventId;
    private String mCreatorId;
    private String mUserId;
    private User mUser;
    private EventService mEventService = new EventService();
    private UserService mUserService = new UserService();
    private Event mEvent;
    private TextView mEventTitle;
    private TextView mEventDescription;
    private TextView mEventCreator;
    private EditText mAttendees;
    private MenuItem mDelete;
    private MenuItem mEdit;
    private MenuItem mAttendEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        // get passed in data
        mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        mEventId = getIntent().getStringExtra(EXTRA_EVENT_ID);

        // query for all necessary data from database
        getEventAttendees();
        getEventInfo();
        getCurrentUser();

        mEventTitle = findViewById(R.id.event_title);
        mEventDescription = findViewById(R.id.event_description);
        mEventCreator = findViewById(R.id.event_Creator);
        mEventCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCreatorId != null){
                    Intent intent = ProfileView.newIntent(getApplicationContext(), mCreatorId);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Creator info hasn't loaded yet, can't view profile", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Creator info hasn't loaded yet, can't view profile");
                }

            }
        });
        mAttendees = findViewById(R.id.event_attendees);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
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
                            // Delete failed, show error to user and log stack trace
                            Log.e(TAG, "Failed to delete event: ", task.getException());
                            Toast.makeText(getApplicationContext(),
                                    "Failed to delete event: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            case R.id.action_edit:
                // User chose edit event option, launch the activity to modify the event
                Intent intent = CreateEventActivity.newIntent(getApplicationContext(),
                        mEvent.getLoc().getLatitude(),
                        mEvent.getLoc().getLongitude(),
                        mEventId,
                        mUserId);
                startActivityForResult(intent, REQUEST_CODE_MODIFY_EVENT);
                return true;

            case R.id.action_attend:
                // User chose attend event option, add them as an attendee of the event
                if (mEventId != null && mUser != null && mUserId != null){
                    try {
                        mEventService.addAttendee(mEventId, mUser, mUserId, new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "You are now attending the event!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e(TAG, "Failed to add user " + mUserId + " to event " + mEventId + ": ", task.getException());
                                    Toast.makeText(getApplicationContext(), "Failed to add you to the event: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (IllegalArgumentException e){
                        Toast.makeText(getApplicationContext(), "You are already attending this event", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d(TAG, "Cannot add attendee, don't have all data yet");
                }
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
        mDelete = menu.findItem(R.id.action_delete);
        mEdit = menu.findItem(R.id.action_edit);
        mAttendEvent = menu.findItem(R.id.action_attend);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        if(!isUserEventCreator()){
            mDelete.setVisible(false);
            mEdit.setVisible(false);
        } else {
            mAttendEvent.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
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

    private void getCurrentUser(){
        if(mUserId != null){
            mUserService.getLoggedInUser(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size() == 0){
                        Log.e(TAG, "No logged in user found");
                    } else {
                        DocumentSnapshot userSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        mUser = userSnapshot.toObject(User.class);
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error getting current user: ", e);
                }
            });
        } else {
            Log.e(TAG, "No current user id provided");
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

                            // After the creator id has loaded, reload the options menu to reflect
                            // the available options for the current user
                            invalidateOptionsMenu();

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

    private void getEventAttendees(){
        if(mEventId != null){
            mEventService.getEventAttendees(mEventId, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        QuerySnapshot result = task.getResult();
                        List<DocumentSnapshot> attendeeSnapshots;
                        if (result.size() > 0){
                            attendeeSnapshots = result.getDocuments();
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < attendeeSnapshots.size(); i++){
                                Attendee attendee = attendeeSnapshots.get(i).toObject(Attendee.class);
                                Log.d(TAG, "Event " + mEventId + " attendee: " + attendee.getUsername());
                                builder.append(attendee.getUsername());
                                if (i != attendeeSnapshots.size() - 1){
                                    builder.append(", ");
                                }
                            }
                            mAttendees.setText(builder.toString());
                        } else {
                            Log.d(TAG, "No attendees for event " + mEventId);
                        }
                    } else {
                        Log.e(TAG, "Get event attendees failed: ", task.getException());
                    }
                }
            });
        } else {
            Log.e("TAG", "No event id provided to getEventAttendees()");
        }
    }

    private boolean isUserEventCreator(){
        if (mCreatorId == null) {
            Log.d(TAG, "No creator found, cannot check permissions");
            return false;
        }

        if (mUserId == null){
            Log.d(TAG, "No current user found, cannot check permissions");
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
