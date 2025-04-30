package com.example.moviereviews.service;

import com.example.moviereviews.dto.ReviewRequestDto;
import com.example.moviereviews.dto.ReviewResponseDto;
import com.example.moviereviews.mapper.ReviewMapper;
import com.example.moviereviews.model.Movie;
import com.example.moviereviews.model.Review;
import com.example.moviereviews.repository.MovieRepository;
import com.example.moviereviews.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ReviewMapper reviewMapper;

	@InjectMocks
	private ReviewService reviewService;

	@Test
	void testCreateReviewSuccess() {
		Long movieId = 1L;
		ReviewRequestDto requestDto = ReviewRequestDto.builder()
			.rating(5).comment("Excellent").reviewerName("Bob").build();

		Movie movie = new Movie();
		Review savedReview = Review.builder().id(10L).movie(movie).build();
		ReviewResponseDto responseDto = ReviewResponseDto.builder().id(10L).build();

		when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
		when(reviewRepository.save(any())).thenReturn(savedReview);
		when(reviewMapper.toResponseDto(savedReview)).thenReturn(responseDto);

		ReviewResponseDto result = reviewService.createReview(movieId, requestDto);

		assertNotNull(result);
		assertEquals(10L, result.getId());
		verify(movieRepository).findById(movieId);
		verify(reviewRepository).save(any());
		verify(reviewMapper).toResponseDto(savedReview);
	}

	@Test
	void testCreateReviewMovieNotFound() {
		when(movieRepository.findById(99L)).thenReturn(Optional.empty());

		ReviewRequestDto requestDto = ReviewRequestDto.builder()
			.rating(5)
			.comment("Test")
			.reviewerName("Tester")
			.build();

		RuntimeException exception = assertThrows(RuntimeException.class, () ->
			reviewService.createReview(99L, requestDto)
		);

		assertEquals("Фильм с ID 99 не найден", exception.getMessage());
	}

	@Test
	void testGetAllReviews() {
		Pageable pageable = PageRequest.of(0, 10);
		Review review = new Review();
		ReviewResponseDto dto = new ReviewResponseDto();
		Page<Review> reviewPage = new PageImpl<>(java.util.List.of(review));

		when(reviewRepository.findAll(pageable)).thenReturn(reviewPage);
		when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(dto);

		Page<ReviewResponseDto> result = reviewService.getAllReviews(pageable);

		assertEquals(1, result.getTotalElements());
		verify(reviewRepository).findAll(pageable);
	}

	@Test
	void testGetReviewByIdFound() {
		Review review = new Review();
		ReviewResponseDto dto = new ReviewResponseDto();

		when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
		when(reviewMapper.toResponseDto(review)).thenReturn(dto);

		ReviewResponseDto result = reviewService.getReviewById(1L);

		assertNotNull(result);
		verify(reviewRepository).findById(1L);
	}

	@Test
	void testGetReviewByIdNotFound() {
		when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () ->
			reviewService.getReviewById(1L)
		);

		assertEquals("Отзыв с ID 1 не найден", exception.getMessage());
	}

	@Test
	void testUpdateReviewSuccess() {
		Long reviewId = 1L;
		Review existing = Review.builder().id(reviewId).build();
		Review updated = Review.builder().id(reviewId).build();
		ReviewRequestDto requestDto = ReviewRequestDto.builder()
			.rating(4).comment("Updated").reviewerName("Sam").build();
		ReviewResponseDto dto = ReviewResponseDto.builder().id(reviewId).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existing));
		when(reviewRepository.save(existing)).thenReturn(updated);
		when(reviewMapper.toResponseDto(updated)).thenReturn(dto);

		ReviewResponseDto result = reviewService.updateReview(reviewId, requestDto);

		assertEquals(reviewId, result.getId());
		verify(reviewRepository).save(existing);
	}

	@Test
	void testUpdateReviewNotFound() {
		when(reviewRepository.findById(2L)).thenReturn(Optional.empty());

		ReviewRequestDto requestDto = ReviewRequestDto.builder()
			.rating(3)
			.comment("Update")
			.reviewerName("Alex")
			.build();

		RuntimeException exception = assertThrows(RuntimeException.class, () ->
			reviewService.updateReview(2L, requestDto)
		);

		assertEquals("Отзыв с ID 2 не найден", exception.getMessage());
	}

	@Test
	void testDeleteReviewSuccess() {
		when(reviewRepository.existsById(3L)).thenReturn(true);

		reviewService.deleteReview(3L);

		verify(reviewRepository).deleteById(3L);
	}

	@Test
	void testDeleteReviewNotFound() {
		when(reviewRepository.existsById(4L)).thenReturn(false);

		RuntimeException exception = assertThrows(RuntimeException.class, () ->
			reviewService.deleteReview(4L)
		);

		assertEquals("Отзыв с ID 4 не найден", exception.getMessage());
	}
}