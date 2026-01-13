package com.example.notesTogether.controllers;

import com.example.notesTogether.dto.ResponseDto;
import com.example.notesTogether.dto.note.NoteDto;
import com.example.notesTogether.dto.noteAccess.AddNoteAccessDto;
import com.example.notesTogether.dto.noteAccess.DeleteNoteAccessDto;
import com.example.notesTogether.dto.noteAccess.NoteAccessDto;
import com.example.notesTogether.entities.NoteVisibility;
import com.example.notesTogether.entities.NoteVersion;
import com.example.notesTogether.services.NoteAccessService;
import com.example.notesTogether.services.NoteService;
import com.example.notesTogether.services.NoteVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
@Tag(
        name = "Notes",
        description = "Manage Notes, Note Access and Note Versions"
)
public class NoteController {
    private final NoteService noteService;
    private final NoteAccessService noteAccessService;
    private final NoteVersionService noteVersionService;

    public NoteController(
            NoteService noteService,
            NoteAccessService noteAccessService,
            NoteVersionService noteVersionService
    ) {
        this.noteService = noteService;
        this.noteAccessService = noteAccessService;
        this.noteVersionService = noteVersionService;
    }

    // ------------------- NOTES -------------------

    @GetMapping
    @Operation(summary = "Fetch all notes", description = "Retrieves all notes accessible to the user")
    public ResponseDto getAllNotes(
            @Parameter(description = "Email of the requesting user", example = "alice@example.com")
            @RequestParam String userEmail
    ) {
        List<NoteDto> notes = noteService.fetchNotes(userEmail);
        return new ResponseDto("Notes fetched", notes);
    }

    @GetMapping("/{noteId}")
    @Operation(summary = "Fetch a single note", description = "Retrieves a specific note by ID")
    public ResponseDto getNote(
            @Parameter(description = "Email of the requesting user", example = "alice@example.com")
            @RequestParam String userEmail,

            @Parameter(description = "Unique identifier of the note", required = true)
            @PathVariable UUID noteId
    ) {
        NoteDto note = noteService.fetchNote(userEmail, noteId);
        return new ResponseDto("Note fetched", note);
    }

    @DeleteMapping("/{noteId}")
    @Operation(summary = "Delete a note", description = "Deletes a note by ID")
    public ResponseDto deleteNote(
            @Parameter(description = "Email of the requesting user", example = "alice@example.com")
            @RequestParam String userEmail,

            @Parameter(description = "Unique identifier of the note", required = true)
            @PathVariable UUID noteId
    ) {
        noteService.deleteNote(userEmail, noteId);
        return new ResponseDto("Note deleted", null);
    }

    @PutMapping("/{noteId}/visibility")
    @Operation(summary = "Change note visibility", description = "Changes the visibility of a note (PUBLIC/PRIVATE)")
    public ResponseDto changeVisibility(
            @Parameter(description = "Email of the note owner", example = "alice@example.com")
            @RequestParam String userEmail,

            @Parameter(description = "Unique identifier of the note", required = true)
            @PathVariable UUID noteId,

            @Parameter(description = "New visibility status", required = true)
            @RequestParam NoteVisibility visibility
    ) {
        noteService.changeNoteVisibility(userEmail, noteId, visibility);
        return new ResponseDto("Note visibility updated", visibility);
    }

    // ------------------- NOTE ACCESS -------------------

    @PostMapping("/{noteId}/access")
    @Operation(summary = "Add note access", description = "Grants access to a note to another user")
    public ResponseDto addAccess(
            @RequestParam String userEmail,
            @PathVariable UUID noteId,
            @RequestBody AddNoteAccessDto payload
    ) {
        NoteAccessDto access = noteAccessService.addAccess(userEmail, payload);
        return new ResponseDto("Access added", access);
    }

    @PutMapping("/{noteId}/access")
    @Operation(summary = "Update note access", description = "Updates access role for a note")
    public ResponseDto updateAccess(
            @RequestParam String userEmail,
            @RequestBody NoteAccessDto payload
    ) {
        NoteAccessDto access = noteAccessService.updateAccess(userEmail, payload);
        return new ResponseDto("Access updated", access);
    }

    @DeleteMapping("/{noteId}/access")
    @Operation(summary = "Delete note access", description = "Removes a user's access to a note")
    public ResponseDto deleteAccess(
            @RequestParam String userEmail,
            @RequestBody DeleteNoteAccessDto payload
    ) {
        noteAccessService.deleteAccess(userEmail, payload);
        return new ResponseDto("Access deleted", null);
    }

    @GetMapping("/{noteId}/access")
    @Operation(summary = "List note accesses", description = "Lists all users with access to a note")
    public ResponseDto getAccessList(
            @RequestParam String userEmail,
            @PathVariable UUID noteId
    ) {
        List<NoteAccessDto> accesses = noteAccessService.getAllAccess(userEmail, noteId);
        return new ResponseDto("Access list fetched", accesses);
    }

    // ------------------- NOTE VERSIONS -------------------

    @GetMapping("/{noteId}/versions")
    @Operation(summary = "Fetch all note versions", description = "Retrieves all versions of a note")
    public ResponseDto getAllVersions(
            @RequestParam String userEmail,
            @PathVariable UUID noteId
    ) {
        List<NoteVersion> versions = noteVersionService.fetchAllVersions(userEmail, noteId);
        return new ResponseDto("Note versions fetched", versions);
    }

    @GetMapping("/{noteId}/versions/{versionId}")
    @Operation(summary = "Fetch a specific note version", description = "Retrieves a specific version of a note")
    public ResponseDto getVersion(
            @RequestParam String userEmail,
            @PathVariable UUID noteId,
            @PathVariable UUID versionId
    ) {
        NoteVersion version = noteVersionService.fetchVersion(userEmail, noteId, versionId);
        return new ResponseDto("Note version fetched", version);
    }

    @PutMapping("/{noteId}/versions/{versionId}/restore")
    @Operation(summary = "Restore a note version", description = "Restores a note to a previous version")
    public ResponseDto restoreVersion(
            @RequestParam String userEmail,
            @PathVariable UUID noteId,
            @PathVariable UUID versionId
    ) {
        NoteVersion restored = noteVersionService.restoreVersion(userEmail, noteId, versionId);
        return new ResponseDto("Note restored to version", restored);
    }
}
