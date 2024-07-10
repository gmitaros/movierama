package com.mitaros.movierama.controller;

import com.mitaros.movierama.dto.UserResponse;
import com.mitaros.movierama.dto.VoteResponse;
import com.mitaros.movierama.service.UserService;
import com.mitaros.movierama.service.VoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class UserController {

    private final UserService userService;
    private final VoteService voteService;

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> userInfo(Authentication connectedUser) {
        return ResponseEntity.ok(userService.getUserInfo(connectedUser));
    }

    @GetMapping("/votes")
    public ResponseEntity<List<VoteResponse>> getUserVotes(Authentication connectedUser) {
        return ResponseEntity.ok(voteService.getUserVotes(connectedUser));
    }

}