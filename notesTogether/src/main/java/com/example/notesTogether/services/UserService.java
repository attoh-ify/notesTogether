package com.example.notesTogether.services;

import com.example.notesTogether.dto.user.LoginDto;
import com.example.notesTogether.dto.user.UserDto;

public interface UserService {
    UserDto registerUser(UserDto user);
    UserDto getUserDetails(String email);
    String loginUser(LoginDto user);
}
