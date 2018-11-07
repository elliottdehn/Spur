package com.gregory.spur.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    private static final String TAG = "UserService";
    private static final String USERS = "users";
    private FirebaseFirestore db;

    public UserService(){
        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public boolean isValid(User user){
        if (user.getAuthId() != null && user.getBio() != null && user.getCity() != null
                && user.getFirst() != null && user.getLast() != null && user.getGender() != null
                && user.getUsername() != null && user.getAge() > 0.0 && user.getAttendingEvents() != null){
            return true;
        } else {
            return false;
        }
    }

    public DocumentReference createRefToUser(String userId){
        return db.collection(USERS).document(userId);
    }

    public void createUser(User user, String firebaseAuthId){
        OnSuccessListener<DocumentReference> successListener = new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "User created with Id " + documentReference.getId());
            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failure creating user: ", e);
            }
        };
        createUser(user, firebaseAuthId, successListener, failureListener);
    }

    public void addAttendingEvent(User user, String userId, String eventId){
        user.addAttendingEvent(eventId);
        db.collection(USERS).document(userId).set(user);
    }

    public void createUser(User user, String firebaseAuthId, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener){
        Map<String, Object> data = new HashMap<>();
        data.put("age", user.getAge());
        data.put("bio", user.getBio());
        data.put("city", user.getCity());
        data.put("first", user.getFirst());
        data.put("last", user.getLast());
        data.put("gender", user.getGender());
        data.put("username", user.getUsername());
        data.put("authId", firebaseAuthId);
        data.put("attendingEvents", new ArrayList<>());
        db.collection(USERS)
                .add(data)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void getLoggedInUser(OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String authId = currentUser.getUid();
        Query loggedInUser = db.collection(USERS).whereEqualTo("authId", authId);
        loggedInUser.get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void getUser(String userId, OnCompleteListener<DocumentSnapshot> listener){
        db.collection(USERS).document(userId).get()
                .addOnCompleteListener(listener);
    }

    public void updateUser(String userId, User user){
        db.collection(USERS).document(userId).set(user);
    }

    public void deleteUser(String userId, OnCompleteListener<Void> listener){
        db.collection(USERS).document(userId).delete().addOnCompleteListener(listener);
    }
}
