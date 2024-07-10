package com.mitaros.movierama.service;

import com.mitaros.movierama.domain.Movie;
import com.mitaros.movierama.domain.User;
import com.mitaros.movierama.domain.Vote;
import com.mitaros.movierama.dto.VoteRequest;
import com.mitaros.movierama.dto.VoteResponse;
import com.mitaros.movierama.dto.enums.VoteType;
import com.mitaros.movierama.exceptions.MovieramaGenericException;
import com.mitaros.movierama.repository.MovieRepository;
import com.mitaros.movierama.repository.VoteRepository;
import com.mitaros.movierama.security.MovieramaUserDetails;
import com.mitaros.movierama.utils.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private VoteService voteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserVotes() {
        // Mock authentication
        Authentication auth = new UsernamePasswordAuthenticationToken(new MovieramaUserDetails(new User()), null);
        // Mock data
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setUser(new User());
        Vote expectedVote = Vote.builder()
                .movie(movie)
                .type(VoteType.LIKE)
                .user(new User())
                .createdBy(1L)
                .createdDate(LocalDateTime.now())
                .build();
        // Mock data
        when(voteRepository.findAllByUser(any())).thenReturn(List.of(expectedVote));

        List<VoteResponse> userVotes = voteService.getUserVotes(auth);

        assertFalse(userVotes.isEmpty());
        verify(voteRepository).findAllByUser(any());
    }

    @Test
    void testFindMovieAndAddVote() {
        // Mock authentication
        Authentication auth = new UsernamePasswordAuthenticationToken(new MovieramaUserDetails(new User()), null);

        // Mock data
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setUser(new User());

        VoteRequest request = new VoteRequest(VoteType.LIKE);

        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(voteRepository.findByMovieAndUser(any(), any())).thenReturn(null);

        assertThrows(MovieramaGenericException.class, () -> voteService.findMovieAndAddVote(1L, request, auth));
    }

    @Test
    void testAddVoteOnMovie() {
        // Mock authentication
        Authentication auth = new UsernamePasswordAuthenticationToken(new MovieramaUserDetails(new User()), null);

        // Mock data
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setUser(new User());

        VoteRequest request = new VoteRequest(VoteType.LIKE);
        Vote expectedVote = Vote.builder()
                .movie(movie)
                .type(VoteType.LIKE)
                .user(new User())
                .createdBy(1L)
                .createdDate(LocalDateTime.now())
                .build();
        when(voteRepository.findByMovieAndUser(any(), any())).thenReturn(null);
        when(voteRepository.save(any())).thenReturn(expectedVote);


        Vote vote = voteService.addVoteOnMovie(movie, request, auth);

        assertNotNull(vote);
        assertEquals(VoteType.LIKE, vote.getType());
        verify(voteRepository).save(any());
    }

    @Test
    void testHandleVote() {
        Movie movie = new Movie();
        movie.setId(1L);
        User user = new User();
        user.setId(1L);

        Vote existingVote = Vote.builder()
                .movie(movie)
                .user(user)
                .type(VoteType.LIKE)
                .build();

        VoteRequest request = new VoteRequest(VoteType.HATE);

        Vote resultVote = voteService.handleVote(existingVote, request.voteType(), movie, user);

        assertNotNull(resultVote);
        assertEquals(VoteType.HATE, resultVote.getType());

        // Test for removing vote
        resultVote = voteService.handleVote(existingVote, request.voteType(), movie, user);

        assertNull(resultVote.getType());

        // Test for adding new vote
        existingVote = null;
        resultVote = voteService.handleVote(existingVote, request.voteType(), movie, user);

        assertNotNull(resultVote);
        assertEquals(VoteType.HATE, resultVote.getType());
    }
}