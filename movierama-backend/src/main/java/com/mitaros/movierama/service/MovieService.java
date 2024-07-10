package com.mitaros.movierama.service;

import com.mitaros.movierama.domain.Movie;
import com.mitaros.movierama.domain.MovieView;
import com.mitaros.movierama.domain.Vote;
import com.mitaros.movierama.dto.MovieRequest;
import com.mitaros.movierama.dto.MovieResponse;
import com.mitaros.movierama.dto.VoteRequest;
import com.mitaros.movierama.dto.enums.BusinessErrorCodes;
import com.mitaros.movierama.dto.enums.MovieSortField;
import com.mitaros.movierama.exceptions.MovieramaGenericException;
import com.mitaros.movierama.repository.MovieRepository;
import com.mitaros.movierama.repository.MovieViewRepository;
import com.mitaros.movierama.security.MovieramaUserDetails;
import com.mitaros.movierama.utils.MapperUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.mitaros.movierama.dto.enums.VoteType.HATE;
import static com.mitaros.movierama.dto.enums.VoteType.LIKE;
import static com.mitaros.movierama.utils.MovieSpecification.withOwnerId;
import static com.mitaros.movierama.utils.MovieSpecification.withTitle;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieViewRepository movieViewRepository;
    private final VoteService voteService;

    /**
     * Save a movie
     *
     * @param request       the movie request
     * @param connectedUser the authenticated user
     * @return the saved movie
     */
    public MovieResponse save(MovieRequest request, Authentication connectedUser) {
        MovieramaUserDetails user = ((MovieramaUserDetails) connectedUser.getPrincipal());
        Movie movie = MapperUtil.toMovie(request);
        movie.setUser(user.getUser());
        movie.setCreatedBy(user.getUser().getId());
        movie.setCreatedDate(LocalDateTime.now());
        return MapperUtil.toMovieResponse(movieRepository.save(movie));
    }

    /**
     * Edit a movie
     *
     * @param movieId       the movie ID
     * @param request       the movie request
     * @param connectedUser the authenticated user
     * @return the updated movie
     */
    public MovieResponse editMovie(Long movieId, MovieRequest request, Authentication connectedUser) {
        final MovieramaUserDetails user = ((MovieramaUserDetails) connectedUser.getPrincipal());
        final Movie existingMovie = findById(movieId);

        // Check if the authenticated user is the owner of the movie
        if (!Objects.equals(existingMovie.getUser().getId(), user.getId())) {
            throw new AccessDeniedException("You do not have permission to edit this movie");
        }

        // Update movie details
        existingMovie.setTitle(request.getTitle());
        existingMovie.setDescription(request.getDescription());
        existingMovie.setPublicationDate(request.getPublicationDate());

        // Save the updated movie
        Movie updatedMovie = movieRepository.save(existingMovie);

        return MapperUtil.toMovieResponse(updatedMovie);
    }

    /**
     * Find a movie by ID
     *
     * @param movieId the movie ID
     * @return the movie
     */
    public Movie findById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("No movie found with ID:: " + movieId));
    }

    /**
     * Find a movie view by ID
     *
     * @param movieId the movie ID
     * @return the movie view
     */
    public MovieView findMovieViewById(Long movieId) {
        return movieViewRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("No movie found with ID:: " + movieId));
    }

    /**
     * Find a movie by ID
     *
     * @param movieId the movie ID
     * @return the movie response
     */
    public MovieResponse findMovieById(Long movieId) {
        return MapperUtil.toMovieResponse(findById(movieId));
    }

    /**
     * Find a movie view by ID
     *
     * @param movieId the movie ID
     * @return the movie response
     */
    public MovieResponse findMovieView(Long movieId) {
        return MapperUtil.toMovieResponse(findMovieViewById(movieId));
    }

    /**
     * Find all movies
     *
     * @param page      the page number
     * @param size      the page size
     * @param sortField the sort field
     * @param sortType  the sort type
     * @param userId    the user ID
     * @param title     the movie title
     * @return the movies
     */
    public Page<MovieResponse> findAllMovies(int page, int size, MovieSortField sortField, Sort.Direction sortType, Long userId, String title) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortType, sortField.getValue()));
        Page<MovieView> movies = Objects.nonNull(userId)
                ? movieViewRepository.findAll(withOwnerId(userId), pageable)
                : movieViewRepository.findAll(withTitle(title), pageable);
        List<MovieResponse> movieResponseList = movies.stream()
                .map(MapperUtil::toMovieResponse)
                .toList();
        return new PageImpl<>(movieResponseList, pageable, movies.getTotalElements());
    }

    /**
     * Find all movies by owner
     *
     * @param ownerId   the owner ID
     * @param page      the page number
     * @param size      the page size
     * @param sortField the sort field
     * @param sortType  the sort type
     * @return the movies
     */
    public Page<MovieResponse> findAllMoviesByOwner(Long ownerId, int page, int size, MovieSortField sortField, Sort.Direction sortType) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortType, sortField.getValue()));
        Page<MovieView> movies = movieViewRepository.findAll(withOwnerId(ownerId), pageable);
        List<MovieResponse> movieResponseList = movies.stream()
                .map(MapperUtil::toMovieResponse)
                .toList();
        return new PageImpl<>(movieResponseList, pageable, movies.getTotalElements());
    }

    /**
     * Vote on a movie
     *
     * @param movieId       the movie ID
     * @param request       the vote request
     * @param connectedUser the authenticated user
     * @return the movie response
     */
    public MovieResponse voteMovie(Long movieId, VoteRequest request, Authentication connectedUser) {
        final MovieramaUserDetails user = ((MovieramaUserDetails) connectedUser.getPrincipal());
        final Movie movie = findById(movieId);
        if (Objects.equals(movie.getUser().getId(), user.getId())) {
            throw new MovieramaGenericException(BusinessErrorCodes.VOTE_ON_SUBMITTED_MOVIE);
        }
        Vote vote = voteService.addVoteOnMovie(movie, request, connectedUser);
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .publicationDate(movie.getPublicationDate())
                .createdDate(vote.getMovie().getCreatedDate().toLocalDate())
                .likesCount(Math.toIntExact(vote.getMovie().getVotes().stream().filter(v -> v.getType() == LIKE).count()))
                .hatesCount(Math.toIntExact(vote.getMovie().getVotes().stream().filter(v -> v.getType() == HATE).count()))
                .user(MapperUtil.toUserResponse(vote.getMovie().getUser()))
                .build();
    }

}