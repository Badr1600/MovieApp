package movieapp.com.movieapp.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import movieapp.com.movieapp.BuildConfig;
import movieapp.com.movieapp.R;
import movieapp.com.movieapp.activities.MainActivity;
import movieapp.com.movieapp.adapter.ReviewAdapter;
import movieapp.com.movieapp.adapter.ReviewItem;
import movieapp.com.movieapp.adapter.TrailerAdapter;
import movieapp.com.movieapp.adapter.TrailerItem;
import movieapp.com.movieapp.movies.details.Movie;
import movieapp.com.movieapp.movies.details.Review;
import movieapp.com.movieapp.movies.details.ReviewResponse;
import movieapp.com.movieapp.movies.details.Trailer;
import movieapp.com.movieapp.movies.details.TrailerResponse;
import movieapp.com.movieapp.utils.AppController;
import movieapp.com.movieapp.utils.Constants;
import movieapp.com.movieapp.utils.DatabaseHandler;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    public static final String TAG = "async_task";

    private DatabaseHandler db ;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    public static ListView listViewTrailers;
    private ListView listViewReviews;

    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView movieLength;
    private TextView movieDate;
    private TextView movieRate;
    private TextView movieVote;
    private TextView movieInfo;
    private Button favButton;

    public static Movie detailedMovie;

    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState != null){
            MainActivity.mTwoPane = savedInstanceState.getBoolean("mTwoPane");
            MainActivity.mTwoPaneHelp = savedInstanceState.getBoolean("mTwoPaneHelp");
        }
        final View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        listViewTrailers = (ListView) rootView.findViewById(R.id.listViewTrailer);
        listViewReviews = (ListView) rootView.findViewById(R.id.listViewReviews);
        favButton = (Button) rootView.findViewById(R.id.fav_button);
        moviePoster = (ImageView) rootView.findViewById(R.id.imageView_details_poster);
        movieTitle = (TextView) rootView.findViewById(R.id.textView_large_movie_title);
        movieLength = (TextView) rootView.findViewById(R.id.textView_movie_length);
        movieDate = (TextView) rootView.findViewById(R.id.textView_movie_date);
        movieRate = (TextView) rootView.findViewById(R.id.textView_movie_rating);
        movieVote = (TextView) rootView.findViewById(R.id.textView_movie_votes);
        movieInfo = (TextView) rootView.findViewById(R.id.textView_movie_info);

        db = new DatabaseHandler(rootView.getContext());
        setListViewHeightBasedOnChildren(listViewTrailers);
        setListViewHeightBasedOnChildren(listViewReviews);

        listViewTrailers.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        listViewReviews.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favButton.getText().equals(getResources().getString(R.string.fav_button_un_mark))) {
                    db.deleteMovie(detailedMovie);
                    Log.d("Removed: ", "Movie ID:" + detailedMovie.getTitle());
                    favButton.setText(getResources().getString(R.string.fav_button_mark));
                    Snackbar snackbar = Snackbar
                            .make(favButton, getResources().getString(R.string.un_mark_action_msg), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    db.addMovie(detailedMovie);
                    Snackbar snackbar = Snackbar
                            .make(favButton, getResources().getString(R.string.mark_action_msg), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    favButton.setText(getResources().getString(R.string.fav_button_un_mark));
                    Log.d("Insert: ", "Inserting ..");
                    Log.d("Insert: ", "Movie ID:" + detailedMovie.getTitle());
                }
            }
        });

        listViewTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TrailerItem item = (TrailerItem) parent.getItemAtPosition(position);
                Trailer trailer = item.getTrailer();
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                startActivity(intent);
                Log.i("vid_url", "http://www.youtube.com/watch?v=" + trailer.getKey());
            }
        });

        listViewReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ReviewItem item = (ReviewItem) parent.getItemAtPosition(position);
                Review review = item.getReview();
                String url = review.getUrl();
                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    url = "http://" + url;
                }
                Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(openUrlIntent);
            }
        });

        return getView(rootView);
    }

    private View getView(View rootView){
        if(MainActivity.mTwoPane && MainActivity.mTwoPaneHelp) {

            Bundle bundle = getArguments();
            Picasso.with(getContext()).load(Constants.POSTER_URL + bundle.getString("moviePoster")).into(moviePoster);
            movieTitle.setText(bundle.getString("movieTitle"));
            movieLength.setText("Vote Count: " + bundle.getString("movieLength"));
            movieDate.setText("Release Date: " + bundle.getString("movieDate"));
            movieRate.setText("Popularity: " + bundle.getString("movieRate"));
            movieVote.setText("Vote Avg: " + bundle.getString("movieVote"));
            movieInfo.setText(bundle.getString("movieInfo"));
            Movie movie = new Movie(
                    bundle.getInt("movieID"),
                    bundle.getString("movieTitle"),
                    bundle.getString("moviePoster"),
                    Integer.parseInt(bundle.getString("movieLength")),
                    bundle.getString("movieDate"),
                    Double.parseDouble(bundle.getString("movieRate")),
                    Double.parseDouble(bundle.getString("movieVote")),
                    bundle.getString("movieInfo"));
            detailedMovie = movie;

            ArrayList<Movie> movies = db.getAllMovies();
            for (int i = 0; i < movies.size(); i++) {
                if (movies.get(i).getId() == movie.getId()) {
                    favButton.setText(getResources().getString(R.string.fav_button_un_mark));
                    break;
                }
            }

            String url_trailers = Constants.API_URL + bundle.getInt("movieID") + "/videos" + Constants.API_KEY + BuildConfig.API_KEY;
            Log.i("Video_Trailers_URL", url_trailers);

            String url_reviews = Constants.API_URL + bundle.getInt("movieID") + "/reviews" + Constants.API_KEY + BuildConfig.API_KEY;
            Log.i("Video_Reviews_URL", url_reviews);

            String[] urls = {url_trailers, url_reviews};

            getMovieDetails(urls);

            return rootView;

        } else {
            if (MainActivity.mTwoPane) {
                return rootView;
            } else {
                Intent intent = getActivity().getIntent();
                Movie movie = intent.getExtras().getParcelable("movie");
                detailedMovie = movie;

                ArrayList<Movie> movies = db.getAllMovies();
                for (int i = 0; i < movies.size(); i++) {
                    if (movies.get(i).getId() == movie.getId()) {
                        favButton.setText(getResources().getString(R.string.fav_button_un_mark));
                        break;
                    }
                }

                Picasso.with(getContext()).load(Constants.POSTER_URL + movie.getPoster_path()).into(moviePoster);
                movieTitle.setText(movie.getTitle());
                movieLength.setText("Vote Count: " + movie.getVote_count());
                movieDate.setText("Release Date: " + movie.getRelease_date());
                movieRate.setText("Popularity: " + movie.getPopularity());
                movieVote.setText("Vote Avg: " + movie.getVote_average());
                movieInfo.setText(movie.getOverview());

                String url_trailers = Constants.API_URL + movie.getId() + "/videos" + Constants.API_KEY + BuildConfig.API_KEY;
                Log.i("Video_Trailers_URL", url_trailers);

                String url_reviews = Constants.API_URL + movie.getId() + "/reviews" + Constants.API_KEY + BuildConfig.API_KEY;
                Log.i("Video_Reviews_URL", url_reviews);

                String[] urls = {url_trailers, url_reviews};

                getMovieDetails(urls);

                return rootView;
            }
        }
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mTwoPane", MainActivity.mTwoPane);
        outState.putBoolean("mTwoPaneHelp", MainActivity.mTwoPaneHelp);
    }

    private void getMovieDetails(String [] urls){
        String tag_json_obj= "json_obj_req";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urls[0], null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        List<Trailer> responseTrailer = TrailerResponse.parseTrailers(response.toString());
                        ArrayList<TrailerItem> tempImageItems = getData(responseTrailer);

                        trailerAdapter = new TrailerAdapter(getActivity(), R.layout.layout_trailer, tempImageItems);
                        trailerAdapter.notifyDataSetChanged();
                        listViewTrailers.setAdapter(trailerAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+ error.getMessage());
            }
        });

        JsonObjectRequest jsonObjReq2 = new JsonObjectRequest(Request.Method.GET,
                urls[1], null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        List<Review> responseReview = ReviewResponse.parseReviews(response.toString());
                        ArrayList<ReviewItem> tempReviewItems = getDataReviews(responseReview);

                        reviewAdapter = new ReviewAdapter(getActivity(), R.layout.layout_review, tempReviewItems);
                        reviewAdapter.notifyDataSetChanged();
                        listViewReviews.setAdapter(reviewAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+ error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        AppController.getInstance().addToRequestQueue(jsonObjReq2, tag_json_obj);
    }

    private ArrayList<TrailerItem> getData(List<Trailer> moviesTrailers) {
        final ArrayList<TrailerItem> trailerItems = new ArrayList<>();
        for (int i = 0; i < moviesTrailers.size(); i++) {
            trailerItems.add(new TrailerItem(moviesTrailers.get(i)));
        }

        return trailerItems;
    }

    private ArrayList<ReviewItem> getDataReviews(List<Review> moviesReviews) {
        final ArrayList<ReviewItem> reviewItems = new ArrayList<>();
        for (int i = 0; i < moviesReviews.size(); i++) {
            reviewItems.add(new ReviewItem(moviesReviews.get(i)));
        }
        return reviewItems;
    }

}
