package com.journal.journalbackend.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.journalbackend.comment.payload.CommentAndJournalResponse;
import com.journal.journalbackend.comment.payload.CommentRequest;
import com.journal.journalbackend.journal.Journal;
import com.journal.journalbackend.jwt.configuration.JwtConfig;
import com.journal.journalbackend.jwt.configuration.JwtSecretKey;
import com.journal.journalbackend.user.AppUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @MockBean
    CommentService commentService;

    @MockBean
    private AppUserService appUserService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtConfig jwtConfig;
    @MockBean
    private JwtSecretKey jwtSecretKey;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "USER")
    void addNewComment() throws Exception {
        // given
        CommentRequest testCommentRequest = new CommentRequest(
                "content"
        );

        // when
        // then
        mockMvc.perform(post("/api/journal/{journalId}/comment", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCommentRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/api/journal/1/comment/")));
    }

    @Test
    void getComment() throws Exception {
        // given
        Journal testJournal = new Journal(
                1L,
                "author",
                "title",
                "content",
                LocalDateTime.now()
        );
        Comment testComment = new Comment(
                1L,
                "author",
                "content",
                LocalDateTime.now()
        );
        CommentAndJournalResponse testCommentAndJournalResponse = new CommentAndJournalResponse(
                testJournal,
                testComment
        );
        given(commentService.getComment(1L, 1L)).willReturn(testCommentAndJournalResponse);

        // when
        // then
        mockMvc.perform(get("/api/journal/1/comment/1"))
                .andExpect(jsonPath("$.journal.id").value(testJournal.getId()))
                .andExpect(jsonPath("$.journal.author").value(testJournal.getAuthor()))
                .andExpect(jsonPath("$.journal.title").value(testJournal.getTitle()))
                .andExpect(jsonPath("$.journal.content").value(testJournal.getContent()))
                .andExpect(jsonPath("$.comment.id").value(testComment.getId()))
                .andExpect(jsonPath("$.comment.author").value(testComment.getAuthor()))
                .andExpect(jsonPath("$.comment.content").value(testComment.getContent()));
    }

    @Test
    @WithMockUser
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/api/journal/1/comment/1"))
                .andExpect(status().isNoContent());
    }

}