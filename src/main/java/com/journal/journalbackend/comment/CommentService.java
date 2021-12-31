package com.journal.journalbackend.comment;

import com.journal.journalbackend.comment.payload.CommentAndJournalResponse;
import com.journal.journalbackend.exceptions.ElementNotFoundException;
import com.journal.journalbackend.exceptions.NoPermissionException;
import com.journal.journalbackend.journal.Journal;
import com.journal.journalbackend.journal.JournalService;
import com.journal.journalbackend.comment.payload.CommentRequest;
import com.journal.journalbackend.user.AppUser;
import com.journal.journalbackend.user.AppUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final JournalService journalService;
    private final AppUserService appUserService;

    public CommentService(CommentRepository commentRepository, JournalService journalService, AppUserService appUserService) {
        this.commentRepository = commentRepository;
        this.journalService = journalService;
        this.appUserService = appUserService;
    }

    public Long addNewComment(Long journalId, CommentRequest commentRequest, String username) {
        Journal journal = journalService.getJournalById(journalId);
        AppUser appUser = (AppUser) appUserService.loadUserByUsername(username);

        Comment comment = new Comment(
                username,
                commentRequest.getContent(),
                LocalDateTime.now(),
                journal,
                appUser
        );

        commentRepository.save(comment);

        return comment.getId();
    }

    public CommentAndJournalResponse getComment(Long journalId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            throw new ElementNotFoundException("Comment not found");
        }

        Journal journal = comment.getJournal();
        if (!Objects.equals(journal.getId(), journalId)) {
            throw new ElementNotFoundException("Wrong journal");
        }

        return new CommentAndJournalResponse(
                journal,
                comment
        );
    }

    public void deleteComment(Long commentId, String username, Collection<? extends GrantedAuthority> authorities) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new ElementNotFoundException("Comment not found");
        }

        if (comment.getAuthor().equals(username) || authorities.contains(new SimpleGrantedAuthority("comment:write")) ||
        authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            commentRepository.delete(comment);
        } else {
            throw new NoPermissionException("You are not authorized to delete this comment");
        }
    }

}
