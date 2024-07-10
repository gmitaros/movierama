package com.mitaros.movierama.dto.enums;

import lombok.Getter;

@Getter
public enum MovieSortField {
    CREATED("createdDate"),
    PUBLISHED("publicationDate"),
    LIKES("likesCount"),
    HATES("hatesCount");

    private final String value;

    MovieSortField(String value) {
        this.value = value;
    }

}
