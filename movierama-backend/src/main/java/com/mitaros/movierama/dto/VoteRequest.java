package com.mitaros.movierama.dto;

import com.mitaros.movierama.dto.enums.VoteType;
import jakarta.validation.constraints.NotNull;

public record VoteRequest(
        @NotNull(message = "Vote type cannot be null")
        VoteType voteType
) {
}