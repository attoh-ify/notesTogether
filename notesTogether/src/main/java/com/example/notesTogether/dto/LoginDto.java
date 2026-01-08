package com.example.notesTogether.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for user login")
public record LoginDto(
        @Schema(description = "User's email address", example = "user@example.com")
        String email
) {}