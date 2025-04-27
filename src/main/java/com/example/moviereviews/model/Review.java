package com.example.moviereviews.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int rating;

	private String comment;

	@Column(name = "reviewer_name")
	private String reviewerName;

	@ManyToOne
	@JoinColumn(name = "movie_id")
	private Movie movie;
}