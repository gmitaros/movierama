package com.mitaros.movierama.utils;


import com.mitaros.movierama.domain.Movie;
import com.mitaros.movierama.domain.MovieView;
import com.mitaros.movierama.domain.User;
import com.mitaros.movierama.domain.Vote;
import com.mitaros.movierama.dto.MovieRequest;
import com.mitaros.movierama.dto.MovieResponse;
import com.mitaros.movierama.dto.UserResponse;
import com.mitaros.movierama.dto.VoteResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Collections;


@UtilityClass
public class MapperUtil {

    /**
     * Converts a MovieRequest to a Movie
     *
     * @param request the MovieRequest to convert
     * @return the Movie
     */
    public Movie toMovie(MovieRequest request) {
        return Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .publicationDate(request.getPublicationDate())
                .votes(Collections.emptyList())
                .createdDate(LocalDateTime.now())
                .build();
    }

    /**
     * Converts a Movie to a MovieResponse
     *
     * @param movie the Movie to convert
     * @return the MovieResponse
     */
    public MovieResponse toMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .createdDate(movie.getCreatedDate().toLocalDate())
                .publicationDate(movie.getPublicationDate())
                .votes(movie.getVotes().stream().map(MapperUtil::toVoteResponse).toList())
                .user(toUserResponse(movie.getUser()))
                .build();
    }

    /**
     * Converts a MovieView to a MovieResponse
     *
     * @param movie the MovieView to convert
     * @return the MovieResponse
     */
    public MovieResponse toMovieResponse(MovieView movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .publicationDate(movie.getPublicationDate())
                .createdDate(movie.getCreatedDate())
                .likesCount(movie.getLikesCount())
                .hatesCount(movie.getHatesCount())
                .user(toUserResponse(movie.getUser()))
                .build();
    }

    /**
     * Converts a Vote to a VoteResponse
     *
     * @param vote the Vote to convert
     * @return the VoteResponse
     */
    public VoteResponse toVoteResponse(Vote vote) {
        return VoteResponse.builder()
                .id(vote.getId())
                .type(vote.getType())
                .movieId(vote.getMovie().getId())
                .userId(vote.getUser().getId())
                .build();
    }

    /**
     * Converts a User to a UserResponse
     *
     * @param user the User to convert
     * @return the UserResponse
     */
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .build();
    }
}