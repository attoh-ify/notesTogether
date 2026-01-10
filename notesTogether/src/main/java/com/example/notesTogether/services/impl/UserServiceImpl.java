package com.example.notesTogether.services.impl;

import com.example.notesTogether.dto.user.LoginDto;
import com.example.notesTogether.entities.User;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.repositories.UserRepository;
import com.example.notesTogether.services.JwtService;
import com.example.notesTogether.services.UserService;
import com.example.notesTogether.utils.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public String loginUser(LoginDto user) {
        log.info("Login attempt email={}", user.email());

        Optional<User> userExists = userRepository.findByEmail(user.email());
        UUID userId;

        if (userExists.isEmpty()) {
            log.info("Registering user email={}", user.email());

            if (Helpers.isBlank(user.email()))
                throw new BadRequestException("Email required");

            User saved = userRepository.save(
                    new User(
                            null,
                            user.email(),
                            null
                    )
            );
            userId = saved.getId();

            log.info("User registered successfully userId={} email={}",
                    saved.getId(), saved.getEmail());

        }
        userId = userExists.get().getId();

        return jwtService.generateToken(user.email(), userId);
    }

    @Override
    public void logoutUser() {

    }
}
