package com.example.crema.malproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by crema on 26/08/2016.
 */
    public class movie_adapter extends ArrayAdapter<movie> {

        public movie_adapter(Activity context, List<movie> Movie) {
            super(context, 0, Movie);
        }




    @Override


        public View getView(int position, View convertView, ViewGroup parent) {
            // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
            movie movies = getItem(position);


            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_items, parent, false);
            }

            final ImageView imgView = (ImageView) convertView.findViewById(R.id.imageView);
            final String IMAGE_URL = "http://image.tmdb.org/t/p/w185" + movies.getPoster_url();

                Picasso.with(getContext()).load(IMAGE_URL)
                        .into(imgView);


            TextView titleview = (TextView) convertView.findViewById(R.id.textView);
            titleview.setText(movies.getTitle());
            return convertView;
        }


    }
