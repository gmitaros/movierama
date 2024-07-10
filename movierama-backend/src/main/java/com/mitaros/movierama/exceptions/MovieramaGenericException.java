package com.mitaros.movierama.exceptions;

import com.mitaros.movierama.dto.enums.BusinessErrorCodes;
import lombok.Generated;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@Generated
public class MovieramaGenericException extends RuntimeException {

    private HttpStatusCode httpStatusCode;
    private int code;
    private String description;

    public MovieramaGenericException(String message) {
        super(message);
    }

    public MovieramaGenericException(int code, HttpStatus status, String description) {
        super(description);
        this.code = code;
        this.httpStatusCode = status;
        this.description = description;
    }

    public MovieramaGenericException(BusinessErrorCodes error) {
        super(error.getDescription());
        this.code = error.getCode();
        this.httpStatusCode = error.getHttpStatus();
        this.description = error.getDescription();
    }
}