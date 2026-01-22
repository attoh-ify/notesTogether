package com.example.notesTogether.controllers;

import com.example.notesTogether.dto.ResponseDto;
import com.example.notesTogether.dto.noteVersion.NoteVersionDto;
import com.example.notesTogether.entities.UserPrincipal;
import com.example.notesTogether.security.CurrentUser;
import com.example.notesTogether.services.NoteVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes/{noteId}/versions")
@Tag(
        name = "Note Versions",
        description = "Manage Note Versions"
)
public class NoteVersionController {
    private final NoteVersionService noteVersionService;

    public NoteVersionController(
            NoteVersionService noteVersionService
    ) {
        this.noteVersionService = noteVersionService;
    }

    @GetMapping
    @Operation(summary = "Fetch all note versions", description = "Retrieves all versions of a note")
    public ResponseDto getAllVersions(
            @CurrentUser UserPrincipal currentUser,

            @Parameter(description = "Unique identifier of the note", required = true)
            @PathVariable UUID noteId
    ) {
        List<NoteVersionDto> versions = noteVersionService.fetchAllVersions(currentUser.getEmail(), noteId);
        return new ResponseDto("Note versions fetched", versions);
    }

    @GetMapping("/{versionId}")
    @Operation(summary = "Fetch a specific note version", description = "Retrieves a specific version of a note")
    public ResponseDto getVersion(
            @CurrentUser UserPrincipal currentUser,

            @Parameter(description = "Unique identifier of the note", required = true)
            @PathVariable UUID noteId,

            @Parameter(description = "Unique identifier of the note version", required = true)
            @PathVariable UUID versionId
    ) {
        NoteVersionDto version = noteVersionService.fetchVersion(currentUser.getEmail(), noteId, versionId);
        return new ResponseDto("Note version fetched", version);
    }

    @PutMapping("/{versionId}/restore")
    @Operation(summary = "Restore a note version", description = "Restores a note to a previous version")
    public ResponseDto restoreVersion(
            @CurrentUser UserPrincipal currentUser,

            @Parameter(description = "Unique identifier of the note", required = true)
            @PathVariable UUID noteId,

            @Parameter(description = "Unique identifier of the note version", required = true)
            @PathVariable UUID versionId
    ) {
        NoteVersionDto restored = noteVersionService.restoreVersion(currentUser.getEmail(), noteId, versionId);
        return new ResponseDto("Note restored to version", restored);
    }
}
