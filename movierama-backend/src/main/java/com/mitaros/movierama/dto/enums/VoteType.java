package com.mitaros.movierama.dto.enums;

public enum VoteType {
    LIKE("LIKE"),
    HATE("HATE");

    private final String value;

    VoteType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
