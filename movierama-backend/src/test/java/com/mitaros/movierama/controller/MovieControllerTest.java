package com.mitaros.movierama.controller;

import com.mitaros.movierama.dto.MovieRequest;
import com.mitaros.movierama.dto.MovieResponse;
import com.mitaros.movierama.dto.VoteRequest;
import com.mitaros.movierama.dto.enums.VoteType;
import com.mitaros.movierama.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Arrays;

import static com.mitaros.movierama.dto.enums.MovieSortField.CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;

class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMovie() {
        MovieRequest request = new MovieRequest("Title", "Description", LocalDate.now());
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password");
        when(movieService.save(request, authentication)).thenReturn(new MovieResponse());
        ResponseEntity<MovieResponse> response = movieController.saveMovie(request, authentication);
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void testFindMovieById() {
        Long movieId = 1L;
        when(movieService.findMovieById(movieId)).thenReturn(new MovieResponse());
        ResponseEntity<MovieResponse> response = movieController.findMovieById(movieId);
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void testFindAllMovies() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(DESC, CREATED.getValue()));
        Page<MovieResponse> page = new PageImpl<>(Arrays.asList(new MovieResponse()), pageable, 1);
        when(movieService.findAllMovies(0, 20, CREATED, DESC, 1L, null)).thenReturn(page);
        ResponseEntity<Page<MovieResponse>> response = movieController.findAllMovies(0, 20, 1L, CREATED, DESC, null);
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void voteMovie() {
        var voteRequest = new VoteRequest(VoteType.LIKE);
        var userAuth = new UsernamePasswordAuthenticationToken("user", "password");
        var movieResp = new MovieResponse();
        when(movieService.voteMovie(1L, voteRequest, userAuth)).thenReturn(movieResp);
        ResponseEntity<MovieResponse> response = movieController.voteMovie(1L, voteRequest, new UsernamePasswordAuthenticationToken("user", "password"));
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }
}
