package movieapp.com.movieapp.adapter;

import movieapp.com.movieapp.movies.details.Movie;
import movieapp.com.movieapp.movies.details.Trailer;

/**
 * Created by Ahmed on 9/4/2016.
 */
public class TrailerItem {

    private Trailer trailer;


    public TrailerItem(Trailer trailer) {
        super();
        this.trailer = trailer;
    }

    public Trailer getTrailer() {
        return trailer;
    }

}
