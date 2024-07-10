package com.mitaros.movierama.controller;

import com.mitaros.movierama.dto.MovieRequest;
import com.mitaros.movierama.dto.MovieResponse;
import com.mitaros.movierama.dto.VoteRequest;
import com.mitaros.movierama.dto.enums.MovieSortField;
import com.mitaros.movierama.service.MovieService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Movie")
public class MovieController {

    private final MovieService service;

    @PostMapping("/movies")
    public ResponseEntity<MovieResponse> saveMovie(@Valid @RequestBody MovieRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @PutMapping("/movies/{movieId}")
    public ResponseEntity<MovieResponse> editMovie(@PathVariable("movieId") Long movieId,
                                                   @Valid @RequestBody MovieRequest request,
                                                   Authentication connectedUser) {
        return ResponseEntity.ok(service.editMovie(movieId, request, connectedUser));
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<MovieResponse> findMovieById(@PathVariable("movieId") Long movieId) {
        return ResponseEntity.ok(service.findMovieView(movieId));
    }

    @GetMapping("/public/movies")
    public ResponseEntity<Page<MovieResponse>> findAllMovies(@RequestParam(name = "page", defaultValue = "0", required = false) int page,
                                                             @RequestParam(name = "size", defaultValue = "10", required = false) int size,
                                                             @RequestParam(name = "user", required = false) Long userId,
                                                             @RequestParam(name = "sortField", defaultValue = "CREATED", required = false) MovieSortField sortField,
                                                             @RequestParam(name = "sortType", defaultValue = "DESC") Sort.Direction sortType,
                                                             @RequestParam(name = "title", required = false) String title) {
        return ResponseEntity.ok(service.findAllMovies(page, size, sortField, sortType, userId, title));
    }

    @GetMapping("/public/movies/owner/{ownerId}")
    public ResponseEntity<Page<MovieResponse>> findAllMoviesByOwner(
            @PathVariable("ownerId") Long ownerId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "sortField", defaultValue = "CREATED", required = false) MovieSortField sortField,
            @RequestParam(name = "sortType", defaultValue = "ASC") Sort.Direction sortType
    ) {
        return ResponseEntity.ok(service.findAllMoviesByOwner(ownerId, page, size, sortField, sortType));
    }

    @PostMapping("/movies/{movieId}/vote")
    public ResponseEntity<MovieResponse> voteMovie(@PathVariable("movieId") Long movieId,
                                                   @Valid @RequestBody VoteRequest request,
                                                   Authentication connectedUser) {
        return ResponseEntity.ok(service.voteMovie(movieId, request, connectedUser));
    }
}