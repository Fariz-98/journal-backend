package com.journal.journalbackend.journal;

import com.journal.journalbackend.journal.payload.JournalRequest;
import com.journal.journalbackend.journal.payload.JournalWithCommentsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/journal")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping
    public List<Journal> getAllJournal() {
        return journalService.getAllJournal();
    }

    @PostMapping
    public ResponseEntity<?> addNewJournalEntry(@RequestBody JournalRequest journalRequest, @AuthenticationPrincipal String username) {
        Long id = journalService.addNewJournalEntry(journalRequest, username);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping(path = "{journalId}")
    public JournalWithCommentsResponse getJournalWithComments(@PathVariable Long journalId) {
        return journalService.getJournalWithComments(journalId);
    }

    @DeleteMapping(path = "{journalId}")
    public ResponseEntity<?> deleteJournal(@PathVariable Long journalId, Authentication auth) {
        journalService.deleteJournal(journalId, auth.getName(), auth.getAuthorities());
        return ResponseEntity.noContent().build();
    }

}
