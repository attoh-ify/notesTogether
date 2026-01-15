package com.example.notesTogether.services.impl;

import com.example.notesTogether.entities.User;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserPolicyService {
    private final UserRepository userRepository;

    private static final Logger log =
            LoggerFactory.getLogger(UserPolicyService.class);

    public UserPolicyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User userExists(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.warn("User not found with email={}", userEmail);
                    return new BadRequestException("User not found with email");
                });
    }
}
