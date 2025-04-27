package com.example.moviereviews.service;

import com.example.moviereviews.dto.MovieRequestDto;
import com.example.moviereviews.dto.MovieResponseDto;
import com.example.moviereviews.dto.ReviewRequestDto;
import com.example.moviereviews.dto.ReviewResponseDto;
import com.example.moviereviews.model.Movie;
import com.example.moviereviews.model.Review;
import com.example.moviereviews.repository.MovieRepository;
import com.example.moviereviews.mapper.MovieMapper;
import com.example.moviereviews.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MovieService {

	private final MovieRepository movieRepository;
	private final MovieMapper movieMapper;
	private final ReviewMapper reviewMapper;

	@CacheEvict(value = {"movies", "topRatedMovies", "reviews"}, allEntries = true)
	public MovieResponseDto createMovieWithReviews(MovieRequestDto requestDto) {
		Movie movie = movieMapper.toEntity(requestDto);

		Movie savedMovie = movieRepository.save(movie);
		
		if (requestDto.getReviews() != null && !requestDto.getReviews().isEmpty()) {
			List<Review> reviews = new ArrayList<>();
			for (ReviewRequestDto reviewDto : requestDto.getReviews()) {
				Review review = reviewMapper.toEntity(reviewDto);
				review.setMovie(savedMovie);
				reviews.add(review);
			}
			savedMovie.setReviews(reviews);
			savedMovie = movieRepository.save(savedMovie);
		}

		return movieMapper.toResponseDto(savedMovie);
	}

	@Cacheable(value = "movies")
	public Page<MovieResponseDto> getAllMovies(Pageable pageable) {
		return movieRepository.findAll(pageable)
			.map(movieMapper::toResponseDto);
	}

	public MovieResponseDto getMovieById(Long id) {
		Movie movie = movieRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Фильм с ID " + id + " не найден"));
		return mapToMovieResponseDto(movie);
	}

	@CacheEvict(value = {"movies", "topRatedMovies", "reviews"}, allEntries = true)
	@Transactional
	public MovieResponseDto updateMovie(Long id, MovieRequestDto movieRequestDto) {
		Movie movie = movieRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Фильм с ID " + id + " не найден"));

		movie.setTitle(movieRequestDto.getTitle());
		movie.setReleaseYear(movieRequestDto.getReleaseYear());
		movie.setGenre(movieRequestDto.getGenre());
		movie.setDirector(movieRequestDto.getDirector());
		
		if (movie.getReviews() != null) {
			movie.getReviews().clear();
		} else {
			movie.setReviews(new ArrayList<>());
		}

		if (movieRequestDto.getReviews() != null) {
			for (var reviewDto : movieRequestDto.getReviews()) {
				Review review = Review.builder()
					.rating(reviewDto.getRating())
					.comment(reviewDto.getComment())
					.reviewerName(reviewDto.getReviewerName())
					.movie(movie)
					.build();
				movie.getReviews().add(review);
			}
		}

		Movie updatedMovie = movieRepository.save(movie);
		return mapToMovieResponseDto(updatedMovie);
	}

	@CacheEvict(value = {"movies", "topRatedMovies", "reviews"}, allEntries = true)
	public void deleteMovie(Long id) {
		if (!movieRepository.existsById(id)) {
			throw new RuntimeException("Фильм с ID " + id + " не найден");
		}
		movieRepository.deleteById(id);
	}

	private MovieResponseDto mapToMovieResponseDto(Movie movie) {
		return MovieResponseDto.builder()
			.id(movie.getId())
			.title(movie.getTitle())
			.releaseYear(movie.getReleaseYear())
			.genre(movie.getGenre())
			.director(movie.getDirector())
			.reviews(movie.getReviews() != null
				? movie.getReviews().stream()
				.map(this::mapToReviewResponseDto)
				.collect(Collectors.toList())
				: null)
			.build();
	}

	private ReviewResponseDto mapToReviewResponseDto(Review review) {
		return ReviewResponseDto.builder()
			.id(review.getId())
			.rating(review.getRating())
			.comment(review.getComment())
			.reviewerName(review.getReviewerName())
			.movieId(review.getMovie() != null ? review.getMovie().getId() : null)
			.build();
	}

	@Cacheable(value = "movies", key = "#genre")
	public List<MovieResponseDto> findMoviesByGenre(String genre) {
		List<Movie> movies = movieRepository.findByGenre(genre);
		return movies.stream()
			.map(movieMapper::toResponseDto)
			.toList();
	}

	@Cacheable(value = "topRatedMovies", key = "#genre")
	public List<MovieResponseDto> findTopRatedMoviesByGenre(String genre) {
		List<Movie> movies = movieRepository.findTopMoviesByGenreOrderByAverageRatingDesc(genre);
		return movies.stream()
			.map(movieMapper::toResponseDto)
			.toList();
	}
	
}