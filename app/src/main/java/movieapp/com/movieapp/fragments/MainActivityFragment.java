package movieapp.com.movieapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import movieapp.com.movieapp.activities.ScrollingActivityDetails;
import movieapp.com.movieapp.activities.SettingsActivity;
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
    private DatabaseHandler db ;
    private int currentVisibleItem;
    private int currentFirstVisibleItem;
    private int currentScrollState;
    private int pagesCount;
    private int counter;
    private String syncConnPref;
    private boolean popFlag;
    private boolean favFlag;
    private boolean topFlag;

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
        syncConnPref = sharedPref.getString("sort_type", "");

        if(!syncConnPref.equals("favorite")){
            String url = Constants.API_URL + syncConnPref + Constants.API_KEY + BuildConfig.API_KEY;
            if(syncConnPref.equals("popular")){
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
        }
        return rootView;
    }

    private void refreshMoviesData (){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        syncConnPref = sharedPref.getString("sort_type", "");

        if(!syncConnPref.equals("favorite") && !isFav()){
            String url = Constants.API_URL + syncConnPref + Constants.API_KEY + BuildConfig.API_KEY;
            if(syncConnPref.equals("popular") && !isPop()){
                resetScrollGridView();
                setPop();
                getMoviesData(url);
                getActivity().setTitle("Pop Movies");
            } if(syncConnPref.equals("top_rated") && !isTop()) {
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
        }
    }

    private void isScrollCompleted() {
        if (currentVisibleItem > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String syncConnPref = sharedPref.getString("sort_type", "");

        if(!syncConnPref.equals("favorite") && !isFav()){
            String url = Constants.API_URL + syncConnPref + Constants.API_KEY + BuildConfig.API_KEY;
            if(syncConnPref.equals("popular") && !isPop()){
                resetScrollGridView();
                setPop();
                getMoviesData(url);
                getActivity().setTitle("Pop Movies");
            } if(syncConnPref.equals("top_rated") && !isTop()) {
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

    private boolean isPop(){
        return popFlag;
    }

    private boolean isTop(){
        return topFlag;
    }

    private boolean isFav(){
        return favFlag;
    }

    private void resetScrollGridView () {
        pagesCount = 1;
        counter = 0;
    }
}
