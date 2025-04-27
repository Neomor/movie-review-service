package com.example.moviereviews.service;

import com.example.moviereviews.dto.ReviewCreateDto;
import com.example.moviereviews.dto.ReviewResponseDto;
import com.example.moviereviews.model.Movie;
import com.example.moviereviews.model.Review;
import com.example.moviereviews.repository.MovieRepository;
import com.example.moviereviews.repository.ReviewRepository;
import com.example.moviereviews.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final MovieRepository movieRepository;
	private final ReviewMapper reviewMapper;

	@CacheEvict(value = {"movies", "topRatedMovies", "reviews"}, allEntries = true)
	public ReviewResponseDto createReview(ReviewCreateDto reviewCreateDto) {
		Movie movie = movieRepository.findById(reviewCreateDto.getMovieId())
			.orElseThrow(() -> new RuntimeException("Фильм с ID " + reviewCreateDto.getMovieId() + " не найден"));

		Review review = Review.builder()
			.rating(reviewCreateDto.getRating())
			.comment(reviewCreateDto.getComment())
			.reviewerName(reviewCreateDto.getReviewerName())
			.movie(movie)
			.build();

		Review savedReview = reviewRepository.save(review);
		return mapToReviewResponseDto(savedReview);
	}

	@Cacheable(value = "reviews")
	public Page<ReviewResponseDto> getAllReviews(Pageable pageable) {
		return reviewRepository.findAll(pageable)
			.map(reviewMapper::toResponseDto);
	}

	@Cacheable(value = "reviews", key = "#id")
	public ReviewResponseDto getReviewById(Long id) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Отзыв с ID " + id + " не найден"));
		return mapToReviewResponseDto(review);
	}

	@CacheEvict(value = {"movies", "topRatedMovies", "reviews"}, allEntries = true)
	public ReviewResponseDto updateReview(Long id, ReviewCreateDto reviewCreateDto) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Отзыв с ID " + id + " не найден"));

		Movie movie = movieRepository.findById(reviewCreateDto.getMovieId())
			.orElseThrow(() -> new RuntimeException("Фильм с ID " + reviewCreateDto.getMovieId() + " не найден"));

		review.setRating(reviewCreateDto.getRating());
		review.setComment(reviewCreateDto.getComment());
		review.setReviewerName(reviewCreateDto.getReviewerName());
		review.setMovie(movie);

		Review updatedReview = reviewRepository.save(review);
		return mapToReviewResponseDto(updatedReview);
	}

	@CacheEvict(value = {"movies", "topRatedMovies", "reviews"}, allEntries = true)
	public void deleteReview(Long id) {
		if (!reviewRepository.existsById(id)) {
			throw new RuntimeException("Отзыв с ID " + id + " не найден");
		}
		reviewRepository.deleteById(id);
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
}