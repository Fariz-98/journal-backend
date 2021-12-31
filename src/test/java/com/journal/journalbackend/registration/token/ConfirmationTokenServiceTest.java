package com.journal.journalbackend.registration.token;

import com.journal.journalbackend.exceptions.DuplicateActionException;
import com.journal.journalbackend.exceptions.ElementExpiredException;
import com.journal.journalbackend.exceptions.ElementNotFoundException;
import com.journal.journalbackend.user.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository tokenRepository;
    private ConfirmationTokenService confirmationTokenServiceUnderTest;

    @BeforeEach
    void setUp() {
        confirmationTokenServiceUnderTest = new ConfirmationTokenService(tokenRepository);
    }

    @Test
    void generateAndSaveTokenWhenSigningUp() {
        // Given
        AppUser testAppUser = new AppUser();

        // when
        String token = confirmationTokenServiceUnderTest.signUpToken(testAppUser);

        // then
        ArgumentCaptor<ConfirmationToken> tokenArgumentCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
        verify(tokenRepository).save(tokenArgumentCaptor.capture());

        ConfirmationToken capturedToken = tokenArgumentCaptor.getValue();
        assertThat(capturedToken.getToken()).isEqualTo(token);
        assertThat(capturedToken.getAppUser()).isEqualTo(testAppUser);
    }

    @Test
    void verifyTokenSuccessful() {
        // Given
        AppUser testUser = new AppUser();

        String token = UUID.randomUUID().toString();
        ConfirmationToken testToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                testUser
        );
        given(tokenRepository.findByToken(token)).willReturn(testToken);

        // When
        AppUser appUser = confirmationTokenServiceUnderTest.verifyToken(token);

        // Then
        verify(tokenRepository).findByToken(token);
        assertThat(testToken.getConfirmedAt()).isNotNull();
        assertThat(appUser).isEqualTo(testUser);
    }

    @Test
    void verifyTokenWillThrowExceptionIfTokenNotFound() {
        // Given
        String testToken = "WrongTokenString";
        given(tokenRepository.findByToken(testToken)).willReturn(null);

        // When
        // Then
        assertThatThrownBy(() -> confirmationTokenServiceUnderTest.verifyToken(testToken))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("Token not found");
        verify(tokenRepository).findByToken(testToken);
    }

    @Test
    void verifyTokenWillThrowExceptionIfGetConfirmedAtIsNotNull() {
        // Given
        String token = UUID.randomUUID().toString();
        ConfirmationToken testToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                new AppUser()
        );
        testToken.setConfirmedAt(LocalDateTime.now());
        given(tokenRepository.findByToken(token)).willReturn(testToken);

        // when
        // then
        assertThatThrownBy(() -> confirmationTokenServiceUnderTest.verifyToken(token))
                .isInstanceOf(DuplicateActionException.class)
                .hasMessageContaining("Email has already been confirmed");
        verify(tokenRepository).findByToken(token);
    }

    @Test
    void verifyTokenWillThrowExceptionWhenConfirmingPastExpirationDate() {
        // given
        String token = UUID.randomUUID().toString();
        ConfirmationToken testToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().minusMinutes(15),
                new AppUser()
        );

        given(tokenRepository.findByToken(token)).willReturn(testToken);

        // when
        // then
        assertThatThrownBy(() -> confirmationTokenServiceUnderTest.verifyToken(token))
                .isInstanceOf(ElementExpiredException.class)
                .hasMessageContaining("Token expired");
        verify(tokenRepository).findByToken(token);
    }

    @Test
    void resendTokenSuccessful() {
        // given
        AppUser testUser = new AppUser();
        testUser.setId(1L);

        LocalDateTime oldCreatedAt = LocalDateTime.now().minusMinutes(1);
        LocalDateTime oldExpiresAt = LocalDateTime.now().plusMinutes(14);
        String oldToken = UUID.randomUUID().toString();
        ConfirmationToken testToken = new ConfirmationToken(
                oldToken,
                oldCreatedAt,
                oldExpiresAt,
                testUser
        );

        given(tokenRepository.findByAppUserId(testUser.getId())).willReturn(testToken);

        // when
        String newToken = confirmationTokenServiceUnderTest.resendToken(testUser);

        // then
        verify(tokenRepository).findByAppUserId(testUser.getId());
        assertThat(testToken.getToken()).isEqualTo(newToken);
        assertThat(testToken.getCreatedAt()).isAfter(oldCreatedAt);
        assertThat(testToken.getExpiresAt()).isAfter(oldExpiresAt);
    }

    @Test
    void resendTokenWillThrowExceptionIfGetConfirmedAtIsNotNull() {
        // given
        AppUser testUser = new AppUser();
        testUser.setId(1L);

        ConfirmationToken testToken = new ConfirmationToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                testUser
        );
        testToken.setConfirmedAt(LocalDateTime.now());

        given(tokenRepository.findByAppUserId(testUser.getId())).willReturn(testToken);

        // when
        // then
        assertThatThrownBy(() -> confirmationTokenServiceUnderTest.resendToken(testUser))
                .isInstanceOf(DuplicateActionException.class)
                .hasMessageContaining("Email has already been confirmed");
        verify(tokenRepository).findByAppUserId(testUser.getId());
    }

}


























