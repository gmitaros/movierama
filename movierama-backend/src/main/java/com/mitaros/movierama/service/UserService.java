package com.mitaros.movierama.service;

import com.mitaros.movierama.dto.UserResponse;
import com.mitaros.movierama.repository.VoteRepository;
import com.mitaros.movierama.security.MovieramaUserDetails;
import com.mitaros.movierama.utils.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    /**
     * Get the user information
     *
     * @param connectedUser the connected user
     * @return the user information
     */
    public UserResponse getUserInfo(Authentication connectedUser) {
        MovieramaUserDetails user = ((MovieramaUserDetails) connectedUser.getPrincipal());
        return MapperUtil.toUserResponse(user.getUser());
    }

}