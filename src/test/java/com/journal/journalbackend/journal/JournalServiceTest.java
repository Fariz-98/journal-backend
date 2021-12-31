package com.journal.journalbackend.journal;

import com.journal.journalbackend.exceptions.ElementNotFoundException;
import com.journal.journalbackend.exceptions.NoPermissionException;
import com.journal.journalbackend.journal.payload.JournalRequest;
import com.journal.journalbackend.journal.payload.JournalWithCommentsResponse;
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

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    private JournalRepository journalRepository;

    @Mock
    private AppUserService appUserService;

    private JournalService journalServiceUnderTest;

    @BeforeEach
    void setUp() {
        journalServiceUnderTest = new JournalService(journalRepository, appUserService);
    }

    @Test
    void getAllJournalSuccessful() {
        // when
        journalServiceUnderTest.getAllJournal();

        // then
        verify(journalRepository).findAll();
    }

    @Test
    void addNewJournalEntrySuccessful() {
        // given
        JournalRequest testJournalRequest = new JournalRequest(
                "testTitle",
                "testContent"
        );

        String username = "username";
        AppUser testAppUser = new AppUser();
        testAppUser.setUsername(username);

        Journal testJournal = new Journal(
                username,
                testJournalRequest.getTitle(),
                testJournalRequest.getContent(),
                LocalDateTime.now(),
                testAppUser
        );

        given(appUserService.loadUserByUsername(username)).willReturn(testAppUser);

        // when
        journalServiceUnderTest.addNewJournalEntry(testJournalRequest, username);

        // then
        verify(appUserService).loadUserByUsername(username);

        ArgumentCaptor<Journal> journalArgumentCaptor = ArgumentCaptor.forClass(Journal.class);
        verify(journalRepository).save(journalArgumentCaptor.capture());
        Journal capturedJournal = journalArgumentCaptor.getValue();

        assertThat(capturedJournal).usingRecursiveComparison().ignoringFields("dateCreated").isEqualTo(testJournal);
    }

    @Test
    void getJournalWithCommentsSuccessful() {
        // given
        Long testJournalId = 1L;
        Journal testJournal = new Journal();
        testJournal.setId(testJournalId);
        testJournal.setAuthor("author");
        testJournal.setTitle("title");
        testJournal.setContent("content");
        testJournal.setDateCreated(LocalDateTime.now());
        testJournal.setComment(new ArrayList<>());

        JournalWithCommentsResponse testResponse = new JournalWithCommentsResponse(
                testJournal.getId(),
                testJournal.getAuthor(),
                testJournal.getTitle(),
                testJournal.getContent(),
                testJournal.getDateCreated(),
                testJournal.getComment()
        );
        given(journalRepository.findById(testJournalId)).willReturn(Optional.of(testJournal));

        // when
        JournalWithCommentsResponse response = journalServiceUnderTest.getJournalWithComments(testJournalId);

        // then
        assertThat(response).usingRecursiveComparison().ignoringFields("dateCreated").isEqualTo(testResponse);
    }

    @Test
    void getJournalWithCommentsWillThrowExceptionWhenJournalIsNotFound() {
        // given
        Long testJournalId = 1L;
        given(journalRepository.findById(testJournalId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> journalServiceUnderTest.getJournalWithComments(testJournalId))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("Journal not found");
        verify(journalRepository).findById(testJournalId);
    }

    @Test
    void deleteJournalSuccessfulForJournalAuthor() {
        // given
        Long testJournalId = 1L;
        String username = "testAuthor";
        List<SimpleGrantedAuthority> testGrantedAuth = new ArrayList<>();

        Journal testJournal = new Journal(
                username,
                "title",
                "content",
                LocalDateTime.now(),
                new AppUser()
        );

        given(journalRepository.findById(testJournalId)).willReturn(Optional.of(testJournal));

        // when
        journalServiceUnderTest.deleteJournal(testJournalId, username, testGrantedAuth);

        // then
        verify(journalRepository).delete(testJournal);
    }

    @Test
    void deleteJournalSuccessfulForUserWithJournalWritePermission() {
        // given
        Long testJournalId = 1L;
        String username = "testAdmin";
        List<SimpleGrantedAuthority> testGrantedAuth = List.of(
                new SimpleGrantedAuthority("journal:write")
        );

        Journal testJournal = new Journal(
                "author",
                "title",
                "content",
                LocalDateTime.now(),
                new AppUser()
        );

        given(journalRepository.findById(testJournalId)).willReturn(Optional.of(testJournal));

        // when
        journalServiceUnderTest.deleteJournal(testJournalId, username, testGrantedAuth);

        // then
        verify(journalRepository).delete(testJournal);
    }

    @Test
    void deleteJournalWillThrowExceptionWhenElementIsNotFound() {
        // given
        given(journalRepository.findById(1L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> journalServiceUnderTest.deleteJournal(1L, "username", new ArrayList<SimpleGrantedAuthority>()))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("Journal not found");
        verify(journalRepository, never()).delete(any());
    }

    @Test
    void deleteJournalWillThrowExceptionWhenNotAuthorOrNotEnoughPermission() {
        // given
        Journal testJournal = new Journal();
        testJournal.setAuthor("testAuthor");
        given(journalRepository.findById(1L)).willReturn(Optional.of(testJournal));

        // when
        // then
        assertThatThrownBy(() -> journalServiceUnderTest.deleteJournal(1L, "username", new ArrayList<SimpleGrantedAuthority>()))
                .isInstanceOf(NoPermissionException.class)
                .hasMessageContaining("You are not authorized to delete this journal");
        verify(journalRepository, never()).delete(any());
    }

    @Test
    void getJournalByIdSuccessful() {
        // given
        Journal testJournal = new Journal();
        given(journalRepository.findById(1L)).willReturn(Optional.of(testJournal));

        // when
        journalServiceUnderTest.getJournalById(1L);

        // then
        verify(journalRepository).findById(1L);
    }

    @Test
    void getJournalWillThrowExceptionWhenJournalIsNotFound() {
        // given
        given(journalRepository.findById(1L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> journalServiceUnderTest.getJournalById(1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("Journal not found");
        verify(journalRepository).findById(1L);
    }

}