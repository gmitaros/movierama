package com.mitaros.movierama.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitaros.movierama.MovieRamaApplication;
import com.mitaros.movierama.dto.LoginRequest;
import com.mitaros.movierama.dto.LoginResponse;
import com.mitaros.movierama.dto.RegistrationRequest;
import com.mitaros.movierama.dto.RegistrationResponse;
import com.mitaros.movierama.exceptions.InvalidTokenException;
import com.mitaros.movierama.exceptions.MovieramaGenericException;
import com.mitaros.movierama.exceptions.TokenAlreadyValidatedException;
import com.mitaros.movierama.exceptions.TokenExpiredException;
import com.mitaros.movierama.repository.UserRepository;
import com.mitaros.movierama.security.JwtService;
import com.mitaros.movierama.security.MovieramaUserDetails;
import com.mitaros.movierama.service.AuthenticationService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {MovieRamaApplication.class, AuthenticationService.class})
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {JavaMailSender.class})
@AutoConfigureMockMvc
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    public void testAuthenticate() throws Exception {
        var user = userRepository.findByEmail("movierama@movierama.com");
        var token = jwtService.generateToken(new MovieramaUserDetails(user.get()));
        LoginRequest request = LoginRequest.builder()
                .email("movierama@movierama.com")
                .password("movieram@")
                .build();
        String requestBody = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
    }

    @Test
    public void testGetUserInfo() throws Exception {
        var user = userRepository.findByEmail("movierama@movierama.com");
        var token = jwtService.generateToken(new MovieramaUserDetails(user.get()));
        LoginRequest request = LoginRequest.builder()
                .email("movierama@movierama.com")
                .password("movieram@")
                .build();
        String requestBody = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(get("/user/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists());
    }

    @Test
    public void testAuthenticate_login_valid() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("movierama@movierama.com")
                .password("movieram@")
                .build();
        String requestBody = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
    }

    @Test
    void register() throws Exception {
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "john.doe@example.com", "password123");
        RegistrationResponse response = authenticationService.register(request);
        assertNotNull(response);
        assertEquals("User registered successfully", response.getMessage());
        assertNotNull(response.getUserId());
    }

    @Test
    void register_withEmailActivation() throws Exception {
        ReflectionTestUtils.setField(authenticationService, "emailValidationEnabled", true);
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "withEmail@example.com", "password123");
        RegistrationResponse response = authenticationService.register(request);
        assertNotNull(response);
        assertEquals("User registered successfully", response.getMessage());
        assertNotNull(response.getUserId());
    }

    @Test
    void register_mail_exists() throws Exception {
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "movierama@movierama.com", "password123");
        Throwable exception = assertThrows(MovieramaGenericException.class,
                () -> authenticationService.register(request), "Expected register() to throw MovieramaGenericException");

        assertEquals("User already exists", exception.getMessage(),
                "Expected exception message does not match");
    }

    @Test
    void authenticate_credentials_exists() {
        LoginRequest request = LoginRequest.builder()
                .email("movierama@movierama.com")
                .password("movieram@")
                .build();
        LoginResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
    }

    @Test
    void authenticate_credentials_not_exists() {
        LoginRequest request = LoginRequest.builder()
                .email("movierama@movierama.com")
                .password("11")
                .build();
        Throwable exception = assertThrows(BadCredentialsException.class,
                () -> authenticationService.authenticate(request), "Expected activateAccount() to throw TokenExpiredException");

    }

    @Test
    @Sql(value = "/test_data/insert_user_token_valid.sql")
    void activateAccount() throws Exception {
        String activationCode = "2424234";
        assertDoesNotThrow(() -> authenticationService.activateAccount(activationCode),
                "Expected activateAccount() to not throw any exceptions");
    }

    @Test
    @Sql(value = "/test_data/insert_user_token_invalid.sql")
    void activateAccount_token_expired() throws Exception {
        String activationCode = "123456";
        Throwable exception = assertThrows(TokenExpiredException.class,
                () -> authenticationService.activateAccount(activationCode), "Expected activateAccount() to throw TokenExpiredException");

        assertEquals("The activation token has expired. A new token has been sent to the same email address.", exception.getMessage(),
                "Expected exception message does not match");
    }

    @Test
    @Sql(value = "/test_data/insert_user_token_invalid.sql")
    void activateAccount_token_invalid() throws Exception {
        String activationCode = "43563643";
        Throwable exception = assertThrows(InvalidTokenException.class,
                () -> authenticationService.activateAccount(activationCode), "Expected activateAccount() to throw InvalidTokenException");

        assertEquals("Invalid token", exception.getMessage(),
                "Expected exception message does not match");
    }

    @Test
    @Sql(value = "/test_data/insert_user_token_invalid.sql")
    void activateAccount_token_already_validated() throws Exception {
        String activationCode = "123444";
        Throwable exception = assertThrows(TokenAlreadyValidatedException.class,
                () -> authenticationService.activateAccount(activationCode), "Expected activateAccount() to throw TokenAlreadyValidatedException");

        assertEquals("The activation token is already validated.", exception.getMessage(),
                "Expected exception message does not match");
    }

}