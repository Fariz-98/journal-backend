package com.journal.journalbackend.registration;

import com.journal.journalbackend.configuration.EmailValidator;
import com.journal.journalbackend.exceptions.InvalidInputException;
import com.journal.journalbackend.registration.payload.IdAndTokenResponse;
import com.journal.journalbackend.registration.payload.RegistrationRequest;
import com.journal.journalbackend.registration.token.ConfirmationTokenService;
import com.journal.journalbackend.user.AppUser;
import com.journal.journalbackend.user.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RegistrationService {

    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailValidator emailValidator;

    @Autowired
    public RegistrationService(AppUserService appUserService, ConfirmationTokenService confirmationTokenService, EmailValidator emailValidator) {
        this.appUserService = appUserService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailValidator = emailValidator;
    }

    public IdAndTokenResponse register(RegistrationRequest registrationRequest) {
        if (!emailValidator.validateEmail(registrationRequest.getEmail())) {
            throw new InvalidInputException("Invalid Email");
        }

        AppUser appUser = appUserService.registerUser(registrationRequest);

        String token = confirmationTokenService.signUpToken(appUser);

        return new IdAndTokenResponse(appUser.getId(), token);
    }

    @Transactional
    public void confirmToken(String token) {
        AppUser appUser = confirmationTokenService.verifyToken(token);

        appUserService.enableUser(appUser);
    }

    public String resendToken(String email) {
        AppUser appUser = appUserService.getUserByEmail(email);

        return confirmationTokenService.resendToken(appUser);
    }

}
