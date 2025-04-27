package com.example.moviereviews.mapper;

import com.example.moviereviews.dto.ReviewRequestDto;
import com.example.moviereviews.dto.ReviewResponseDto;
import com.example.moviereviews.model.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

	public Review toEntity(ReviewRequestDto dto) {
		if (dto == null) {
			return null;
		}
		Review review = new Review();
		review.setRating(dto.getRating());
		review.setComment(dto.getComment());
		review.setReviewerName(dto.getReviewerName());
		return review;
	}

	public ReviewResponseDto toResponseDto(Review review) {
		if (review == null) {
			return null;
		}
		return ReviewResponseDto.builder()
			.id(review.getId())
			.rating(review.getRating())
			.comment(review.getComment())
			.reviewerName(review.getReviewerName())
			.movieId(review.getMovie() != null ? review.getMovie().getId() : null)
			.build();
	}
}