package movieapp.com.movieapp.movies.details;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import movieapp.com.movieapp.utils.AppController;

/**
 * Created by Ahmed on 8/12/2016.
 */
public class MoviesResponse {

    @SerializedName("results")
    List<Movie> movies_loaded;

    public MoviesResponse(){
        movies_loaded = new ArrayList<Movie>();
    }

    public List<Movie> getMovies_loaded() {
        return movies_loaded;
    }

    public static List<Movie> parseMovies(String response) {

        Gson gson = new GsonBuilder().create();
        MoviesResponse moviesResponse = gson.fromJson(response, MoviesResponse.class);

        return moviesResponse.getMovies_loaded();
    }
}
