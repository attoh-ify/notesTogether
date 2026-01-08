package com.example.notesTogether.controllers;

import com.example.notesTogether.dto.LoginDto;
import com.example.notesTogether.dto.LoginResponseDto;
import com.example.notesTogether.dto.ResponseDto;
import com.example.notesTogether.mappers.UserMapper;
import com.example.notesTogether.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "Users",
        description = "User registration, authentication, and profile management"
)
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user using email and password and returns an access token"
    )
    public ResponseDto loginUser(
            @RequestBody LoginDto dto
    ) {
        String token = userService.loginUser(dto);
        return new ResponseDto(
                "User logged in",
                new LoginResponseDto(token)
        );
    }

    @GetMapping("/test")
    @Operation(
            summary = "Test route"
    )
    public ResponseDto testRoute() {
        return new ResponseDto(
                "Success",
                "Success"
        );
    }
}
