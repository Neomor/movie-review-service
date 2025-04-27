package com.example.moviereviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
	private Long id;
	private int rating;
	private String comment;
	private String reviewerName;
	private Long movieId;
}