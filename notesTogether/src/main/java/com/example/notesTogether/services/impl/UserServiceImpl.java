package com.example.notesTogether.services.impl;

import com.example.notesTogether.dto.LoginDto;
import com.example.notesTogether.entities.User;
import com.example.notesTogether.repositories.UserRepository;
import com.example.notesTogether.services.JwtService;
import com.example.notesTogether.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (userRepository.findByEmail(user.email()).isEmpty()) {
            log.info("Registering user email={}", user.email());

            validateUser(user);
            User saved = userRepository.save(
                    new User(
                            null,
                            user.email(),
                            null
                    )
            );

            log.info("User registered successfully userId={} email={}",
                    saved.getId(), saved.getEmail());

        }

        return jwtService.generateToken(user.email());
    }

    private void validateUser(LoginDto user) {
    }

    @Override
    public void logoutUser(LoginDto user) {

    }
}
