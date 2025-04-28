package com.example.moviereviews.service;

import com.example.moviereviews.dto.ReviewCreateDto;
import com.example.moviereviews.dto.ReviewResponseDto;
import com.example.moviereviews.mapper.ReviewMapper;
import com.example.moviereviews.model.Movie;
import com.example.moviereviews.model.Review;
import com.example.moviereviews.repository.MovieRepository;
import com.example.moviereviews.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

	private ReviewRepository reviewRepository;
	private MovieRepository movieRepository;
	private ReviewMapper reviewMapper;
	private ReviewService reviewService;

	@BeforeEach
	void setUp() {
		reviewRepository = mock(ReviewRepository.class);
		movieRepository = mock(MovieRepository.class);
		reviewMapper = mock(ReviewMapper.class);
		reviewService = new ReviewService(reviewRepository, movieRepository, reviewMapper);
	}

	@Test
	void createReview_shouldSaveReviewCorrectly() {
		ReviewCreateDto reviewCreateDto = ReviewCreateDto.builder()
			.rating(5)
			.comment("Awesome!")
			.reviewerName("John Doe")
			.movieId(1L)
			.build();
		Movie movie = new Movie();
		movie.setId(1L);

		Review review = new Review();
		review.setRating(5);
		review.setComment("Awesome!");
		review.setReviewerName("John Doe");
		review.setMovie(movie);

		when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
		when(reviewRepository.save(any(Review.class))).thenReturn(review);

		ReviewResponseDto response = reviewService.createReview(reviewCreateDto);
		assertThat(response.getRating()).isEqualTo(5);
		assertThat(response.getComment()).isEqualTo("Awesome!");
		verify(reviewRepository, times(1)).save(any(Review.class));
	}

	@Test
	void getAllReviews_shouldReturnPageOfReviews() {
		Review review = new Review();
		when(reviewRepository.findAll(any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of(review)));
		when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(new ReviewResponseDto());

		Page<ReviewResponseDto> result = reviewService.getAllReviews(Pageable.unpaged());
		assertThat(result.getContent()).hasSize(1);
		verify(reviewRepository, times(1)).findAll(any(Pageable.class));
	}

	@Test
	void getReviewById_shouldReturnReviewIfExists() {
		Review review = new Review();
		review.setId(1L);
		review.setRating(4);

		when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

		ReviewResponseDto result = reviewService.getReviewById(1L);
		assertThat(result.getRating()).isEqualTo(4);
		verify(reviewRepository, times(1)).findById(1L);
	}

	@Test
	void getReviewById_shouldThrowExceptionIfNotFound() {
		when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> reviewService.getReviewById(1L));
		verify(reviewRepository, times(1)).findById(1L);
	}

	@Test
	void updateReview_shouldUpdateAndSaveReview() {
		Review review = new Review();
		review.setId(1L);
		Movie movie = new Movie();
		movie.setId(1L);

		ReviewCreateDto reviewCreateDto = ReviewCreateDto.builder()
			.rating(5)
			.comment("Updated Comment")
			.reviewerName("Jane Doe")
			.movieId(1L)
			.build();

		when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
		when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
		when(reviewRepository.save(any(Review.class))).thenReturn(review);

		ReviewResponseDto response = reviewService.updateReview(1L, reviewCreateDto);
		assertThat(response.getComment()).isEqualTo("Updated Comment");
		verify(reviewRepository, times(1)).save(review);
	}

	@Test
	void deleteReview_shouldDeleteWhenExists() {
		when(reviewRepository.existsById(1L)).thenReturn(true);
		reviewService.deleteReview(1L);
		verify(reviewRepository, times(1)).deleteById(1L);
	}

	@Test
	void deleteReview_shouldThrowExceptionWhenNotFound() {
		when(reviewRepository.existsById(anyLong())).thenReturn(false);
		
		assertThrows(RuntimeException.class, () -> reviewService.deleteReview(1L));
		verify(reviewRepository, never()).deleteById(anyLong());
	}
}