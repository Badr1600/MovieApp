package movieapp.com.movieapp.adapter;

import movieapp.com.movieapp.movies.details.Movie;

public class ImageItem {
        
	private String imageURL;
	private Movie movie;


	public ImageItem(String imageURL, Movie movie) {
		super();
		this.imageURL = imageURL;
		this.movie = movie;
	}

	public String getImageURL() {
		return imageURL;
	}

	public String getImageTitle(){
		return movie.getTitle();
	}

	public Movie getMovie() {
		return movie;
	}

	public void setImageURL(String image) {
		this.imageURL = image;
	}

}