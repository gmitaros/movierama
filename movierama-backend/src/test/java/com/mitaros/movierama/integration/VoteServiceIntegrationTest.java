package com.mitaros.movierama.integration;

import com.mitaros.movierama.domain.Role;
import com.mitaros.movierama.domain.User;
import com.mitaros.movierama.dto.VoteRequest;
import com.mitaros.movierama.dto.VoteResponse;
import com.mitaros.movierama.dto.enums.VoteType;
import com.mitaros.movierama.exceptions.MovieramaGenericException;
import com.mitaros.movierama.repository.UserRepository;
import com.mitaros.movierama.security.JwtService;
import com.mitaros.movierama.security.MovieramaUserDetails;
import com.mitaros.movierama.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Sql(value = "/test_data/insert_movie.sql")
public class VoteServiceIntegrationTest {

    @Autowired
    private VoteService voteService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private Authentication authentication;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testFindMovieAndAddVoteIntegration_withError() {
        User existingUser = userRepository.findByEmail("integration2@test.com").orElseThrow();
        User user = createUser();
        user.setId(existingUser.getId());
        MovieramaUserDetails userDetails = new MovieramaUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        Throwable exception = assertThrows(MovieramaGenericException.class,
                () -> voteService.findMovieAndAddVote(14L, new VoteRequest(VoteType.LIKE), authentication), "Expected findMovieAndAddVote() to throw MovieramaGenericException");

        assertEquals("You can't vote for a movie that you have submitted", exception.getMessage(),
                "Expected exception message does not match");
    }

    @Test
    void testFindMovieAndAddVoteIntegration() {
        User existingUser = userRepository.findByEmail("integration3@test.com").orElseThrow();
        User user = createUser();
        user.setId(existingUser.getId());
        MovieramaUserDetails userDetails = new MovieramaUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        VoteResponse voteResponse = voteService.findMovieAndAddVote(14L, new VoteRequest(VoteType.LIKE), authentication);

        assertEquals(14L, voteResponse.getMovieId(), "Expected movie ID does match");
    }

    @Test
    public void testVoteMovie() throws Exception {
        var user = userRepository.findByEmail("integration3@test.com");
        var token = jwtService.generateToken(new MovieramaUserDetails(user.get()));
        var voteMovie = """
                {
                     "voteType": "HATE"
                 }
                """;

        mockMvc.perform(put("/votes/movie/16")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteMovie)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").isEmpty());
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

}
