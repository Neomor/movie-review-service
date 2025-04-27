package com.example.moviereviews.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class MovieRequestDto {

	@NotBlank(message = "Название фильма не может быть пустым")
	@Schema(example = "Inception")
	private String title;

	@NotNull(message = "Год выпуска обязателен")
	@Min(value = 1888, message = "Год выпуска должен быть не раньше 1888 года")
	@Schema(example = "2010")
	private Integer releaseYear;

	@NotBlank(message = "Жанр обязателен")
	@Schema(example = "Sci-Fi")
	private String genre;

	@NotBlank(message = "Имя режиссера обязательно")
	@Schema(example = "Christopher Nolan")
	private String director;

	@Valid
	private List<ReviewRequestDto> reviews;
}