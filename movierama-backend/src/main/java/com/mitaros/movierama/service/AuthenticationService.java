package com.mitaros.movierama.service;


import com.mitaros.movierama.domain.Role;
import com.mitaros.movierama.domain.Token;
import com.mitaros.movierama.domain.User;
import com.mitaros.movierama.dto.LoginRequest;
import com.mitaros.movierama.dto.LoginResponse;
import com.mitaros.movierama.dto.RegistrationRequest;
import com.mitaros.movierama.dto.RegistrationResponse;
import com.mitaros.movierama.dto.enums.EmailTemplate;
import com.mitaros.movierama.exceptions.InvalidTokenException;
import com.mitaros.movierama.exceptions.MovieramaGenericException;
import com.mitaros.movierama.exceptions.TokenAlreadyValidatedException;
import com.mitaros.movierama.exceptions.TokenExpiredException;
import com.mitaros.movierama.repository.RoleRepository;
import com.mitaros.movierama.repository.TokenRepository;
import com.mitaros.movierama.repository.UserRepository;
import com.mitaros.movierama.security.JwtService;
import com.mitaros.movierama.security.MovieramaUserDetails;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.mitaros.movierama.dto.enums.BusinessErrorCodes.USER_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${movierama.email.validation.enabled}")
    private boolean emailValidationEnabled;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    @Value("${application.activation.code.length:6}")
    private int activationCodeLength;
    @Value("${application.activation.code.valid.minutes:15}")
    private int activationCodeMinutesValidity;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    /**
     * Registers a new user
     *
     * @param request the registration request
     * @return the registration response
     * @throws MessagingException if an error occurs while sending the email
     */
    @Transactional
    public RegistrationResponse register(RegistrationRequest request) throws MessagingException {
        var userRole = getUserRole();
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new MovieramaGenericException(USER_EXISTS);
        }
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(!emailValidationEnabled)
                .roles(List.of(userRole))
                .createdDate(LocalDateTime.now())
                .build();
        var storedUser = userRepository.save(user);
        var newToken = generateAndSaveActivationToken(user);
        sendValidationEmail(storedUser, newToken);
        return RegistrationResponse.builder()
                .message("User registered successfully")
                .userId(storedUser.getId())
                .build();
    }

    /**
     * Retrieves the user role
     *
     * @return the user role
     */
    private Role getUserRole() {
        return roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER was not initiated"));
    }

    /**
     * Authenticates a user
     *
     * @param request the login request
     * @return the login response
     */
    public LoginResponse authenticate(LoginRequest request) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var claims = new HashMap<String, Object>();
            var user = ((MovieramaUserDetails) auth.getPrincipal());
            claims.put("fullName", user.getFullName());
            var jwtToken = jwtService.generateToken(claims, (MovieramaUserDetails) auth.getPrincipal());
            return LoginResponse.builder().token(jwtToken).build();
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for email: {}", request.getEmail(), e);
            throw new BadCredentialsException("Invalid credentials");
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for email: {}", request.getEmail(), e);
            throw new BadCredentialsException("Authentication failed");
        }
    }

    /**
     * Activates a user account
     *
     * @param token the activation token
     * @throws MessagingException if an error occurs while sending the email
     */
    @Transactional(noRollbackFor = TokenExpiredException.class)
    public void activateAccount(String token) throws MessagingException {
        final Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            final var newToken = generateAndSaveActivationToken(savedToken.getUser());
            sendValidationEmail(savedToken.getUser(), newToken);
            throw new TokenExpiredException("The activation token has expired. A new token has been sent to the same email address.");
        } else if (Objects.nonNull(savedToken.getValidatedAt())) {
            throw new TokenAlreadyValidatedException("The activation token is already validated.");
        }

        final var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    /**
     * Generates and saves an activation token
     *
     * @param user the user
     * @return the generated token
     */
    private String generateAndSaveActivationToken(User user) {
        // Generate a token
        String generatedToken = generateActivationCode(activationCodeLength);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(activationCodeMinutesValidity))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    /**
     * Sends a validation email
     *
     * @param user  the user
     * @param token the token
     * @throws MessagingException if an error occurs while sending the email
     */
    private void sendValidationEmail(User user, String token) throws MessagingException {
        if (emailValidationEnabled) {
            logger.info("Sending activation email to {} for user {}", user.getEmail(), user.getId());
            emailService.sendEmail(
                    user.getEmail(),
                    String.format("%s %s", user.getFirstname(), user.getLastname()),
                    EmailTemplate.ACTIVATE_ACCOUNT,
                    String.format("%s?activation-code=%s", activationUrl, token),
                    "Account activation"
            );
        }
    }

    /**
     * Generates an activation code
     *
     * @param length the length of the code
     * @return the generated code
     */
    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}