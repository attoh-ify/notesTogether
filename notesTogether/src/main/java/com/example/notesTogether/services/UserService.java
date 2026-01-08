package com.example.notesTogether.services;

import com.example.notesTogether.dto.LoginDto;

public interface UserService {
    String loginUser(LoginDto user);
    void logoutUser(LoginDto user);
}
