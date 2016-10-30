package movieapp.com.movieapp.movies.details;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Ahmed on 8/12/2016.
 */
public class Movie implements Parcelable {

    String poster_path;
    boolean adult;
    String overview;
    String release_date;
    int [] genre_ids;
    int id;
    String original_title;
    String original_language;
    String title;
    String backdrop_path;
    double popularity;
    int vote_count;
    boolean video;
    double vote_average;


    public Movie (){}

    public Movie (
            int id,
            String title,
            String poster_path,
            int vote_count,
            String release_date,
            double popularity,
            double vote_average,
            String overview) {

        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.id = id;
        this.title = title;
        this.popularity = popularity;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
    }

    protected Movie(Parcel in) {
        poster_path = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        release_date = in.readString();
        genre_ids = in.createIntArray();
        id = in.readInt();
        original_title = in.readString();
        original_language = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readDouble();
        vote_count = in.readInt();
        video = in.readByte() != 0;
        vote_average = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setGenre_ids(int[] genre_ids) {
        this.genre_ids = genre_ids;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public int[] getGenre_ids() {
        return genre_ids;
    }

    public int getId() {
        return id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getVote_count() {
        return vote_count;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVote_average() {
        return vote_average;
    }


    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeIntArray(genre_ids);
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(original_language);
        dest.writeString(title);
        dest.writeString(backdrop_path);
        dest.writeDouble(popularity);
        dest.writeInt(vote_count);
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeDouble(vote_average);
    }
}
