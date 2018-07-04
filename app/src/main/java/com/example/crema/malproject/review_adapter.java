package com.example.crema.malproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by crema on 28/09/2016.
 */
public class review_adapter extends ArrayAdapter<review> {

    public review_adapter(Activity context, List<review> Review) {
        super(context, 0, Review);
    }


    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position

        review reviews =getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review, parent, false);
        }

        TextView titleview = (TextView) convertView.findViewById(R.id.textView3);
        titleview.setText(reviews.getreview());

        return convertView;
    }
}

