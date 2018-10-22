package com.gregory.spur.com.gregory.spur.services;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.com.gregory.spur.domain.Event;

public interface IEventService {
    void createEvent(Event event);
    void updateEvent(Event event);
    void getEvent(String eventId);
    void getEvents(int age, String gender, boolean romantic);
    void getEvents(OnCompleteListener<QuerySnapshot> listener);
    void deleteEvent(Event event);
}
