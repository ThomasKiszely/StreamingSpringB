package com.example.streamingspringb.Model;

import java.util.Date;
import java.util.Objects;

public class Movie {
    private int movieId;
    private String title;
    private String director;
    private Date releaseDate;
    private String genre;
    private String description;
    private int rating;
    private String src;
    private String imgsrc;

    public Movie(int movieId, String title, String director, Date releaseDate, String genre, String description, int rating, String src, String imgsrc) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.description = description;
        this.rating = rating;
        this.src = src;
        this.imgsrc = imgsrc;
    }
    public int getMovieId() {
        return movieId;
    }
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }
    public Date getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getSrc() {
        return src;
    }
    public void setSrc(String src) {
        this.src = src;
    }
    public String getImgsrc() {
        return imgsrc;
    }
    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    @Override
    public String toString() {
        return title + ", directed by " + director + ". Released " + releaseDate + ". Genre: " + genre + ".\n" + description + ". Rating: " + rating;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movie movie = (Movie) obj;
        return movieId == movie.movieId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId);
    }
}
