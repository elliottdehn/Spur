package com.gregory.spur;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private String mCurrentUserId;
    private User mCurrentUser;
    private EventService mEventService;
    private UserService mUserService;
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

        mUserService = UserService.getInstance();
        mEventService = EventService.getInstance();

        // get passed in data
        mCurrentUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        mEventId = getIntent().getStringExtra(EXTRA_EVENT_ID);

        // query for all necessary data from database
        getEventAttendees();
        getEventInfo();
        getCurrentUser();

        mEventTitle = findViewById(R.id.event_title);
        mEventDescription = findViewById(R.id.event_description);
        mEventCreator = findViewById(R.id.event_Creator);
        mEventCreator.setPaintFlags(mEventCreator.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        mEventCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet_connection()) {
                    if (mCreatorId != null) {
                        Intent intent = ProfileView.newIntent(getApplicationContext(), mCreatorId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Creator info hasn't loaded yet, can't view profile", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Creator info hasn't loaded yet, can't view profile");
                    }
                } else {
                    makeInternetToast();
                }
            }
        });
        mAttendees = findViewById(R.id.event_attendees);
    }

    private void makeInternetToast(){
        Toast.makeText(this,"No internet", Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(internet_connection()) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // User chose the delete event option, delete the event from the database
                    mEventService.deleteEvent(mEventId, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
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
                            mCurrentUserId);
                    startActivityForResult(intent, REQUEST_CODE_MODIFY_EVENT);
                    return true;

                case R.id.action_attend:
                    // User chose attend event option, add them as an attendee of the event
                    if (mEventId != null && mCurrentUser != null && mCurrentUserId != null) {
                        try {
                            mEventService.addAttendee(mEventId, mCurrentUser, mCurrentUserId, new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        // Refresh the list of event attendees
                                        getEventAttendees();
                                        Toast.makeText(getApplicationContext(), "You are now attending the event!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG, "Failed to add user " + mCurrentUserId + " to event " + mEventId + ": ", task.getException());
                                        Toast.makeText(getApplicationContext(), "Failed to add you to the event: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (IllegalArgumentException e) {
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
        } else {
            Toast.makeText(this,"No internet", Toast.LENGTH_SHORT);
        }
        return super.onOptionsItemSelected(item);
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
        if(mCurrentUserId != null){
            mUserService.getUser(mCurrentUserId, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot userSnapshot = task.getResult();
                        mCurrentUser = userSnapshot.toObject(User.class);
                    } else {
                        Log.e(TAG, "Unable to get current user: ", task.getException());
                    }
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

                            mEventTitle.setText(event.getName());
                            mEventDescription.setText(event.getDesc());
                            mEventCreator.setText(event.getCreatorUserame());
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

        if (mCurrentUserId == null){
            Log.d(TAG, "No current user found, cannot check permissions");
            return false;
        }

        if (!Objects.equals(mCreatorId.trim(), mCurrentUserId.trim())) {
            Log.d(TAG, "Current user: " + mCurrentUserId + ", Creator: " + mCreatorId);
            Log.d(TAG, "Creator id doesn't match user id");

            return false;
        } else {
            Log.d(TAG, "Current user is event creator, they can do anything");
            return true;
        }
    }

    public static Intent newIntent(Context packageContext, String eventId, String userId){
        Intent intent = new Intent(packageContext, ViewEventActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
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
