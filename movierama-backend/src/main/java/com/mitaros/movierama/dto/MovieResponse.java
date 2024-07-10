package com.mitaros.movierama.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate publicationDate;
    private LocalDate createdDate;
    private List<VoteResponse> votes;
    private Integer likesCount;
    private Integer hatesCount;
    private UserResponse user;
    private byte[] cover;

}