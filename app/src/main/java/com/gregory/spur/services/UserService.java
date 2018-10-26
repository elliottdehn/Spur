package com.gregory.spur.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.gregory.spur.domain.User;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    private static final String TAG = "UserService";
    private FirebaseFirestore db;

    public UserService(){
        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
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

    public void createUser(User user, String firebaseAuthId, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener){
        Map<String, Object> data = new HashMap<>();
        data.put("age", user.getAge());
        data.put("bio", user.getBio());
        data.put("city", user.getCity());
        data.put("first", user.getfName());
        data.put("last", user.getlName());
        data.put("gender", user.getGender());
        data.put("username", user.getUsername());
        data.put("auth_id", firebaseAuthId);
        db.collection("events")
                .add(data)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void getUser(String userId){

    }

    public void updateUser(User user){

    }

    public void deleteUser(User user){

    }
}
