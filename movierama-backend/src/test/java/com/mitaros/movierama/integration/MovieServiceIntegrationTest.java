package com.mitaros.movierama.integration;

import com.mitaros.movierama.domain.Movie;
import com.mitaros.movierama.domain.Role;
import com.mitaros.movierama.domain.User;
import com.mitaros.movierama.dto.MovieRequest;
import com.mitaros.movierama.dto.MovieResponse;
import com.mitaros.movierama.dto.VoteRequest;
import com.mitaros.movierama.dto.enums.MovieSortField;
import com.mitaros.movierama.dto.enums.VoteType;
import com.mitaros.movierama.exceptions.MovieramaGenericException;
import com.mitaros.movierama.repository.UserRepository;
import com.mitaros.movierama.security.JwtService;
import com.mitaros.movierama.security.MovieramaUserDetails;
import com.mitaros.movierama.service.MovieService;
import com.mitaros.movierama.utils.MapperUtil;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Sql(value = "/test_data/insert_movie.sql")
public class MovieServiceIntegrationTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveMovie() {
        // Arrange
        MovieRequest movieRequest = new MovieRequest("Fake Title", "Fake Description", LocalDate.now());
        Authentication authentication = new UsernamePasswordAuthenticationToken(new MovieramaUserDetails(createUser()), null);

        MovieResponse expectedResponse = MapperUtil.toMovieResponse(createMovie(1L, 1L));
        // Act
        MovieResponse actualResponse = movieService.save(movieRequest, authentication);

        // Assert
        assertEquals(expectedResponse.getTitle(), actualResponse.getTitle());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(expectedResponse.getPublicationDate(), actualResponse.getPublicationDate());
        assertNotNull(actualResponse.getCreatedDate());
    }

    @Test
    @Transactional
    public void testVoteMovie() {
        // Arrange
        VoteRequest voteRequest = new VoteRequest(VoteType.LIKE);
        Authentication authentication = new UsernamePasswordAuthenticationToken(new MovieramaUserDetails(createUser()), null);
        // Act
        MovieResponse actualResponse = movieService.voteMovie(14L, voteRequest, authentication);

        // Assert
        assertEquals("Pulp Fiction", actualResponse.getTitle());
        assertEquals("The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.", actualResponse.getDescription());
        assertEquals(LocalDate.of(1994, 10, 14), actualResponse.getPublicationDate());
        assertEquals(0, actualResponse.getHatesCount());
        assertEquals(1, actualResponse.getLikesCount());
        assertNotNull(actualResponse.getCreatedDate());
    }

    @Test
    @Transactional
    public void testVoteMovie_that_is_mine() {
        // Arrange
        VoteRequest voteRequest = new VoteRequest(VoteType.LIKE);
        User user = userRepository.findByEmail("integration2@test.com").orElseThrow();
        Authentication authentication = new UsernamePasswordAuthenticationToken(new MovieramaUserDetails(user), null);
        // Act
        Throwable exception = assertThrows(MovieramaGenericException.class,
                () -> movieService.voteMovie(14L, voteRequest, authentication), "Expected voteMovie() to throw MovieramaGenericException");

        assertEquals("You can't vote for a movie that you have submitted", exception.getMessage(),
                "Expected exception message does not match");
    }

    @Test
    public void findMovieById_that_not_exists() {
        // Act
        Throwable exception = assertThrows(EntityNotFoundException.class,
                () -> movieService.findMovieById(100L), "Expected findMovieById() to throw EntityNotFoundException");

        assertEquals("No movie found with ID:: 100", exception.getMessage(),
                "Expected exception message does not match");
    }

    @Test
    public void findMovieView_that_not_exists() {
        // Act
        Throwable exception = assertThrows(EntityNotFoundException.class,
                () -> movieService.findMovieView(100L), "Expected findMovieView() to throw EntityNotFoundException");

        assertEquals("No movie found with ID:: 100", exception.getMessage(),
                "Expected exception message does not match");
    }

    @Test
    @Transactional
    public void testFindAllMoviesByOwner() {
        // Act
        User user = userRepository.findByEmail("integration2@test.com").orElseThrow();
        Page<MovieResponse> movies = movieService.findAllMovies(0, 10, MovieSortField.HATES, Sort.Direction.ASC, user.getId(), null);
        // Assert
        List<MovieResponse> movieResponseList = movies.getContent();
        assertEquals(1, movies.getTotalElements());
        assertEquals("Pulp Fiction", movieResponseList.get(0).getTitle());
        assertEquals("The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.", movieResponseList.get(0).getDescription());
        assertEquals(LocalDate.of(1994, 10, 14), movieResponseList.get(0).getPublicationDate());
    }

    @Test
    @Transactional
    public void testFindAllMovies() {
        // Act
        Page<MovieResponse> movies = movieService.findAllMovies(0, 10, MovieSortField.HATES, Sort.Direction.ASC, null, null);
        // Assert
        List<MovieResponse> movieResponseList = movies.getContent();
        assertEquals(4, movies.getTotalElements());
        assertEquals("Pulp Fiction", movieResponseList.get(0).getTitle());
        assertEquals("The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.", movieResponseList.get(0).getDescription());
        assertEquals(LocalDate.of(1994, 10, 14), movieResponseList.get(0).getPublicationDate());
    }

    @Test
    public void testEditMovie() throws Exception {
        var user = userRepository.findByEmail("movierama@movierama.com");
        var token = jwtService.generateToken(new MovieramaUserDetails(user.get()));
        var requestEditMovie = """
                {
                  "title": "Movie Title 5",
                  "description": "Movie Description 5",
                  "publicationDate": "2023-07-12"
                }
                """;

        mockMvc.perform(put("/movies/16")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestEditMovie)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicationDate").value("2023-07-12"))
                .andExpect(jsonPath("$.title").value("Movie Title 5"));
    }

    @Test
    public void testEditMovie_not_belongs_to_me() throws Exception {
        var user = userRepository.findByEmail("movierama@movierama.com");
        var token = jwtService.generateToken(new MovieramaUserDetails(user.get()));
        var requestEditMovie = """
                {
                  "title": "Movie Title 5",
                  "description": "Movie Description 5",
                  "publicationDate": "2023-07-12"
                }
                """;
        mockMvc.perform(put("/movies/14")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestEditMovie)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("You do not have permission to edit this movie"));
    }

    @Test
    public void testEditMovie_validation_error() throws Exception {
        var user = userRepository.findByEmail("movierama@movierama.com");
        var token = jwtService.generateToken(new MovieramaUserDetails(user.get()));
        var requestEditMovie = """
                {
                  "description": "Movie Description 5",
                  "publicationDate": "2023-07-12"
                }
                """;
        mockMvc.perform(put("/movies/14")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestEditMovie)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.validationErrors", Matchers.containsInAnyOrder(
                        "Title cannot be null",
                        "Title cannot be empty"
                )));
    }

    @Test
    public void testFindMovieById() throws Exception {
        var user = userRepository.findByEmail("movierama@movierama.com");
        var token = jwtService.generateToken(new MovieramaUserDetails(user.get()));
        mockMvc.perform(get("/movies/14")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Pulp Fiction"));
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
                .title("Fake Title")
                .description("Fake Description")
                .publicationDate(LocalDate.now())
                .user(User.builder().id(userId).build())
                .createdDate(LocalDateTime.now())
                .votes(emptyList())
                .build();
    }

}
