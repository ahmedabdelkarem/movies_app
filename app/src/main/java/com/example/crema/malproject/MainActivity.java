package com.example.crema.malproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity implements Main_Fragment.Movielistener {

    public movie [] mov_list ;
    movie favourite_movie;
    public movie_adapter mov_adap;
    public GridView movie_grid;
    database obj;
    boolean IsTablet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout fragment2 = (FrameLayout) findViewById(R.id.frag2);
        obj=new database(getApplicationContext());
        favourite_movie = new movie();

        if (fragment2==null)
        {
            IsTablet=false;
        }
        else
        {
            IsTablet=true;
        }
/*        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity, new Main_Fragment())
                .commit();*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void onItemSelected(movie movie) {

        if (IsTablet) {
            detail_fragment fragment = new detail_fragment();
            Bundle b = new Bundle();
            b.putSerializable("MovieKey", movie);
            fragment.setArguments(b);

            getSupportFragmentManager().beginTransaction().replace(R.id.frag2, fragment).commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);


            Bundle b = new Bundle();
            b.putSerializable("MovieKey", movie);
            intent.putExtras(b);
            startActivity(intent);


        }
    }


}
