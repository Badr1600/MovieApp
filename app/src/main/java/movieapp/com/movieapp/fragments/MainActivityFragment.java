package movieapp.com.movieapp.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import movieapp.com.movieapp.BuildConfig;
import movieapp.com.movieapp.R;
import movieapp.com.movieapp.adapter.GridViewAdapter;
import movieapp.com.movieapp.adapter.ImageItem;
import movieapp.com.movieapp.movies.details.Movie;
import movieapp.com.movieapp.movies.details.MoviesResponse;
import movieapp.com.movieapp.utils.AppController;
import movieapp.com.movieapp.utils.Constants;
import movieapp.com.movieapp.utils.DatabaseHandler;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment  {

    private static final String TAG = "main_activity_frag";
    private ProgressDialog pDialog;
    public static GridView gridView;
    private GridViewAdapter gridAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static DatabaseHandler db ;
    private int currentVisibleItem;
    private int currentFirstVisibleItem;
    private int currentScrollState;
    private int pagesCount;
    private int counter;
    private String syncConnPref;
    private boolean popFlag;
    private boolean favFlag;
    private boolean topFlag;
    private boolean rotatedScreen;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_main);
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);

        db = new DatabaseHandler(rootView.getContext());
        pagesCount = 1;
        counter = 0;
        popFlag = false;
        favFlag = false;
        topFlag = false;

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("swipe_action", "onRefresh called from SwipeRefreshLayout");
                        refreshMoviesData();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentScrollState = scrollState;
                isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItem = visibleItemCount;
            }
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        syncConnPref = sharedPref.getString("sort_type", "top_rated");

        if (!syncConnPref.equals("favorite")) {
            String url = Constants.API_URL + syncConnPref + Constants.API_KEY + BuildConfig.API_KEY;
            if (syncConnPref.equals("popular")) {
                setPop();
                getActivity().setTitle("Pop Movies");
            } else {
                setTop();
                getActivity().setTitle("Top Movies");
            }
            getMoviesData(url);
        } else {
            setFav();
            ArrayList<Movie> movies = db.getAllMovies();
            ArrayList<ImageItem> tempImageItems = getData(movies);
            gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, tempImageItems);
            gridAdapter.notifyDataSetChanged();
            gridView.setAdapter(gridAdapter);
            getActivity().setTitle("Fav Movies");
            Log.i("fav movies", "db loaded");
        }
        return rootView;
    }

    private void refreshMoviesData () {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        syncConnPref = sharedPref.getString("sort_type", "top_rated");

        if (!syncConnPref.equals("favorite")) {
            String url = Constants.API_URL + syncConnPref + Constants.API_KEY + BuildConfig.API_KEY;
            if (syncConnPref.equals("popular")) {
                resetScrollGridView();
                setPop();
                getMoviesData(url);
                getActivity().setTitle("Pop Movies");
            }
            if (syncConnPref.equals("top_rated")) {
                resetScrollGridView();
                setTop();
                getMoviesData(url);
                getActivity().setTitle("Top Movies");
            }
        } else {
            resetScrollGridView();
            setFav();
            ArrayList<Movie> movies = db.getAllMovies();
            ArrayList<ImageItem> tempImageItems = getData(movies);
            gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, tempImageItems);
            gridAdapter.notifyDataSetChanged();
            gridView.setAdapter(gridAdapter);
            getActivity().setTitle("Fav Movies");
            Log.i("fav movies", "db loaded");

        }

    }

    private void isScrollCompleted() {
        if (currentVisibleItem > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            syncConnPref = sharedPref.getString("sort_type", "top_rated");
            /*** In this way I detect if there's been a scroll which has completed ***/
            /*** do the work for load more date! ***/
            Log.i("test", "test: " + currentFirstVisibleItem);
        if(currentFirstVisibleItem >= (14 + counter)){
            counter = counter + 10;
            pagesCount++;
            String url = Constants.API_URL + syncConnPref + Constants.API_KEY + BuildConfig.API_KEY + "&page=" + pagesCount;
            Log.i("test", url);
            getMoviesDataUpdatedAdapter(url);
        }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(pDialog != null)
            pDialog.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!rotatedScreen) {
            Log.d("onResume", "Yrotated");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String syncConnPref = sharedPref.getString("sort_type", "top_rated");

            if (!syncConnPref.equals(this.syncConnPref)) {
                if (!syncConnPref.equals("favorite")) {
                    String url = Constants.API_URL + syncConnPref + Constants.API_KEY + BuildConfig.API_KEY;
                    if (syncConnPref.equals("popular")) {
                        resetScrollGridView();
                        setPop();
                        getMoviesData(url);
                        getActivity().setTitle("Pop Movies");
                    }
                    if (syncConnPref.equals("top_rated")) {
                        resetScrollGridView();
                        setTop();
                        getMoviesData(url);
                        getActivity().setTitle("Top Movies");
                    }
                } else {
                    resetScrollGridView();
                    setFav();
                    ArrayList<Movie> movies = db.getAllMovies();
                    ArrayList<ImageItem> tempImageItems = getData(movies);
                    gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, tempImageItems);
                    gridAdapter.notifyDataSetChanged();
                    gridView.setAdapter(gridAdapter);
                    getActivity().setTitle("Fav Movies");
                    Log.i("fav movies", "db loaded");
                }
            }
            this.syncConnPref = syncConnPref;
        } else {
            rotatedScreen = false;
            Log.d("onResume", "Xrotated");

        }
    }

    private ArrayList<ImageItem> getData(List<Movie> moviesData) {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        for (int i = 0; i < moviesData.size(); i++) {
            ImageItem imageItem = new ImageItem(Constants.POSTER_URL + moviesData.get(i).getPoster_path(), moviesData.get(i));
            imageItems.add(imageItem);
        }
        return imageItems;
    }

    private void getMoviesData(String apiURL){
        String tag_json_obj= "json_obj_req";

        pDialog.setMessage("Loading ...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                apiURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        List<Movie> responseMovies = MoviesResponse.parseMovies(response.toString());
                        ArrayList<ImageItem> tempImageItems = getData(responseMovies);
                        gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, tempImageItems);
                        gridAdapter.notifyDataSetChanged();
                        gridView.setAdapter(gridAdapter);
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+ error.getMessage());
                pDialog.hide();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void getMoviesDataUpdatedAdapter(String apiURL){
        String tag_json_obj= "json_obj_req";
        pDialog.setMessage("Loading ...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                apiURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        List<Movie> responseMovies = MoviesResponse.parseMovies(response.toString());
                        ArrayList<ImageItem> tempImageItems = getData(responseMovies);
                        if(gridAdapter != null){
                            gridAdapter.addAll(tempImageItems);
                            gridAdapter.notifyDataSetChanged();
                        }
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+ error.getMessage());
                pDialog.hide();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void setFav(){
        favFlag = true;
        topFlag = false;
        popFlag = false;
    }

    private void setPop(){
        popFlag = true;
        topFlag = false;
        favFlag = false;
    }

    private void setTop(){
        topFlag = true;
        favFlag = false;
        popFlag = false;
    }

    private void resetScrollGridView () {
        pagesCount = 1;
        counter = 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState != null){
            if (savedInstanceState.getBoolean("rotated")){
                rotatedScreen = true;
                Log.d("rotated", "true");
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_most_pop) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("sort_type", getString(R.string.popular));
            editor.commit();
            refreshMoviesData();
            return true;
        }
        if (id == R.id.action_top_rated) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("sort_type", getString(R.string.top_rated));
            editor.commit();
            refreshMoviesData();
            return true;
        }
        if (id == R.id.action_favorite) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("sort_type", getString(R.string.favourite));
            editor.commit();
            refreshMoviesData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("rotated",true);
    }
}
