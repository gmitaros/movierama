package com.mitaros.movierama.repository;

import com.mitaros.movierama.domain.Movie;
import com.mitaros.movierama.domain.User;
import com.mitaros.movierama.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Vote findByMovieAndUser(Movie movie, User user);

    List<Vote> findAllByUser(User user);
}
