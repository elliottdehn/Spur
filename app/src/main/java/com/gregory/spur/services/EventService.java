package com.gregory.spur.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.domain.Attendee;
import com.gregory.spur.domain.Event;
import com.gregory.spur.domain.User;

import java.util.HashMap;
import java.util.Map;

public class EventService {

    private FirebaseFirestore db;
    private UserService userService;
    private static final String TAG = "EventService";
    private static final String EVENTS = "events";
    private static final String ATTENDEES = "attendees";

    public EventService(){
        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        userService = new UserService();
    }

    public void createEvent(Event event) {
        OnSuccessListener<DocumentReference> successListener = new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Event created with Id " + documentReference.getId());
            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failure creating event: ", e);
            }
        };
        createEvent(event, successListener, failureListener);
    }

    public void addAttendee(final String eventId, final User user, String userId){
        addAttendee(eventId, user, userId, new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Added attendee " + user.getUsername() + " to event " + eventId);
                } else {
                    Log.e(TAG, "Failed to add attendee: ", task.getException());
                }
            }
        });
    }

    public void addAttendee(final String eventId, final User user, String userId, OnCompleteListener<DocumentReference> listener) throws IllegalArgumentException{
        // Before adding the attendee, make sure they aren't already attending the event
        if (user.getAttendingEvents().contains(eventId)){
            throw new IllegalArgumentException("User is already attending this event");
        }

        // Update the user model with the new event they're attending
        userService.addAttendingEvent(user, userId, eventId);

        // Update the event with the new attendee
        Attendee attendee = new Attendee(user, userId);
        db.collection(EVENTS).document(eventId).collection(ATTENDEES).add(attendee).addOnCompleteListener(listener);
    }

    public void createEvent(Event event, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener){
        Map<String, Object> docData = new HashMap<>();
        docData.put("name", event.getName());
        docData.put("desc", event.getDesc());
        docData.put("creator", event.getCreator());
        docData.put("loc", event.getLoc());
        docData.put("min", event.getMin());
        docData.put("max", event.getMax());
        docData.put("romantic", event.isRomantic());
        docData.put("vis", event.getVis());
        docData.put("start", event.getStart());
        docData.put("end", event.getEnd());
        db.collection(EVENTS)
                .add(docData)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void updateEvent(String eventId, Event event) {
        db.collection(EVENTS).document(eventId).set(event);
    }

    public void getEvent(String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection(EVENTS).document(eventId).get().addOnCompleteListener(listener);
    }

    public void getEventAttendees(String eventId, OnCompleteListener<QuerySnapshot> listener){
        db.collection(EVENTS).document(eventId)
                .collection(ATTENDEES).get().addOnCompleteListener(listener);
    }

    public void deleteEvent(String eventId, OnCompleteListener<Void> listener) {
        db.collection(EVENTS).document(eventId).delete().addOnCompleteListener(listener);
    }


    public void getEvents(int age, String gender, boolean romantic, OnCompleteListener<QuerySnapshot> listener) {

    }

    public void getEvents(OnCompleteListener<QuerySnapshot> listener) {
        db.collection(EVENTS).get().addOnCompleteListener(listener);
    }

}
