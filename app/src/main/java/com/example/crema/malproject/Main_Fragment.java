package com.example.crema.malproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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


public class Main_Fragment extends Fragment {

    public movie []arr;
    public movie [] mov_list ;
    movie favourite_movie;
    public movie_adapter mov_adap;
    public GridView movie_grid;
    database obj;
    boolean Fav_id = false;
    public Main_Fragment() {
        // Required empty public constructor}

    }

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public interface Movielistener {
        public void onItemSelected(movie movie);

    }
    public void update_order()
    {
        FetchMovieDataTask fetchMovieDataTask=  new FetchMovieDataTask();
        String order = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_units_key),getString(R.string.pref_units_popular));
        if(!Fav_id) {
            fetchMovieDataTask.execute(order);
        }
        else
        {
            start_favourite();
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        update_order();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        else  if (id == R.id.favourite) {
            Cursor c = obj.get_all_movies();
            mov_list = new movie[c.getCount()];
            if (c ==null)
            {
                Toast.makeText(getContext(), "No favourites", Toast.LENGTH_LONG).show();
            }
            else
            {
                for (int i = 0 ; i < c.getCount(); i++)
                {
                    mov_list[i] = new movie();
                    mov_list[i].setMovie_id(c.getString(0));
                    mov_list[i].setTitle(c.getString(1));
                    mov_list[i].setPoster_url(c.getString(2));
                    mov_list[i].setMovie_overview(c.getString(3));
                    mov_list[i].setMovie_date(c.getString(4));
                    mov_list[i].setMovie_rate(c.getString(5));
                    mov_list[i].Ismoviefavaourite = "YES";
                    c.moveToNext();
                }

                start_favourite();


            }

        }

        return super.onOptionsItemSelected(item);
    }

    public void start_favourite()
    {
        mov_adap = new movie_adapter(getActivity(), Arrays.asList(mov_list));
        movie_grid.setAdapter(mov_adap);
        movie_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                movie SelecetedMovie = arr[i];
                ((Movielistener) getActivity()).onItemSelected(SelecetedMovie);
            }
        });

    }

    public class FetchMovieDataTask extends AsyncTask<String, Void, movie[]> {

        private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();


        @Override
        protected movie[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String MoviesJsonStr = null;

            //Press HERE API_KEY
            final String api_key = "23bcc2e96b68b3935e6b1827fe2413bc";
            try {

                final String BASE_URL = "https://api.themoviedb.org/3/movie/";
                final String KEY_PARAM = "api_key";

                Uri BuiltUri = Uri.parse(BASE_URL).buildUpon()
                        .appendEncodedPath(params[0])
                        .appendQueryParameter(KEY_PARAM, api_key)
                        .build();

                URL url = new URL(BuiltUri.toString());
                Log.v("uri=",BuiltUri.toString());
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
                return getdata(MoviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(movie[] movies) {
            mov_adap = new movie_adapter(getActivity(), Arrays.asList(movies));
            movie_grid.setAdapter(mov_adap);



        }

        private movie[] getdata(String MoviesJsonStr)
                throws JSONException {

            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String TITLE = "title";
            final String RELEASE_DATE = "release_date";
            final String RATE = "vote_average";
            final String ID = "id";



            JSONObject MoviesJson = new JSONObject(MoviesJsonStr);
            JSONArray MoviesArray = MoviesJson.getJSONArray("results");

            arr = new movie[MoviesArray.length()];

            for (int i = 0; i < MoviesArray.length(); ++i) {

                JSONObject SingleMovieJson = MoviesArray.getJSONObject(i);
                movie movie_obj = new movie();
                movie_obj.setMovie_date(SingleMovieJson.getString(RELEASE_DATE));
                movie_obj.setMovie_id(SingleMovieJson.getString(ID));
                movie_obj.setMovie_overview(SingleMovieJson.getString(OVERVIEW));
                movie_obj.setMovie_rate(SingleMovieJson.getString(RATE));
                movie_obj.setPoster_url(SingleMovieJson.getString(POSTER_PATH));
                movie_obj.setTitle(SingleMovieJson.getString(TITLE));


                arr[i] = movie_obj;
            }

            return arr;
        }
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        obj=new database(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View  rootView;
        if (!haveNetworkConnection(getContext().getApplicationContext()))
        {
            Toast.makeText(getContext().getApplicationContext(),"Sorry,There is no internet connection. " , Toast.LENGTH_SHORT).show();
        }
       /* if (!haveNetworkConnection())
        {
            Toast.makeText(getContext(), "no internet connection", Toast.LENGTH_LONG).show();
             //rootView  = inflater.inflate(R.layout.empty, container, false);
        }*/
        rootView = inflater.inflate(R.layout.fragment_main_, container, false);
        movie_grid = (GridView) rootView.findViewById(R.id.gridView);

        movie_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                movie SelecetedMovie = arr[i];
                ((Movielistener) getActivity()).onItemSelected(SelecetedMovie);
            }
        });

        return rootView;
    }




}
