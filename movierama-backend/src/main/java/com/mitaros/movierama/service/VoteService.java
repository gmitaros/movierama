package com.mitaros.movierama.service;

import com.mitaros.movierama.domain.Movie;
import com.mitaros.movierama.domain.User;
import com.mitaros.movierama.domain.Vote;
import com.mitaros.movierama.dto.VoteRequest;
import com.mitaros.movierama.dto.VoteResponse;
import com.mitaros.movierama.dto.enums.BusinessErrorCodes;
import com.mitaros.movierama.dto.enums.VoteType;
import com.mitaros.movierama.exceptions.MovieramaGenericException;
import com.mitaros.movierama.repository.MovieRepository;
import com.mitaros.movierama.repository.VoteRepository;
import com.mitaros.movierama.security.MovieramaUserDetails;
import com.mitaros.movierama.utils.MapperUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;
    private final MovieRepository movieRepository;

    /**
     * Get the votes of the connected user
     *
     * @param connectedUser the connected user
     * @return the list of votes
     */
    public List<VoteResponse> getUserVotes(Authentication connectedUser) {
        MovieramaUserDetails user = ((MovieramaUserDetails) connectedUser.getPrincipal());
        return voteRepository.findAllByUser(user.getUser()).stream().map(MapperUtil::toVoteResponse).toList();
    }

    /**
     * Find a movie by ID and add a vote to it
     * If the movie is submitted by the connected user, throw an exception
     *
     * @param movieId       the movie ID
     * @param request       the vote request
     * @param connectedUser the connected user
     * @return the vote response
     */
    public VoteResponse findMovieAndAddVote(Long movieId, VoteRequest request, Authentication connectedUser) {
        MovieramaUserDetails user = ((MovieramaUserDetails) connectedUser.getPrincipal());
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("No movie found with ID: " + movieId));
        if (Objects.equals(movie.getUser().getId(), user.getId())) {
            throw new MovieramaGenericException(BusinessErrorCodes.VOTE_ON_SUBMITTED_MOVIE);
        }
        Vote vote = voteRepository.findByMovieAndUser(movie, user.getUser());
        vote = handleVote(vote, request.voteType(), movie, user.getUser());
        if (vote.getType() != null) {
            vote = voteRepository.save(vote);
        } else {
            voteRepository.delete(vote);
        }
        return MapperUtil.toVoteResponse(vote);
    }

    /**
     * Add a vote to a movie
     *
     * @param movie         the movie
     * @param request       the vote request
     * @param connectedUser the connected user
     * @return the vote
     */
    public Vote addVoteOnMovie(Movie movie, VoteRequest request, Authentication connectedUser) {
        MovieramaUserDetails user = ((MovieramaUserDetails) connectedUser.getPrincipal());
        Vote vote = voteRepository.findByMovieAndUser(movie, user.getUser());
        vote = handleVote(vote, request.voteType(), movie, user.getUser());
        if (vote.getType() != null) {
            vote = voteRepository.save(vote);
        } else {
            voteRepository.delete(vote);
        }
        return vote;
    }

    /**
     * Handle the vote.
     * If the vote is null, create a new vote.
     * If the vote is not null and the vote type is different from the request vote type, update the vote.
     * If the vote is not null and the vote type is the same as the request vote type, delete the vote.
     *
     * @param vote     the vote
     * @param voteType the vote type
     * @param movie    the movie
     * @param user     the user
     * @return the vote
     */
    Vote handleVote(Vote vote, VoteType voteType, Movie movie, User user) {
        if (Objects.nonNull(vote) && voteType != vote.getType()) {
            vote.setType(voteType);
        } else if (Objects.nonNull(vote)) {
            vote.setType(null);
        } else {
            vote = Vote.builder()
                    .movie(movie)
                    .type(voteType)
                    .user(user)
                    .createdBy(user.getId())
                    .createdDate(LocalDateTime.now())
                    .build();
        }
        return vote;
    }

}