package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.UserDto;
import com.example.notesTogether.entities.User;
import com.example.notesTogether.mappers.NoteMapper;
import com.example.notesTogether.mappers.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapperImpl implements UserMapper {
    private final NoteMapper noteMapper;

    public UserMapperImpl(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    @Override
    public User fromDto(UserDto userDto) {
        return new User(
                userDto.id(),
                userDto.email(),
                Optional.ofNullable(userDto.notes())
                                .map(notes -> notes.stream()
                                        .map(noteMapper::fromDto)
                                        .toList()
                                ).orElse(null)
        );
    }

    @Override
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                Optional.ofNullable(user.getNotes())
                                .map(notes -> notes.stream()
                                        .map(noteMapper::toDto)
                                        .toList()
                                ).orElse(null),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
