package com.example.moviereviews.service;

import com.example.moviereviews.dto.MovieRequestDto;
import com.example.moviereviews.dto.MovieResponseDto;
import com.example.moviereviews.dto.ReviewRequestDto;
import com.example.moviereviews.mapper.MovieMapper;
import com.example.moviereviews.mapper.ReviewMapper;
import com.example.moviereviews.model.Movie;
import com.example.moviereviews.model.Review;
import com.example.moviereviews.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private MovieMapper movieMapper;

	@Mock
	private ReviewMapper reviewMapper;

	@InjectMocks
	private MovieService movieService;

	@Test
	void testCreateMovieWithReviews() {
		MovieRequestDto requestDto = MovieRequestDto.builder()
			.title("Inception")
			.releaseYear(2010)
			.genre("Sci-Fi")
			.director("Christopher Nolan")
			.reviews(List.of(ReviewRequestDto.builder().rating(5).comment("Great").reviewerName("Alice").build()))
			.build();

		Movie movieEntity = new Movie();
		Movie savedMovie = new Movie();
		savedMovie.setReviews(new ArrayList<>());
		MovieResponseDto responseDto = new MovieResponseDto();

		when(movieMapper.toEntity(requestDto)).thenReturn(movieEntity);
		when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);
		when(reviewMapper.toEntity(any())).thenReturn(new Review());
		when(movieMapper.toResponseDto(any(Movie.class))).thenReturn(responseDto);

		MovieResponseDto result = movieService.createMovieWithReviews(requestDto);

		assertNotNull(result);
		verify(movieRepository, times(2)).save(any(Movie.class));
		verify(movieMapper).toEntity(requestDto);
		verify(movieMapper).toResponseDto(any(Movie.class));
	}

	@Test
	void testGetAllMovies() {
		Pageable pageable = PageRequest.of(0, 10);
		Movie movie = new Movie();
		MovieResponseDto dto = new MovieResponseDto();
		Page<Movie> moviePage = new PageImpl<>(List.of(movie));

		when(movieRepository.findAll(pageable)).thenReturn(moviePage);
		when(movieMapper.toResponseDto(any(Movie.class))).thenReturn(dto);

		Page<MovieResponseDto> result = movieService.getAllMovies(pageable);

		assertEquals(1, result.getTotalElements());
		verify(movieRepository).findAll(pageable);
	}

	@Test
	void testGetMovieByIdExists() {
		Movie movie = new Movie();
		movie.setId(1L);
		MovieResponseDto dto = new MovieResponseDto();

		when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
		when(movieMapper.toResponseDto(movie)).thenReturn(dto);

		MovieResponseDto result = movieService.getMovieById(1L);

		assertNotNull(result);
		verify(movieRepository).findById(1L);
	}

	@Test
	void testGetMovieByIdNotFound() {
		when(movieRepository.findById(1L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> movieService.getMovieById(1L));

		assertEquals("Фильм с ID 1 не найден", exception.getMessage());
	}

	@Test
	void testDeleteMovieExists() {
		when(movieRepository.existsById(1L)).thenReturn(true);

		movieService.deleteMovie(1L);

		verify(movieRepository).deleteById(1L);
	}

	@Test
	void testDeleteMovieNotFound() {
		when(movieRepository.existsById(1L)).thenReturn(false);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> movieService.deleteMovie(1L));

		assertEquals("Фильм с ID 1 не найден", exception.getMessage());
	}

	@Test
	void testFindMoviesByGenre() {
		List<Movie> movies = List.of(new Movie());
		when(movieRepository.findByGenre("Sci-Fi")).thenReturn(movies);
		when(movieMapper.toResponseDto(any(Movie.class))).thenReturn(new MovieResponseDto());

		List<MovieResponseDto> result = movieService.findMoviesByGenre("Sci-Fi");

		assertEquals(1, result.size());
		verify(movieRepository).findByGenre("Sci-Fi");
	}

	@Test
	void testFindTopRatedMoviesByGenre() {
		List<Movie> movies = List.of(new Movie());
		when(movieRepository.findTopMoviesByGenreOrderByAverageRatingDesc("Action")).thenReturn(movies);
		when(movieMapper.toResponseDto(any(Movie.class))).thenReturn(new MovieResponseDto());

		List<MovieResponseDto> result = movieService.findTopRatedMoviesByGenre("Action");

		assertEquals(1, result.size());
		verify(movieRepository).findTopMoviesByGenreOrderByAverageRatingDesc("Action");
	}
}