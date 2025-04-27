package com.example.moviereviews.repository;

import com.example.moviereviews.model.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
	List<Movie> findByGenre(String genre);
	@Query("""
            SELECT m
            FROM Movie m
            JOIN m.reviews r
            WHERE m.genre = :genre
            GROUP BY m
            ORDER BY AVG(r.rating) DESC
            """)
	List<Movie> findTopMoviesByGenreOrderByAverageRatingDesc(@Param("genre") String genre);
}