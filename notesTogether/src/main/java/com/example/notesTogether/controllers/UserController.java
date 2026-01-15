package com.example.notesTogether.controllers;

import com.example.notesTogether.dto.user.LoginDto;
import com.example.notesTogether.dto.user.LoginResponseDto;
import com.example.notesTogether.dto.ResponseDto;
import com.example.notesTogether.dto.user.UserDto;
import com.example.notesTogether.entities.UserPrincipal;
import com.example.notesTogether.security.CurrentUser;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided registration details"
    )
    public ResponseDto registerUser(
            @RequestBody UserDto dto
    ) {
        return new ResponseDto("User registered", userService.registerUser(dto));
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

    @GetMapping
    @Operation(
            summary = "Get user profile",
            description = "Retrieves user profile information using the user's email address"
    )
    public ResponseDto getDetails(
            @CurrentUser UserPrincipal currentUser
    ) {
        return new ResponseDto("User fetched", userService.getUserDetails(currentUser.getUsername()));
    }
}
