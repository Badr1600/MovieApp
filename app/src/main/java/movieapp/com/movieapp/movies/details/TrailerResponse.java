package movieapp.com.movieapp.movies.details;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed on 9/4/2016.
 */
public class TrailerResponse {

    @SerializedName("results")
    List<Trailer> trailers;

    public TrailerResponse(){
        trailers = new ArrayList<Trailer>();
    }

    public List<Trailer> getTrailers_loaded() {
        return trailers;
    }

    public static List<Trailer> parseTrailers(String response) {

        Gson gson = new GsonBuilder().create();
        TrailerResponse trailerResponse = gson.fromJson(response, TrailerResponse.class);

        return trailerResponse.getTrailers_loaded();
    }
}
