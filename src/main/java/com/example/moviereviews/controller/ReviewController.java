package com.example.moviereviews.controller;

import com.example.moviereviews.dto.ReviewCreateDto;
import com.example.moviereviews.dto.ReviewResponseDto;
import com.example.moviereviews.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Управление отзывами", description = "Создание, получение, обновление и удаление отзывов")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	@Operation(summary = "Создать отзыв для фильма", description = "Создать отдельный отзыв и привязать его к фильму")
	public ReviewResponseDto createReview(@RequestBody @Valid ReviewCreateDto reviewCreateDto) {
		log.info("Creating a new review for movieId={}", reviewCreateDto.getMovieId());
		return reviewService.createReview(reviewCreateDto);
	}

	@GetMapping
	@Operation(summary = "Получить все отзывы", description = "Получить список всех отзывов с пагинацией")
	public Page<ReviewResponseDto> getAllReviews(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		log.info("Getting reviews: page={}, size={}", page, size);
		return reviewService.getAllReviews(PageRequest.of(page, size));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Получить отзыв по ID", description = "Получить отзыв по его идентификатору")
	public ReviewResponseDto getReviewById(@PathVariable Long id) {
		log.info("Getting review with id={}", id);
		return reviewService.getReviewById(id);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Обновить отзыв", description = "Обновить отзыв по его ID")
	public ReviewResponseDto updateReview(@PathVariable Long id, @RequestBody @Valid ReviewCreateDto reviewCreateDto) {
		log.info("Updating review with id={}", id);
		return reviewService.updateReview(id, reviewCreateDto);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Удалить отзыв", description = "Удалить отзыв по его ID")
	public void deleteReview(@PathVariable Long id) {
		log.info("Deleting review with id={}", id);
		reviewService.deleteReview(id);
	}
}