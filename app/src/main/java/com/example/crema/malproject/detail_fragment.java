package com.example.crema.malproject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;


public class detail_fragment extends Fragment {

    movie mov;
    String mov_id;
    public movie [] mov_list ;
    public trailer []arr;
    public review []arr2;
    public trailer_adapter tr_adap ;
    public ListView trailer_list;
    public review_adapter rev_adap ;
    public ListView review_list;
    database obj;
    movie favourite_movie;
    public movie_adapter mov_adap;
    public GridView movie_grid;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {

        super.onStart();

        Bundle x = getArguments();
        if (x != null) {
            mov = (movie) x.getSerializable("MovieKey");
            mov_id = mov.getMovie_id();
        }

        trailerTask trailerTask = new trailerTask();
        trailerTask.execute(mov_id);

        reviwTask reviwTask= new reviwTask();
        reviwTask.execute(mov_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        obj=new database(getContext().getApplicationContext());
        Bundle x = getArguments();
        if (x != null) {
            mov = (movie) x.getSerializable("MovieKey");
        }
        final View rootView = inflater.inflate(R.layout.fragment_detail_fragment, container, false);

        ScrollView scroll=(ScrollView)rootView.findViewById(R.id.scrollView);
        trailer_list=(ListView)rootView.findViewById(R.id.trailer_list);
        ImageView imgView = (ImageView) rootView.findViewById(R.id.imageView);
        TextView title = (TextView) rootView.findViewById(R.id.title_textview);
        TextView overview = (TextView) rootView.findViewById(R.id.movie_overview_txtview);
        TextView date = (TextView) rootView.findViewById(R.id.movie_date);
        TextView rate = (TextView) rootView.findViewById(R.id.movie_rate);
        final ImageButton imgBtn = (ImageButton) rootView.findViewById(R.id.movie_favourite);
        review_list=(ListView)rootView.findViewById(R.id.review_list);


        trailer_list.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185" + mov.getPoster_url()).into(imgView);
        /*title.setText(mov.getTitle().toString());
        overview.setText(mov.getMovie_overview().toString());
        date.setText(mov.getMovie_date().toString());
        rate.setText(mov.getMovie_rate().toString() + "/10");
*/
        title.setText(mov.getTitle());
        overview.setText(mov.getMovie_overview());
        date.setText(mov.getMovie_date());
        rate.setText(mov.getMovie_rate() + "/10");


        trailer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                trailer SelectedTrailer = arr[position];
                String key = SelectedTrailer.getTrailer_key();
                watchYoutubeVideo(key);
            }
        });

        if (obj.get_movies(mov.getMovie_id()) == null)
        {
            mov.Ismoviefavaourite = "NO";
            imgBtn.setImageResource(android.R.drawable.btn_star_big_off);
        }
        else
        {
            mov.Ismoviefavaourite = "YES";
            imgBtn.setImageResource(android.R.drawable.btn_star_big_on);

        }
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mov.Ismoviefavaourite.equals("NO"))
                {
                    mov.Ismoviefavaourite = "YES";
                    imgBtn.setImageResource(android.R.drawable.btn_star_big_on);
                    obj.add_movie(mov.getMovie_id(),mov.getTitle(),mov.getPoster_url(),mov.getMovie_overview(),mov.getMovie_date(),mov.getMovie_rate());
                    Toast.makeText(getContext(), "movie added to favourites", Toast.LENGTH_SHORT).show();


                }
                else {
                    mov.Ismoviefavaourite = "NO" ;
                    //dbFav.delete(movie);
                    obj.delete_movie(mov.getMovie_id());
                    imgBtn.setImageResource(android.R.drawable.btn_star_big_off);
                    Toast.makeText(getContext(), "removed from favourites", Toast.LENGTH_SHORT).show();

                }
            }
        });


        //Inflate the layout for this fragment
        return rootView;

    }

    public  void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }


    public class trailerTask extends AsyncTask<String, Void, trailer[]> {


        private final String LOG_TAG = trailerTask.class.getSimpleName();

        @Override
        protected trailer[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String MoviesJsonStr = null;

            //Press HERE API_KEY
            final String api_key = "23bcc2e96b68b3935e6b1827fe2413bc";
            try {

                final String BASE_URL = "https://api.themoviedb.org/3/movie/"+ params[0]+"/videos?";
                final String KEY_PARAM = "api_key";

                Uri BuiltUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(KEY_PARAM, api_key)
                        .build();

                URL url = new URL(BuiltUri.toString());
                Log.v("uri=", BuiltUri.toString());
                // Create the request to theMovieDb, and open the connectio
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }

                MoviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "JSON String = " + MoviesJsonStr);
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }



            try {
                trailer arr[] =getdata(MoviesJsonStr);
                return arr;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(trailer[] trailers) {

            tr_adap =new trailer_adapter(getActivity(), Arrays.asList(trailers));
            trailer_list.setAdapter(tr_adap);
        }

        private trailer[] getdata(String TrailersJsonStr)
                throws JSONException {

            final String trailer_name = "name";
            final String trailer_key = "key";


            JSONObject TrailerJson = new JSONObject(TrailersJsonStr);
            JSONArray TrailerArray = TrailerJson.getJSONArray("results");

            arr = new trailer[TrailerArray.length()];

            for (int i = 0; i < TrailerArray.length(); ++i) {

                JSONObject SingleTrailerJson = TrailerArray.getJSONObject(i);
                trailer trailer_obj = new trailer();
                trailer_obj.settrailer_name(SingleTrailerJson.getString(trailer_name));
                trailer_obj.setTrailer_key(SingleTrailerJson.getString(trailer_key));


                arr[i] = trailer_obj;
            }

            return arr;
        }
    }

    public class reviwTask extends AsyncTask<String, Void, review[]> {


        private final String LOG_TAG = reviwTask.class.getSimpleName();

        @Override
        protected review[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String MoviesJsonStr = null;

            //Press HERE API_KEY
            final String api_key = "23bcc2e96b68b3935e6b1827fe2413bc";
            try {

                final String BASE_URL = "https://api.themoviedb.org/3/movie/"+params[0]+"/reviews?";
                final String KEY_PARAM = "api_key";

                Uri BuiltUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(KEY_PARAM, api_key)
                        .build();

                URL url = new URL(BuiltUri.toString());
                Log.v("uri=", BuiltUri.toString());
                // Create the request to theMovieDb, and open the connectio
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }

                MoviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "JSON String = " + MoviesJsonStr);
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }



            try {
                review arr2[] =getdata(MoviesJsonStr);
                return arr2;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(review[] reviews) {

            rev_adap =new review_adapter(getActivity(), Arrays.asList(reviews));
            review_list.setAdapter(rev_adap);
        }

        private review[] getdata(String ReviewsJsonStr)
                throws JSONException {

            final String reviews = "content";

            JSONObject ReviewJson = new JSONObject(ReviewsJsonStr);
            JSONArray ReviewArray = ReviewJson.getJSONArray("results");

            arr2 = new review[ReviewArray.length()];

            for (int i = 0; i < ReviewArray.length(); ++i) {

                JSONObject SingleReviewJson = ReviewArray.getJSONObject(i);
                review review_obj = new review();
                review_obj.setreview(SingleReviewJson.getString(reviews));
                arr2[i] = review_obj;
            }

            return arr2;
        }
    }

}
