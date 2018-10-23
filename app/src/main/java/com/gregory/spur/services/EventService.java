package com.gregory.spur.services;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.domain.Event;

import java.util.HashMap;
import java.util.Map;

public class EventService {

    private FirebaseFirestore db;

    public EventService(){
        FirebaseFirestore.setLoggingEnabled(true);
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public void createEvent(Event event, OnCompleteListener<DocumentReference> listener) {
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
        docData.put("attendees",event.getAttendees());
        db.collection("events").add(docData).addOnCompleteListener(listener);
    }

    public void updateEvent(String eventId, Event event) {
        db.collection("events").document(eventId).set(event);
    }

    public void getEvent(String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("events").document(eventId).get().addOnCompleteListener(listener);
    }

    public void deleteEvent(String eventId, OnCompleteListener<Void> listener) {
        db.collection("events").document(eventId).delete().addOnCompleteListener(listener);
    }


    public void getEvents(int age, String gender, boolean romantic, OnCompleteListener<QuerySnapshot> listener) {

    }

    public void getEvents(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("events").get().addOnCompleteListener(listener);
    }

}
