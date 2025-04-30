package com.example.moviereviews.mapper;

import com.example.moviereviews.dto.MovieRequestDto;
import com.example.moviereviews.dto.MovieResponseDto;
import com.example.moviereviews.dto.ReviewResponseDto;
import com.example.moviereviews.model.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {

	private final ReviewMapper reviewMapper;

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
				.map(reviewMapper::toResponseDto)
				.collect(Collectors.toList());
			dto.setReviews(reviews);
		}

		return dto;
	}
}