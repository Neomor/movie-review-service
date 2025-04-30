package com.example.moviereviews.service;

import com.example.moviereviews.dto.ReviewRequestDto;
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
	public ReviewResponseDto createReview(Long id, ReviewRequestDto reviewRequestDto) {
		Movie movie = movieRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Фильм с ID " + id + " не найден"));

		Review review = Review.builder()
			.rating(reviewRequestDto.getRating())
			.comment(reviewRequestDto.getComment())
			.reviewerName(reviewRequestDto.getReviewerName())
			.movie(movie)
			.build();

		Review savedReview = reviewRepository.save(review);
		return reviewMapper.toResponseDto(savedReview);
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
		return reviewMapper.toResponseDto(review);
	}

	@CacheEvict(value = {"movies", "topRatedMovies", "reviews"}, allEntries = true)
	public ReviewResponseDto updateReview(Long id, ReviewRequestDto reviewRequestDto) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Отзыв с ID " + id + " не найден"));

		review.setRating(reviewRequestDto.getRating());
		review.setComment(reviewRequestDto.getComment());
		review.setReviewerName(reviewRequestDto.getReviewerName());

		Review updatedReview = reviewRepository.save(review);
		return reviewMapper.toResponseDto(updatedReview);
	}

	@CacheEvict(value = {"movies", "topRatedMovies", "reviews"}, allEntries = true)
	public void deleteReview(Long id) {
		if (!reviewRepository.existsById(id)) {
			throw new RuntimeException("Отзыв с ID " + id + " не найден");
		}
		reviewRepository.deleteById(id);
	}

}