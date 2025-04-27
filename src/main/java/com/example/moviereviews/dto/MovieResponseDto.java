package com.example.moviereviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDto {
	private Long id;
	private String title;
	private int releaseYear;
	private String genre;
	private String director;
	private List<ReviewResponseDto> reviews;
}