package com.journal.journalbackend.registration;

import com.journal.journalbackend.configuration.EmailValidator;
import com.journal.journalbackend.exceptions.InvalidInputException;
import com.journal.journalbackend.registration.payload.IdAndTokenResponse;
import com.journal.journalbackend.registration.payload.RegistrationRequest;
import com.journal.journalbackend.registration.token.ConfirmationTokenService;
import com.journal.journalbackend.user.AppUser;
import com.journal.journalbackend.user.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private AppUserService appUserService;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private EmailValidator emailValidator;
    private RegistrationService registrationServiceUnderTest;

    @BeforeEach
    void setUp() {
        registrationServiceUnderTest = new RegistrationService(appUserService, confirmationTokenService, emailValidator);
    }

    @Test
    void registerSuccessful() {
        // given
        RegistrationRequest testRegistrationRequest = new RegistrationRequest(
                "username",
                "password",
                "test@test.com",
                "firstuser",
                "lastuser"
        );

        AppUser testUser = new AppUser(
                testRegistrationRequest.getFirstName(),
                testRegistrationRequest.getLastName(),
                testRegistrationRequest.getEmail(),
                testRegistrationRequest.getUsername(),
                testRegistrationRequest.getPassword()
        );
        testUser.setId(1L);

        String testToken = UUID.randomUUID().toString();

        given(emailValidator.validateEmail(testRegistrationRequest.getEmail())).willReturn(true);
        given(appUserService.registerUser(testRegistrationRequest)).willReturn(testUser);
        given(confirmationTokenService.signUpToken(testUser)).willReturn(testToken);

        // when
        IdAndTokenResponse returnedResponse = registrationServiceUnderTest.register(testRegistrationRequest);

        // then
        verify(emailValidator).validateEmail(testRegistrationRequest.getEmail());
        verify(appUserService).registerUser(testRegistrationRequest);
        verify(confirmationTokenService).signUpToken(testUser);

        assertThat(returnedResponse.getId()).isEqualTo(testUser.getId());
        assertThat(returnedResponse.getToken()).isEqualTo(testToken);
    }

    @Test
    void registerWillThrowExceptionWhenEmailIsInvalid() {
        // given
        String testEmail = "invalidTestEmail";
        RegistrationRequest testRegistrationRequest = new RegistrationRequest();
        testRegistrationRequest.setEmail(testEmail);
        given(emailValidator.validateEmail(testEmail)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> registrationServiceUnderTest.register(testRegistrationRequest))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Invalid Email");
        verify(emailValidator).validateEmail(testEmail);
    }

    @Test
    void confirmTokenSuccessful() {
        // given
        String testToken = UUID.randomUUID().toString();
        AppUser testUser = new AppUser();
        given(confirmationTokenService.verifyToken(testToken)).willReturn(testUser);

        // when
        registrationServiceUnderTest.confirmToken(testToken);

        // then
        verify(confirmationTokenService).verifyToken(testToken);
        verify(appUserService).enableUser(testUser);
    }

    @Test
    void resendTokenSuccessful() {
        // given
        String testEmail = "test@test.com";
        AppUser testUser = new AppUser();
        String testToken = UUID.randomUUID().toString();

        given(appUserService.getUserByEmail(testEmail)).willReturn(testUser);
        given(confirmationTokenService.resendToken(testUser)).willReturn(testToken);

        // when
        registrationServiceUnderTest.resendToken(testEmail);

        // then
        verify(appUserService).getUserByEmail(testEmail);
        verify(confirmationTokenService).resendToken(testUser);
    }

}