package com.mitaros.movierama.repository;

import com.mitaros.movierama.domain.MovieView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface MovieViewRepository extends JpaRepository<MovieView, Long>, JpaSpecificationExecutor<MovieView> {


}
