package com.gregory.spur.services;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.domain.Event;

import java.util.ArrayList;
import java.util.List;

public class EventService implements IEventService {

    private FirebaseFirestore db;

    public EventService(){
        this.db = FirebaseFirestore.getInstance();
    }
    @Override
    public void createEvent(Event event) {

    }

    @Override
    public void updateEvent(Event event) {

    }

    @Override
    public void getEvent(String eventId) {

    }

    @Override
    public void getEvents(int age, String gender, boolean romantic) {

    }

    @Override
    public void getEvents(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("events").get().addOnCompleteListener(listener);
    }

    public List<Event> listEvents(QuerySnapshot qs){
        return  new ArrayList<Event>();
    }

    @Override
    public void deleteEvent(Event event) {

    }
}
