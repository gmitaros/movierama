package com.mitaros.movierama.controller;

import com.mitaros.movierama.dto.LoginRequest;
import com.mitaros.movierama.dto.LoginResponse;
import com.mitaros.movierama.dto.RegistrationRequest;
import com.mitaros.movierama.dto.RegistrationResponse;
import com.mitaros.movierama.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {

    private AuthenticationService authenticationService = mock(AuthenticationService.class);

    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        controller=new AuthenticationController(authenticationService);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register() throws Exception {
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "john.doe@example.com", "password123");
        var resp = RegistrationResponse.builder()
                .message("User registered successfully")
                .userId(1L)
                .build();
        when(authenticationService.register(request)).thenReturn(resp);
        ResponseEntity<?> response = controller.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(resp, response.getBody());
        verify(authenticationService, times(1)).register(request);
    }

    @Test
    void authenticate() {
        LoginRequest request = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();
        LoginResponse mockResponse = LoginResponse.builder().token("token123").build();

        when(authenticationService.authenticate(request)).thenReturn(mockResponse);

        ResponseEntity<LoginResponse> response = controller.authenticate(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(authenticationService, times(1)).authenticate(request);
    }

    @Test
    void confirm() throws Exception {
        String activationCode = "123456";

        controller.confirm(activationCode);

        verify(authenticationService, times(1)).activateAccount(activationCode);
    }
}