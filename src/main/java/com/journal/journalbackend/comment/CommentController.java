package com.journal.journalbackend.comment;

import com.journal.journalbackend.comment.payload.CommentAndJournalResponse;
import com.journal.journalbackend.comment.payload.CommentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/journal/{journalId}/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    private ResponseEntity<?> addNewComment(@PathVariable Long journalId, @RequestBody CommentRequest commentRequest, @AuthenticationPrincipal String username) {
        Long id = commentService.addNewComment(journalId, commentRequest, username);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping(path = "{commentId}")
    private CommentAndJournalResponse getComment(@PathVariable Long journalId, @PathVariable Long commentId) {
        return commentService.getComment(journalId, commentId);
    }

    @DeleteMapping(path = "{commentId}")
    private ResponseEntity<?> deleteComment(@PathVariable Long commentId, Authentication auth) {
        commentService.deleteComment(commentId, auth.getName(), auth.getAuthorities());
        return ResponseEntity.noContent().build();
    }

}
