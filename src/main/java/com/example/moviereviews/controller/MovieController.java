package com.example.moviereviews.controller;

import com.example.moviereviews.dto.MovieRequestDto;
import com.example.moviereviews.dto.MovieResponseDto;
import com.example.moviereviews.service.MovieService;
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
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Управление фильмами", description = "Создание, получение, обновление и удаление фильмов")
public class MovieController {

	private final MovieService movieService;

	@PostMapping
	@Operation(summary = "Создать фильм с отзывами", description = "Создать новый фильм вместе с отзывами")
	public MovieResponseDto createMovie(@RequestBody @Valid MovieRequestDto movieRequestDto) {
		log.info("Creating a new movie: {}", movieRequestDto.getTitle());
		return movieService.createMovieWithReviews(movieRequestDto);
	}

	@GetMapping
	@Operation(summary = "Получить все фильмы", description = "Получить список всех фильмов с пагинацией")
	public Page<MovieResponseDto> getAllMovies(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		log.info("Getting movies: page={}, size={}", page, size);
		return movieService.getAllMovies(PageRequest.of(page, size));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Получить фильм по ID", description = "Получить фильм по его идентификатору")
	public MovieResponseDto getMovieById(@PathVariable Long id) {
		log.info("Getting movie with id={}", id);
		return movieService.getMovieById(id);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Обновить фильм", description = "Обновить информацию о фильме")
	public MovieResponseDto updateMovie(@PathVariable Long id, @RequestBody @Valid MovieRequestDto movieRequestDto) {
		log.info("Updating movie with id={}", id);
		return movieService.updateMovie(id, movieRequestDto);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Удалить фильм", description = "Удалить фильм по его идентификатору")
	public void deleteMovie(@PathVariable Long id) {
		log.info("Deleting movie with id={}", id);
		movieService.deleteMovie(id);
	}

	@GetMapping("/by-genre")
	@Operation(summary = "Найти фильмы по жанру", description = "Вернуть все фильмы указанного жанра без сортировки по рейтингу")
	public List<MovieResponseDto> getMoviesByGenre(@RequestParam String genre) {
		log.info("Getting movies by genre: {}", genre);
		return movieService.findMoviesByGenre(genre);
	}

	@GetMapping("/top-by-genre")
	@Operation(summary = "Поиск фильмов с наивысшим рейтингом по жанру", description = "Вернуть фильмы в указанном жанре, отсортированные по среднему рейтингу")
	public List<MovieResponseDto> getTopRatedMoviesByGenre(@RequestParam String genre) {
		log.info("Getting top rated movies by genre: {}", genre);
		return movieService.findTopRatedMoviesByGenre(genre);
	}

}