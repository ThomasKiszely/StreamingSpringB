package com.example.streamingspringb.Model;

import java.time.LocalDate;
import java.util.Date;

public class Review {
    private int id;
    private String title;
    private String author;
    private int authorId;
    private String content;
    private int rating;
    private LocalDate date;
    private int movieId;
    private String movieTitle;

    public Review() {
    }

    public Review(int id, String title, String author, int authorId, String content, int rating, LocalDate date, int movieId, String movieTitle) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.content = content;
        this.rating = rating;
        this.date = date;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public int getAuthorId() {
        return authorId;
    }
    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public int getMovieId() {
        return movieId;
    }
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
    public String getMovieTitle() {
        return movieTitle;
    }
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
    @Override
    public String toString() {
        return (title + ", af " + author + ". "  + content + ". Rating: " + rating + ". Skrevet  " + date);
    }
}
