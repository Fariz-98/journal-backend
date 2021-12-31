package com.journal.journalbackend.journal;

import com.journal.journalbackend.exceptions.ElementNotFoundException;
import com.journal.journalbackend.exceptions.NoPermissionException;
import com.journal.journalbackend.journal.payload.JournalRequest;
import com.journal.journalbackend.journal.payload.JournalWithCommentsResponse;
import com.journal.journalbackend.user.AppUser;
import com.journal.journalbackend.user.AppUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class JournalService {

    private final JournalRepository journalRepository;
    private final AppUserService appUserService;

    public JournalService(JournalRepository journalRepository, AppUserService appUserService) {
        this.journalRepository = journalRepository;
        this.appUserService = appUserService;
    }

    public List<Journal> getAllJournal() {
        return journalRepository.findAll();
    }

    public Long addNewJournalEntry(JournalRequest journalRequest, String username) {
        AppUser appUser = (AppUser) appUserService.loadUserByUsername(username);

        Journal journal = new Journal(
                username,
                journalRequest.getTitle(),
                journalRequest.getContent(),
                LocalDateTime.now(),
                appUser
        );

        journalRepository.save(journal);
        return journal.getId();
    }

    public JournalWithCommentsResponse getJournalWithComments(Long journalId) {
        Journal journal = journalRepository.findById(journalId).orElse(null);

        if (journal == null) {
            throw new ElementNotFoundException("Journal not found");
        }

        JournalWithCommentsResponse journalWithCommentsResponse = new JournalWithCommentsResponse(
                journal.getId(),
                journal.getAuthor(),
                journal.getTitle(),
                journal.getContent(),
                journal.getDateCreated(),
                journal.getComment()
        );

        return journalWithCommentsResponse;
    }

    public void deleteJournal(Long journalId, String username, Collection<? extends GrantedAuthority> authorities) {
        Journal journal = journalRepository.findById(journalId).orElse(null);

        if (journal == null) {
            throw new ElementNotFoundException("Journal not found");
        }

        if (username.equals(journal.getAuthor()) || authorities.contains(new SimpleGrantedAuthority("journal:write"))) {
            journalRepository.delete(journal);
        } else {
            throw new NoPermissionException("You are not authorized to delete this journal");
        }
    }

    public Journal getJournalById(Long journalId) {
        Journal journal = journalRepository.findById(journalId).orElse(null);

        if (journal == null) {
            throw new ElementNotFoundException("Journal not found");
        }

        return journal;
    }

}
