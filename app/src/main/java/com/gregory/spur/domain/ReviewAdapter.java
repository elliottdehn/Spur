package com.gregory.spur.domain;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gregory.spur.R;
import com.gregory.spur.domain.Review;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review> {

    private Context mCtx;
    private List<Review> reviews;

    public ReviewAdapter(@NonNull Context context, int resource, @NonNull List<Review> objects) {
        super(context, resource, objects);
        this.mCtx = context;
        reviews = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mCtx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;

        if(rowView == null) {
            rowView = inflater.inflate(R.layout.content_review_layout, parent, false);
            TextView likeView = (TextView) rowView.findViewById(R.id.TextViewLikeDislike);
            TextView descriptionView = (TextView) rowView.findViewById(R.id.TextViewReviewDescription);
            Review r = reviews.get(position);
            if(!r.isLike()){
                likeView.setText("👎");
            }
            descriptionView.setText(r.getDescription());
        }

        return rowView;
    }
}
