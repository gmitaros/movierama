package com.mitaros.movierama.utils;

import com.mitaros.movierama.domain.Movie;
import org.springframework.data.jpa.domain.Specification;


public class MovieViewSpecification {

    public static Specification<Movie> withOwnerId(Long ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}