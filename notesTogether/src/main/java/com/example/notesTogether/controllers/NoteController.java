package com.example.notesTogether.controllers;

import com.example.notesTogether.dto.ResponseDto;
import com.example.notesTogether.dto.note.NoteDto;
import com.example.notesTogether.dto.note.NotePayloadDto;
import com.example.notesTogether.entities.NoteVisibility;
import com.example.notesTogether.entities.UserPrincipal;
import com.example.notesTogether.security.CurrentUser;
import com.example.notesTogether.services.NoteService;
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
        description = "Manage Notes"
)
public class NoteController {
    private final NoteService noteService;

    public NoteController(
            NoteService noteService
    ) {
        this.noteService = noteService;
    }

    @GetMapping
    @Operation(summary = "Fetch all notes", description = "Retrieves all notes accessible to the user")
    public ResponseDto getAllNotes(
            @CurrentUser UserPrincipal currentUser
    ) {
        List<NoteDto> notes = noteService.fetchNotes(currentUser.getEmail());
        return new ResponseDto("Notes fetched", notes);
    }

    @GetMapping("/{noteId}")
    @Operation(summary = "Fetch a single note", description = "Retrieves a specific note by ID")
    public ResponseDto getNote(
            @CurrentUser UserPrincipal currentUser,

            @Parameter(description = "Unique identifier of the note", required = true)
            @PathVariable UUID noteId
    ) {
        NoteDto note = noteService.fetchNote(currentUser.getEmail(), noteId);
        return new ResponseDto("Note fetched", note);
    }

    @PostMapping
    @Operation(summary = "Create a new note", description = "Create a new note for user")
    public ResponseDto createNote(
            @CurrentUser UserPrincipal currentUser,

            @Parameter(description = "Note object details")
            @RequestBody NotePayloadDto payloadDto
    ) {
        NoteDto note = noteService.createNote(currentUser.getEmail(), payloadDto);
        return new ResponseDto("Note created", note);
    }

    @DeleteMapping("/{noteId}")
    @Operation(summary = "Delete a note", description = "Deletes a note by ID")
    public ResponseDto deleteNote(
            @CurrentUser UserPrincipal currentUser,

            @Parameter(description = "Unique identifier of the note", required = true)
            @PathVariable UUID noteId
    ) {
        noteService.deleteNote(currentUser.getEmail(), noteId);
        return new ResponseDto("Note deleted", null);
    }

    @PutMapping("/{noteId}/visibility")
    @Operation(summary = "Change note visibility", description = "Changes the visibility of a note (PUBLIC/PRIVATE)")
    public ResponseDto changeVisibility(
            @CurrentUser UserPrincipal currentUser,

            @Parameter(description = "Unique identifier of the note", required = true)
            @PathVariable UUID noteId,

            @Parameter(description = "New visibility status", required = true)
            @RequestParam NoteVisibility visibility
    ) {
        noteService.changeNoteVisibility(currentUser.getEmail(), noteId, visibility);
        return new ResponseDto("Note visibility updated", visibility);
    }
}
