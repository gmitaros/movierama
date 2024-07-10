package com.mitaros.movierama.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class MovieRequest {

    @NotNull(message = "Title cannot be null")
    @NotEmpty(message = "Title cannot be empty")
    private final String title;

    @NotNull(message = "Description cannot be null")
    @NotEmpty(message = "Description cannot be empty")
    private final String description;

    @NotNull(message = "Publication date cannot be null")
    private final LocalDate publicationDate;
}