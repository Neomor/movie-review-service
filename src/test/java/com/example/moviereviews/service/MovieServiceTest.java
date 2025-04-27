package com.example.moviereviews.service;

import com.example.moviereviews.dto.*;
import com.example.moviereviews.mapper.MovieMapper;
import com.example.moviereviews.mapper.ReviewMapper;
import com.example.moviereviews.model.Movie;
import com.example.moviereviews.model.Review;
import com.example.moviereviews.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovieServiceTest {

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private MovieMapper movieMapper;

	@Mock
	private ReviewMapper reviewMapper;

	@InjectMocks
	private MovieService movieService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createMovieWithReviews_ShouldReturnCreatedMovie() {
		MovieRequestDto requestDto = MovieRequestDto.builder()
			.title("Inception")
			.releaseYear(2010)
			.genre("Sci-Fi")
			.director("Christopher Nolan")
			.reviews(List.of(ReviewRequestDto.builder()
				.rating(5)
				.comment("Excellent!")
				.reviewerName("Alice")
				.build()))
			.build();

		Movie movieEntity = new Movie();
		Movie savedMovie = new Movie();
		savedMovie.setId(1L);

		MovieResponseDto responseDto = MovieResponseDto.builder()
			.id(1L)
			.title("Inception")
			.build();

		when(movieMapper.toEntity(requestDto)).thenReturn(movieEntity);
		when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);
		when(reviewMapper.toEntity(any(ReviewRequestDto.class))).thenReturn(new Review());
		when(movieMapper.toResponseDto(savedMovie)).thenReturn(responseDto);

		MovieResponseDto result = movieService.createMovieWithReviews(requestDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);

		verify(movieRepository, times(2)).save(any(Movie.class));
		verify(movieMapper).toResponseDto(savedMovie);
	}

	@Test
	void getMovieById_ShouldReturnMovie() {
		Movie movie = new Movie();
		movie.setId(1L);

		when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

		MovieResponseDto expectedResponse = MovieResponseDto.builder()
			.id(1L)
			.title("Inception")
			.build();

		when(movieMapper.toResponseDto(movie)).thenReturn(expectedResponse);

		MovieResponseDto result = movieService.getMovieById(1L);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
	}

	@Test
	void getMovieById_ShouldThrowException_WhenMovieNotFound() {
		when(movieRepository.findById(1L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> movieService.getMovieById(1L));

		assertThat(exception.getMessage()).isEqualTo("Фильм с ID 1 не найден");
	}

	@Test
	void updateMovie_ShouldUpdateAndReturnMovie() {
		Movie movie = new Movie();
		movie.setId(1L);

		MovieRequestDto updateDto = MovieRequestDto.builder()
			.title("Interstellar")
			.releaseYear(2014)
			.genre("Sci-Fi")
			.director("Christopher Nolan")
			.reviews(Collections.emptyList())
			.build();

		Movie updatedMovie = new Movie();
		updatedMovie.setId(1L);
		updatedMovie.setTitle("Interstellar");

		MovieResponseDto responseDto = MovieResponseDto.builder()
			.id(1L)
			.title("Interstellar")
			.build();

		when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
		when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);
		when(movieMapper.toResponseDto(updatedMovie)).thenReturn(responseDto);

		MovieResponseDto result = movieService.updateMovie(1L, updateDto);

		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("Interstellar");
	}

	@Test
	void deleteMovie_ShouldDeleteSuccessfully() {
		when(movieRepository.existsById(1L)).thenReturn(true);

		movieService.deleteMovie(1L);

		verify(movieRepository).deleteById(1L);
	}

	@Test
	void deleteMovie_ShouldThrowException_WhenNotFound() {
		when(movieRepository.existsById(1L)).thenReturn(false);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> movieService.deleteMovie(1L));

		assertThat(exception.getMessage()).isEqualTo("Фильм с ID 1 не найден");
	}

	@Test
	void findMoviesByGenre_ShouldReturnMovieList() {
		Movie movie = new Movie();
		movie.setId(1L);
		movie.setGenre("Sci-Fi");

		when(movieRepository.findByGenre("Sci-Fi")).thenReturn(List.of(movie));

		MovieResponseDto responseDto = MovieResponseDto.builder()
			.id(1L)
			.title("Inception")
			.build();

		when(movieMapper.toResponseDto(movie)).thenReturn(responseDto);

		List<MovieResponseDto> result = movieService.findMoviesByGenre("Sci-Fi");

		assertThat(result).isNotEmpty();
		assertThat(result.get(0).getTitle()).isEqualTo("Inception");
	}

	@Test
	void findTopRatedMoviesByGenre_ShouldReturnTopMovies() {
		Movie movie = new Movie();
		movie.setId(2L);

		when(movieRepository.findTopMoviesByGenreOrderByAverageRatingDesc("Sci-Fi")).thenReturn(List.of(movie));

		MovieResponseDto responseDto = MovieResponseDto.builder()
			.id(2L)
			.title("Interstellar")
			.build();

		when(movieMapper.toResponseDto(movie)).thenReturn(responseDto);

		List<MovieResponseDto> result = movieService.findTopRatedMoviesByGenre("Sci-Fi");

		assertThat(result).isNotEmpty();
		assertThat(result.get(0).getTitle()).isEqualTo("Interstellar");
	}
}