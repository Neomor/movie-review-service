package com.example.moviereviews.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ReviewRequestDto {

	@NotNull(message = "Рейтинг обязателен")
	@Min(value = 1, message = "Рейтинг должен быть не меньше 1")
	@Max(value = 5, message = "Рейтинг должен быть не больше 5")
	@Schema(example = "5")
	private Integer rating;

	@NotBlank(message = "Комментарий обязателен")
	@Schema(example = "Отличный фильм! Обязательно к просмотру.")
	private String comment;

	@NotBlank(message = "Имя рецензента обязательно")
	@Schema(example = "Alice")
	private String reviewerName;
}