package com.example.streamingspringb.Infrastructure;

import com.example.streamingspringb.Model.Movie;
import com.example.streamingspringb.Model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MovieRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Movie> getMovies(){
        String sql = "select * from movie";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> new Movie(rs.getInt("movieid"), rs.getString("title"), rs.getString("director"), rs.getDate("releasedate"), rs.getString("genre"), rs.getString("description"), rs.getInt("rating"), rs.getString("src"), rs.getString("imgsrc"))));
    }
    public Optional<Movie> getMovie(int id) {
        try {
            String sql = "select * from movie where movieid = ?";
            Movie movie = jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new Movie(rs.getInt("movieid"), rs.getString("title"), rs.getString("director"), rs.getDate("releasedate"), rs.getString("genre"), rs.getString("description"), rs.getInt("rating"), rs.getString("src"), rs.getString("imgsrc"))), id);
            return Optional.of(movie);
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    public Boolean createReview(Review review) {
        String sql = "INSERT INTO `streamingtjeneste`.`review` (`title`, `author`, `authorid`, `content`, `rating`, `date`, `fkmovie`) VALUES (?, ?, ?, ?, ?, ?, ?);";
        int ok = jdbcTemplate.update(sql, review.getTitle(), review.getAuthor(), review.getAuthorId(), review.getContent(), review.getRating(), review.getDate(), review.getMovieId());
        if (ok > 0) {
            return true;
        }
        return false;
    }
    public List<Review> getReviews(int id) {
        String sql = "select * from review join movie on movieid = fkmovie where fkmovie = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Review(rs.getInt("idreview") , rs.getString("title"), rs.getString("author"), rs.getInt("authorid"), rs.getString("content"), rs.getInt("rating"), rs.getDate("date").toLocalDate(), rs.getInt("fkmovie"), rs.getString("movie.title")), id);
    }
    public Boolean updateReview(int id, Review review) {
        String sql ="UPDATE review\n" +
                "SET title = ?,\n" +
                "    content = ?,\n" +
                "    rating = ?,\n" +
                "    date = ?\n" +
                "WHERE idreview = ?;";
        int updated = jdbcTemplate.update(sql, review.getTitle(), review.getContent(), review.getRating(), review.getDate(), id);
        if (updated > 0) {
            return true;
        }
        return false;
    }
    public List<Movie> getFavorites(int id) {
        String sql = "select * from movie\n" +
                "join favorites on fkmovie = movieid\n" +
                "join user on iduser = fkuser\n" +
                "where iduser = ?;";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> new Movie(rs.getInt("movieid"), rs.getString("title"), rs.getString("director"), rs.getDate("releasedate"), rs.getString("genre"), rs.getString("description"), rs.getInt("rating"), rs.getString("src"), rs.getString("imgsrc"))), id);
    }
    public Boolean addFavorite(int movieid, int userid) {
        String sql = "INSERT INTO favorites (fkuser, fkmovie) VALUES (?, ?);";
        int ok = jdbcTemplate.update(sql, userid, movieid);
        if (ok > 0) {
            return true;
        }
        return false;
    }
    public boolean removeFavorite(int movieid, int userid) {
        String sql = "DELETE FROM favorites WHERE fkmovie = ? AND fkuser = ?";
        int ok = jdbcTemplate.update(sql, movieid, userid);
        if (ok > 0) {
            return true;
        }
        return false;
    }
    public List<Review> getAllReviews() {
        String sql = "select * from `streamingtjeneste`.`review` join movie on movieid = fkmovie;";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> new Review(rs.getInt("idreview") , rs.getString("title"), rs.getString("author"), rs.getInt("authorid"), rs.getString("content"), rs.getInt("rating"), rs.getDate("date").toLocalDate(), rs.getInt("fkmovie"), rs.getString("movie.title"))));
    }
    public boolean deleteReview(int id) {
        String sql = "DELETE FROM review WHERE idreview = ?";
        int ok = jdbcTemplate.update(sql, id);
        if (ok > 0) {
            return true;
        }
        return false;
    }
    public List<Review> getReviewsPaged(int page, int pageSize) {

        String sql = "select * from review join movie on movieid = fkmovie\n" +
                    "limit ? offset ?;";
        int offset = (page - 1) * pageSize;
        return jdbcTemplate.query(sql, new Object[]{pageSize, offset}, ((rs, rowNum) -> new Review(rs.getInt("idreview") , rs.getString("title"), rs.getString("author"), rs.getInt("authorid"), rs.getString("content"), rs.getInt("rating"), rs.getDate("date").toLocalDate(), rs.getInt("fkmovie"), rs.getString("movie.title"))));
    }
}
