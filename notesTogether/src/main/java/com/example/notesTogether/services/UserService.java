package com.example.notesTogether.services;

import com.example.notesTogether.dto.user.LoginDto;

public interface UserService {
    String loginUser(LoginDto user);
    void logoutUser();
}
