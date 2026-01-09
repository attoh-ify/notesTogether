package com.example.notesTogether.mappers;

import com.example.notesTogether.dto.user.UserDto;
import com.example.notesTogether.entities.User;

public interface UserMapper {
    User fromDto(UserDto userDto);
    UserDto toDto(User user);
}
