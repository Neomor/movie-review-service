package com.example.moviereviews.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ReviewCreateDto {

	@NotNull(message = "Рейтинг обязателен")
	@Min(value = 1, message = "Рейтинг должен быть минимум 1")
	@Max(value = 5, message = "Рейтинг должен быть максимум 5")
	@Schema(example = "5")
	private Integer rating;

	@NotBlank(message = "Комментарий обязателен")
	@Schema(example = "Amazing movie!")
	private String comment;

	@NotBlank(message = "Имя рецензента обязательно")
	@Schema(example = "Alice")
	private String reviewerName;

	@NotNull(message = "ID фильма обязателен")
	@Schema(example = "1")
	private Long movieId;
}