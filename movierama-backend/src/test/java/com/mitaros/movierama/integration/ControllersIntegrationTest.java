package com.mitaros.movierama.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitaros.movierama.MovieRamaApplication;
import com.mitaros.movierama.dto.LoginRequest;
import com.mitaros.movierama.repository.UserRepository;
import com.mitaros.movierama.security.JwtService;
import com.mitaros.movierama.security.MovieramaUserDetails;
import com.mitaros.movierama.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {MovieRamaApplication.class, AuthenticationService.class})
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {JavaMailSender.class})
@AutoConfigureMockMvc
@Sql(value = "/test_data/insert_movie.sql")
class ControllersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindMovies() throws Exception {
        var user = userRepository.findByEmail("movierama@movierama.com");
        var token = jwtService.generateToken(new MovieramaUserDetails(user.get()));
        LoginRequest request = LoginRequest.builder()
                .email("movierama@movierama.com")
                .password("movieram@")
                .build();
        String requestBody = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(get("/public/movies?page=0&size=2&sortField=CREATED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").isNumber());
    }

    @Test
    public void testActivateAccount() throws Exception {
        var user = userRepository.findByEmail("movierama@movierama.com");
        var token = jwtService.generateToken(new MovieramaUserDetails(user.get()));
        LoginRequest request = LoginRequest.builder()
                .email("movierama@movierama.com")
                .password("movieram@")
                .build();
        String requestBody = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(get("/auth/activate-account?activation-code=455299")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorDescription").isString());
    }

    @Test
    public void testUsernameNotFound() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("na@movierama.com")
                .password("movieram@")
                .build();
        String requestBody = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorDescription").isString());
    }

    @Test
    public void testFindAllMovies_with_a_in_title() throws Exception {
        mockMvc.perform(get("/public/movies?sortField=CREATED&page=0&size=10&sortType=ASC&title=pulp")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title").value("Pulp Fiction"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].title").value("Pulp Fiction22222222222"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(2));
    }

}