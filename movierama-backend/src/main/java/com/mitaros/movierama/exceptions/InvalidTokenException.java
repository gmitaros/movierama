package com.mitaros.movierama.exceptions;

import lombok.Generated;

@Generated
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}