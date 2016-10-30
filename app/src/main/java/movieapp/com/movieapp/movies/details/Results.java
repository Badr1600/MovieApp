package movieapp.com.movieapp.movies.details;

/**
 * Created by Ahmed on 9/9/2016.
 */
public class Results {

    int page;
    MoviesResponse [] results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public MoviesResponse[] getResults() {
        return results;
    }

    public void setResults(MoviesResponse[] results) {
        this.results = results;
    }
}
