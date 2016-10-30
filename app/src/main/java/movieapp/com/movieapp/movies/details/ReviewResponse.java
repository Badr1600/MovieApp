package movieapp.com.movieapp.movies.details;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed on 9/5/2016.
 */
public class ReviewResponse {

    @SerializedName("results")
    List<Review> reviews_loaded;

    public ReviewResponse(){
        reviews_loaded = new ArrayList<Review>();
    }

    public List<Review> getReviews_loaded() {
        return reviews_loaded;
    }

    public static List<Review> parseReviews(String response) {

        Gson gson = new GsonBuilder().create();
        ReviewResponse reviewsResponse = gson.fromJson(response, ReviewResponse.class);

        return reviewsResponse.getReviews_loaded();
    }
}
