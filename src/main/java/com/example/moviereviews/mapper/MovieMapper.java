package com.example.moviereviews.mapper;

import com.example.moviereviews.dto.MovieRequestDto;
import com.example.moviereviews.dto.MovieResponseDto;
import com.example.moviereviews.dto.ReviewResponseDto;
import com.example.moviereviews.model.Movie;
import com.example.moviereviews.model.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

	public Movie toEntity(MovieRequestDto movieRequestDto) {
		Movie movie = new Movie();
		movie.setTitle(movieRequestDto.getTitle());
		movie.setReleaseYear(movieRequestDto.getReleaseYear());
		movie.setGenre(movieRequestDto.getGenre());
		movie.setDirector(movieRequestDto.getDirector());
		return movie;
	}

	public MovieResponseDto toResponseDto(Movie movie) {
		MovieResponseDto dto = new MovieResponseDto();
		dto.setId(movie.getId());
		dto.setTitle(movie.getTitle());
		dto.setReleaseYear(movie.getReleaseYear());
		dto.setGenre(movie.getGenre());
		dto.setDirector(movie.getDirector());

		if (movie.getReviews() != null) {
			List<ReviewResponseDto> reviews = movie.getReviews().stream()
				.map(this::toReviewResponseDto)
				.collect(Collectors.toList());
			dto.setReviews(reviews);
		}

		return dto;
	}

	private ReviewResponseDto toReviewResponseDto(Review review) {
		ReviewResponseDto dto = new ReviewResponseDto();
		dto.setId(review.getId());
		dto.setRating(review.getRating());
		dto.setComment(review.getComment());
		dto.setReviewerName(review.getReviewerName());
		dto.setMovieId(review.getMovie().getId());
		return dto;
	}
}