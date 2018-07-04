package com.example.crema.malproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class DetailsActivity extends AppCompatActivity {

    movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detail_fragment fragment = new detail_fragment();
        Bundle b = new Bundle();
        Intent intent = getIntent();
        movie = (movie) intent.getSerializableExtra("MovieKey");
        b.putSerializable("MovieKey", movie);
        fragment.setArguments(b);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag2, fragment)
                .commit();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
