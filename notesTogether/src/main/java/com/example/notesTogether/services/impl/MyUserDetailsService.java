package com.example.notesTogether.services.impl;

import com.example.notesTogether.entities.User;
import com.example.notesTogether.entities.UserPrincipal;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    private static final Logger log =
            LoggerFactory.getLogger(MyUserDetailsService.class);

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("MyUserDetailsService initialized");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found during authentication email={}", email);
                    return new BadRequestException("User not found");
                });

        log.debug("User loaded successfully email={} userId={}",
                user.getEmail(), user.getId());

        return new UserPrincipal(user);
    }
}
