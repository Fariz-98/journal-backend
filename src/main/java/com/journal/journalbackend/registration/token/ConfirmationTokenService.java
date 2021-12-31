package com.journal.journalbackend.registration.token;

import com.journal.journalbackend.exceptions.DuplicateActionException;
import com.journal.journalbackend.exceptions.ElementNotFoundException;
import com.journal.journalbackend.exceptions.ElementExpiredException;
import com.journal.journalbackend.user.AppUser;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private ConfirmationToken generateConfirmationToken(AppUser appUser) {
        return new ConfirmationToken(
                generateToken(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
    }

    public String signUpToken(AppUser appUser) {
        ConfirmationToken confirmationToken = generateConfirmationToken(appUser);
        confirmationTokenRepository.save(confirmationToken);

        return confirmationToken.getToken();
    }

    @Transactional
    public AppUser verifyToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);

        if (confirmationToken == null) {
            throw new ElementNotFoundException("Token not found");
        }

        if (confirmationToken.getConfirmedAt() != null) {
            throw new DuplicateActionException("Email has already been confirmed");
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ElementExpiredException("Token expired");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        return confirmationToken.getAppUser();
    }

    @Transactional
    public String resendToken(AppUser appUser) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByAppUserId(appUser.getId());

        if (confirmationToken.getConfirmedAt() != null) {
            throw new DuplicateActionException("Email has already been confirmed");
        }

        String token = generateToken();

        confirmationToken.setToken(token);
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        // Send Email
        return token;
    }

}
