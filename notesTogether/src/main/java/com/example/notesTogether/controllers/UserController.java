package com.example.notesTogether.controllers;

import com.example.notesTogether.dto.user.LoginDto;
import com.example.notesTogether.dto.user.LoginResponseDto;
import com.example.notesTogether.dto.ResponseDto;
import com.example.notesTogether.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user using email and password and returns an access token"
    )
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseDto loginUser(
            @RequestBody LoginDto dto
    ) {
        String token = userService.loginUser(dto);
        return new ResponseDto(
                "User logged in",
                new LoginResponseDto(token)
        );
    }

    @GetMapping("/logout")
    @Operation(
            summary = "Logout user",
            description = "Logs out a user using their access token"
    )
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseDto logoutUser(
            @RequestBody LoginDto dto
    ) {
        userService.logoutUser();
        return new ResponseDto(
                "Success"
        );
    }
}
