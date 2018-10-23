package com.gregory.spur.services;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.domain.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventService {

    private FirebaseFirestore db;

    public EventService(){
        this.db = FirebaseFirestore.getInstance();
    }

    public void createEvent(Event event, OnCompleteListener listener) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("name", event.getTitle());
        docData.put("description", event.getDescription());
        docData.put("creator", event.getCreator());
        docData.put("loc", event.getLocation());
        docData.put("min", event.getMin());
        docData.put("max", event.getMax());
        docData.put("romantic", event.isRomantic());
        docData.put("vis", event.getVisibility());
        docData.put("start", event.getStartTime());
        docData.put("end", event.getEndTime());
        db.collection("cities").document("LA").set(docData).addOnCompleteListener(listener);
    }

    public void updateEvent(Event event, OnCompleteListener listener) {
        createEvent(event, listener); //in this case, updating == creating
    }

    public void getEvent(String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("events").document(eventId).get().addOnCompleteListener(listener);
    }

    public void deleteEvent(String eventId, OnCompleteListener listener) {
        db.collection("events").document(eventId).delete().addOnCompleteListener(listener);
    }


    public void getEvents(int age, String gender, boolean romantic, OnCompleteListener<QuerySnapshot> listener) {

    }

    public void getEvents(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("events").get().addOnCompleteListener(listener);
    }


    public static List<Event> listEvents(QuerySnapshot qs){
        return  new ArrayList<Event>();
    }


}
