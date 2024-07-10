package com.mitaros.movierama.controller;

import com.mitaros.movierama.dto.VoteRequest;
import com.mitaros.movierama.dto.VoteResponse;
import com.mitaros.movierama.service.VoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
@Tag(name = "Vote")
public class VoteController {

    private final VoteService service;

    @PutMapping("/movie/{movieId}")
    public ResponseEntity<VoteResponse> vote(@PathVariable("movieId") Long movieId,
                                             @Valid @RequestBody VoteRequest request,
                                             Authentication connectedUser) {
        return ResponseEntity.ok(service.findMovieAndAddVote(movieId, request, connectedUser));
    }


}