package com.mitaros.movierama.utils;

import com.mitaros.movierama.domain.MovieView;
import org.springframework.data.jpa.domain.Specification;


public class MovieSpecification {

    public static Specification<MovieView> withOwnerId(Long ownerId) {
        if (ownerId != null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), ownerId);
        }
        return null;
    }

    public static Specification<MovieView> withTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isEmpty()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Always true = no filtering
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }
}