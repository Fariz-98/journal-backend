package com.journal.journalbackend.journal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.journalbackend.comment.Comment;
import com.journal.journalbackend.journal.payload.JournalRequest;
import com.journal.journalbackend.journal.payload.JournalWithCommentsResponse;
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
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JournalController.class)
class JournalControllerTest {

    @MockBean
    private JournalService journalService;

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
    void getAllJournal() throws Exception {
        // given
        Journal testFirst = new Journal(
                1L,
                "author1",
                "title1",
                "content1",
                LocalDateTime.now()
        );
        Journal testSecond = new Journal(
                2L,
                "author2",
                "title2",
                "content2",
                LocalDateTime.now()
        );
        List<Journal> testJournalList = List.of(
                testFirst,
                testSecond
        );
        given(journalService.getAllJournal()).willReturn(testJournalList);

        // when
        // then
        mockMvc.perform(get("/api/journal"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].id").value(testFirst.getId()))
                .andExpect(jsonPath("$[0].author").value(testFirst.getAuthor()))
                .andExpect(jsonPath("$[0].title").value(testFirst.getTitle()))
                .andExpect(jsonPath("$[0].content").value(testFirst.getContent()))
                .andExpect(jsonPath("$[1].id").value(testSecond.getId()))
                .andExpect(jsonPath("$[1].author").value(testSecond.getAuthor()))
                .andExpect(jsonPath("$[1].title").value(testSecond.getTitle()))
                .andExpect(jsonPath("$[1].content").value(testSecond.getContent()));
    }

    @Test
    @WithMockUser(authorities = "USER")
    void addNewJournalEntry() throws Exception {
        // given
        JournalRequest testJournalRequest = new JournalRequest(
                "title",
                "content"
        );
        Long testId = 1L;
        given(journalService.addNewJournalEntry(any(JournalRequest.class), any(String.class))).willReturn(testId);

        // when
        // then
        mockMvc.perform(post("/api/journal/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testJournalRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/api/journal/")));
    }

    @Test
    void getJournalWithComments() throws Exception {
        // given
        Comment testFirstComment = new Comment(
                1L,
                "firstAuthor",
                "firstContent",
                LocalDateTime.now()
        );
        Comment testSecondComment = new Comment(
                2L,
                "secondAuthor",
                "secondContent",
                LocalDateTime.now()
        );
        JournalWithCommentsResponse testJournalWithCommentResponse = new JournalWithCommentsResponse(
                1L,
                "author",
                "title",
                "content",
                LocalDateTime.now(),
                List.of(
                        testFirstComment,
                        testSecondComment
                )
        );
        given(journalService.getJournalWithComments(1L)).willReturn(testJournalWithCommentResponse);

        // when
        // then
        mockMvc.perform(get("/api/journal/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(testJournalWithCommentResponse.getId()))
                .andExpect(jsonPath("$.author").value(testJournalWithCommentResponse.getAuthor()))
                .andExpect(jsonPath("$.title").value(testJournalWithCommentResponse.getTitle()))
                .andExpect(jsonPath("$.comments[0].id").value(testFirstComment.getId()))
                .andExpect(jsonPath("$.comments[0].author").value(testFirstComment.getAuthor()))
                .andExpect(jsonPath("$.comments[0].content").value(testFirstComment.getContent()))
                .andExpect(jsonPath("$.comments[1].id").value(testSecondComment.getId()))
                .andExpect(jsonPath("$.comments[1].author").value(testSecondComment.getAuthor()))
                .andExpect(jsonPath("$.comments[1].content").value(testSecondComment.getContent()));

    }

    @Test
    @WithMockUser(username = "username")
    void deleteJournal() throws Exception {
        mockMvc.perform(delete("/api/journal/1"))
                .andExpect(status().isNoContent());
    }

}