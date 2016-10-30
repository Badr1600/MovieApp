package movieapp.com.movieapp.adapter;

import movieapp.com.movieapp.movies.details.Review;
import movieapp.com.movieapp.movies.details.Trailer;

/**
 * Created by Ahmed on 9/5/2016.
 */
public class ReviewItem {

    private Review review;


    public ReviewItem(Review review) {
        super();
        this.review = review;
    }

    public Review getReview() {
        return review;
    }

}
