package com.gregory.spur.services;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.domain.Review;

import java.util.HashMap;
import java.util.Map;

public class ReviewService {
    private FirebaseFirestore db;

    public ReviewService(){
        this.db = FirebaseFirestore.getInstance();
    }

    public void createReview(Review review, OnCompleteListener listener){
        Map<String, Object> docData = new HashMap<>();
        docData.put("author", review.getAuthor());
        docData.put("like", review.isLike());
        docData.put("target", review.getTarget());
        docData.put("desc", review.getDescription());
        docData.put("written", review.getWritten());
        db.collection("reviews").add(docData).addOnCompleteListener(listener);
    }

    public void getReviewsAbout(String userPath, OnCompleteListener<QuerySnapshot> listener){
        db.collection("reviews").whereEqualTo("target", userPath).get().addOnCompleteListener(listener);
    }

    public void getReviewsBy(String userPath, OnCompleteListener<QuerySnapshot> listener){
        db.collection("reviews").whereEqualTo("author", userPath).get().addOnCompleteListener(listener);
    }

    public void updateReview(String path, Review review){
        db.collection("reviews").document(path).set(review);
    }

    public void deleteReview(String path){
        db.collection("reviews").document(path).delete();
    }
}
