package com.example.crema.malproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by crema on 16/09/2016.
 */
public class trailer_adapter extends ArrayAdapter<trailer> {

        public trailer_adapter(Activity context, List<trailer> Trailer) {
            super(context, 0, Trailer);
        }


        @Override

        public View getView(int position, View convertView, ViewGroup parent) {
            // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position

            trailer trailers =getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer, parent, false);
            }

            TextView titleview = (TextView) convertView.findViewById(R.id.textView2);
            titleview.setText(trailers.gettrailer_name());

            return convertView;
        }


}
