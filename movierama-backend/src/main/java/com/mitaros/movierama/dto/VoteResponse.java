package com.mitaros.movierama.dto;

import com.mitaros.movierama.dto.enums.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteResponse {

    private Long id;
    private VoteType type;
    private Long movieId;
    private Long userId;

}