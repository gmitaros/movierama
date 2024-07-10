package com.mitaros.movierama.service;

import com.mitaros.movierama.domain.Movie;
import com.mitaros.movierama.domain.MovieView;
import com.mitaros.movierama.domain.Role;
import com.mitaros.movierama.domain.User;
import com.mitaros.movierama.domain.Vote;
import com.mitaros.movierama.dto.MovieRequest;
import com.mitaros.movierama.dto.MovieResponse;
import com.mitaros.movierama.dto.VoteRequest;
import com.mitaros.movierama.dto.enums.MovieSortField;
import com.mitaros.movierama.dto.enums.VoteType;
import com.mitaros.movierama.repository.MovieRepository;
import com.mitaros.movierama.repository.MovieViewRepository;
import com.mitaros.movierama.security.MovieramaUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieViewRepository movieViewRepository;

    @Mock
    private VoteService voteService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MovieService movieService;

    private static final Long MOVIE_ID = 1L;
    private static final Long USER_ID = 2L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveMovie() {
        MovieRequest movieRequest = new MovieRequest("Title", "Description", LocalDate.now());
        MovieramaUserDetails userDetails = new MovieramaUserDetails(createUser());
        when(authentication.getPrincipal()).thenReturn(userDetails);

        Movie movie = createMovie(MOVIE_ID, USER_ID);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieResponse response = movieService.save(movieRequest, authentication);

        assertThat(response.getId()).isEqualTo(MOVIE_ID);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    public void testFindById() {
        Movie movie = new Movie();
        movie.setId(MOVIE_ID);
        when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.of(movie));

        Movie result = movieService.findById(MOVIE_ID);

        assertThat(result.getId()).isEqualTo(MOVIE_ID);
    }

    @Test
    public void testFindMovieViewById() {
        MovieView movieView = new MovieView();
        movieView.setId(MOVIE_ID);
        when(movieViewRepository.findById(MOVIE_ID)).thenReturn(Optional.of(movieView));

        MovieView result = movieService.findMovieViewById(MOVIE_ID);

        assertThat(result.getId()).isEqualTo(MOVIE_ID);
    }

    @Test
    public void testFindAllMovies() {
        Page<MovieView> page = new PageImpl<>(List.of(createMovieView(MOVIE_ID, USER_ID)));
        when(movieViewRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<MovieResponse> result = movieService.findAllMovies(0, 10, MovieSortField.CREATED, Sort.Direction.DESC, null, null);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testFindAllMoviesByOwner() {
        Page<MovieView> page = new PageImpl<>(List.of(createMovieView(MOVIE_ID, USER_ID)));
        when(movieViewRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<MovieResponse> result = movieService.findAllMoviesByOwner(USER_ID, 0, 10, MovieSortField.CREATED, Sort.Direction.DESC);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testVoteMovie() {
        VoteRequest voteRequest = new VoteRequest(VoteType.LIKE);
        Movie movie = createMovie(MOVIE_ID, USER_ID);
        when(authentication.getPrincipal()).thenReturn(new MovieramaUserDetails(createUser()));
        when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.of(movie));
        Vote vote = Vote.builder()
                .id(2L)
                .movie(movie)
                .type(VoteType.LIKE)
                .createdBy(USER_ID)
                .createdDate(LocalDateTime.now())
                .user(createUser())
                .build();
        when(voteService.addVoteOnMovie(movie, voteRequest, authentication)).thenReturn(vote);
        MovieResponse response = movieService.voteMovie(MOVIE_ID, voteRequest, authentication);

        assertThat(response.getId()).isEqualTo(MOVIE_ID);
        verify(voteService, times(1)).addVoteOnMovie(movie, voteRequest, authentication);
    }

    public static User createUser() {
        return User.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("johndoe@example.com")
                .password("password123")
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(Role.builder().name("USER").build()))
                .movies(List.of())
                .votes(List.of())
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static Movie createMovie(Long id, Long userId) {
        return Movie.builder()
                .id(id)
                .title("Title")
                .description("Description")
                .publicationDate(LocalDate.now().minusDays(1))
                .user(User.builder().id(userId).build())
                .createdDate(LocalDateTime.now())
                .votes(emptyList())
                .build();
    }

    public static MovieView createMovieView(Long id, Long userId) {
        return MovieView.builder()
                .id(id)
                .title("Title")
                .description("Description")
                .publicationDate(LocalDate.now().minusDays(1))
                .user(User.builder().id(userId).build())
                .hatesCount(2)
                .likesCount(2)
                .createdDate(LocalDate.now())
                .build();
    }

}