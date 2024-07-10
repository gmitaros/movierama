package com.mitaros.movierama.repository;

import com.mitaros.movierama.domain.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    @Query("""
            SELECT movie
            FROM Movie movie
            WHERE movie.user.id = :userId
            """)
    Page<Movie> findAllMovieByUser(Pageable pageable, Integer userId);

}
