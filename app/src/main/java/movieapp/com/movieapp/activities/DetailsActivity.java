package movieapp.com.movieapp.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import movieapp.com.movieapp.R;
import movieapp.com.movieapp.adapter.ImageItem;
import movieapp.com.movieapp.adapter.TrailerItem;
import movieapp.com.movieapp.fragments.DetailsActivityFragment;
import movieapp.com.movieapp.movies.details.Movie;
import movieapp.com.movieapp.movies.details.Trailer;

public class DetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_details_content, new DetailsActivityFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {
            TrailerItem itemT = (TrailerItem) DetailsActivityFragment.listViewTrailers.getItemAtPosition(0);
            Trailer trailer = itemT.getTrailer();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + trailer.getKey());
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_trailer_url));
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_url)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
