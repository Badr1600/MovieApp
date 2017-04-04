package movieapp.com.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import movieapp.com.movieapp.R;
import movieapp.com.movieapp.adapter.ImageItem;
import movieapp.com.movieapp.fragments.DetailsActivityFragment;
import movieapp.com.movieapp.fragments.MainActivityFragment;
import movieapp.com.movieapp.movies.details.Movie;

public class MainActivity extends AppCompatActivity {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static boolean mTwoPane;
    public static boolean mTwoPaneHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragment_details_content) != null){
            mTwoPane = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_details_content, new DetailsActivityFragment());
            } else {
                mTwoPane = false;
            }
        }

        MainActivityFragment.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                Movie movie = item.getMovie();
                if(mTwoPane){
                    Bundle args = new Bundle();
                    args.putInt("movieID", movie.getId());
                    args.putString("moviePoster", movie.getPoster_path());
                    args.putString("movieTitle", movie.getTitle());
                    args.putString("movieLength", "" + movie.getVote_count());
                    args.putString("movieDate", movie.getRelease_date());
                    args.putString("movieRate", "" + movie.getPopularity());
                    args.putString("movieVote", "" + movie.getVote_average());
                    args.putString("movieInfo", movie.getOverview());

                    mTwoPaneHelp = true;
                    DetailsActivityFragment fragment = new DetailsActivityFragment();
                    fragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_details_content, fragment, DETAILFRAGMENT_TAG)
                            .commit();
                } else {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("movie", movie);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mTwoPane", mTwoPane);
        outState.putBoolean("mTwoPaneHelp", mTwoPaneHelp);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTwoPane = savedInstanceState.getBoolean("mTwoPane");
        mTwoPaneHelp = savedInstanceState.getBoolean("mTwoPaneHelp");
    }
}
