package com.journal.journalbackend.comment;

import com.journal.journalbackend.comment.payload.CommentAndJournalResponse;
import com.journal.journalbackend.comment.payload.CommentRequest;
import com.journal.journalbackend.exceptions.ElementNotFoundException;
import com.journal.journalbackend.exceptions.NoPermissionException;
import com.journal.journalbackend.journal.Journal;
import com.journal.journalbackend.journal.JournalService;
import com.journal.journalbackend.user.AppUser;
import com.journal.journalbackend.user.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private JournalService journalService;

    @Mock
    private AppUserService appUserService;

    CommentService commentServiceUnderTest;

    @BeforeEach
    void setUp() {
        commentServiceUnderTest = new CommentService(commentRepository, journalService, appUserService);
    }

    @Test
    void addNewCommentSuccessful() {
        // given
        Long journalId = 1L;
        Journal testJournal = new Journal();
        testJournal.setId(journalId);

        AppUser testUser = new AppUser();

        CommentRequest testCommentRequest = new CommentRequest(
                "content"
        );
        String username = "username";
        Comment testComment = new Comment(
                username,
                testCommentRequest.getContent(),
                LocalDateTime.now(),
                testJournal,
                testUser
        );

        given(journalService.getJournalById(1L)).willReturn(testJournal);
        given(appUserService.loadUserByUsername(username)).willReturn(testUser);

        // when
        commentServiceUnderTest.addNewComment(1L, testCommentRequest, username);

        // then
        verify(journalService).getJournalById(1L);
        verify(appUserService).loadUserByUsername(username);

        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment capturedComment = commentArgumentCaptor.getValue();
        assertThat(capturedComment).usingRecursiveComparison().ignoringFields("dateCreated").isEqualTo(testComment);
    }

    @Test
    void getCommentSuccessful() {
        // given
        Long journalId = 1L;
        Journal testJournal = new Journal();
        testJournal.setId(journalId);

        Long commentId = 1L;
        Comment testComment = new Comment();
        testComment.setId(commentId);
        testComment.setJournal(testJournal);

        CommentAndJournalResponse testCommentAndJournalResponse = new CommentAndJournalResponse(
                testComment.getJournal(),
                testComment
        );

        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));

        // when
        CommentAndJournalResponse newCommentAndJournalResponse = commentServiceUnderTest.getComment(1L, 1L);

        // then
        assertThat(newCommentAndJournalResponse).usingRecursiveComparison().isEqualTo(testCommentAndJournalResponse);
        verify(commentRepository).findById(1L);
    }

    @Test
    void getCommentWillThrowExceptionWhenCommentIsNotFound() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> commentServiceUnderTest.getComment(1L, 1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("Comment not found");
        verify(commentRepository).findById(1L);
    }

    @Test
    void getCommentWillThrowExceptionWhenJournalIdIsWrong() {
        // given
        Long commentId = 1L;
        Comment testComment = new Comment();
        testComment.setId(commentId);
        testComment.setJournal(new Journal());
        testComment.getJournal().setId(2L);

        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));

        // when
        // then
        assertThatThrownBy(() -> commentServiceUnderTest.getComment(1L, 1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("Wrong journal");
        verify(commentRepository).findById(1L);
    }

    @Test
    void deleteCommentSuccessfulForCommentAuthor() {
        // given
        String username = "username";
        Comment testComment = new Comment();
        testComment.setId(1L);
        testComment.setAuthor(username);

        List<SimpleGrantedAuthority> testGrantedAuth = new ArrayList<>();
        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));

        // when
        commentServiceUnderTest.deleteComment(1L, username, testGrantedAuth);

        // then
        verify(commentRepository).delete(testComment);
    }

    @Test
    void deleteCommentSuccessfulForUserWithCommentWritePermission() {
        // given
        Comment testComment = new Comment();
        testComment.setId(1L);
        testComment.setAuthor("testCommentAuthor");

        List<SimpleGrantedAuthority> testGrantedAuth = List.of(
                new SimpleGrantedAuthority("comment:write")
        );
        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));

        // when
        commentServiceUnderTest.deleteComment(1L, "userWithCommentWrite", testGrantedAuth);

        // then
        verify(commentRepository).delete(testComment);
    }

    @Test
    void deleteCommentWillThrowExceptionWhenCommentIsNotFound() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> commentServiceUnderTest.deleteComment(1L, "username", new ArrayList<SimpleGrantedAuthority>()))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("Comment not found");
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteCommentWillThrowExceptionWhenNotAuthorOrNotEnoughPermission() {
        // given
        Comment testComment = new Comment();
        testComment.setAuthor("testAuthor");
        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));

        // when
        // then
        assertThatThrownBy(() -> commentServiceUnderTest.deleteComment(1L, "username", new ArrayList<SimpleGrantedAuthority>()))
                .isInstanceOf(NoPermissionException.class)
                .hasMessageContaining("You are not authorized to delete this comment");
        verify(commentRepository, never()).delete(any());
    }

}





















