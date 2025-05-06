package com.example.streamingspringb.Service;

import com.example.streamingspringb.Infrastructure.MovieRepository;
import com.example.streamingspringb.Model.Movie;
import com.example.streamingspringb.Model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getMovies(){
        return movieRepository.getMovies();
    }
    public Movie getMovie(int id) {
        Optional<Movie> movie = movieRepository.getMovie(id);
        if (movie.isPresent()) {
            return (Movie) movie.get();
        }
        return null;
    }
    public Boolean createReview(Review review) {
        if (review != null) {
            int id = review.getMovieId();
            List<Review> ListedReviews = getReviews(id);
            for (Review listedReview : ListedReviews) {
                if (listedReview.getAuthorId() == review.getAuthorId()) {
                    return movieRepository.updateReview(listedReview.getId(), review);
                }
            }
            System.out.println(review.getMovieTitle());
            return movieRepository.createReview(review);
        }
        return false;
    }
    public List<Review> getReviews(int id) {
        return movieRepository.getReviews(id);
    }
    public List<Movie> favorites(int id){
        return movieRepository.getFavorites(id);
    }
    public boolean addFavorite(int movieid, int userid) {
        return movieRepository.addFavorite(movieid, userid);
    }
    public boolean removeFavorite(int movieid, int userid) {
        return movieRepository.removeFavorite(movieid, userid);
    }
    public List<Movie> searchMovies(String query, String genre) {
        List<Movie> movies = movieRepository.getMovies();
        List<Movie> searchedMovies = new ArrayList<>();
        for (Movie movie : movies) {
            if ((movie.getTitle().toLowerCase().contains(query.toLowerCase()) && !query.isEmpty()) || movie.getGenre().toLowerCase().equals(genre.toLowerCase())) {
                searchedMovies.add(movie);
            }
        }
        return searchedMovies;
    }
//    public List<Review> getAllReviews(){
//        return movieRepository.getAllReviews();
//    }
    public boolean deleteReview(int id) {
        return movieRepository.deleteReview(id);
    }
    public List<Review> getReviewsPaged(int page, int pageSize){
        return movieRepository.getReviewsPaged(page, pageSize);
    }
    public List<Review> searchReviews(String query){
        List<Review> allReviews = movieRepository.getAllReviews();
        List<Review> filteredReviews = new ArrayList<>();
        for (Review review : allReviews) {
            if (review.getTitle().toLowerCase().contains(query.toLowerCase()) || review.getAuthor().toLowerCase().contains(query.toLowerCase()) || review.getMovieTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredReviews.add(review);
            }
        }
        return filteredReviews;
    }
}
