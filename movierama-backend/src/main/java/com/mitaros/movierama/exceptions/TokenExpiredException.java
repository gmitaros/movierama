package com.mitaros.movierama.exceptions;

import lombok.Generated;

@Generated
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}