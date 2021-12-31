package com.journal.journalbackend.registration.token;

import com.journal.journalbackend.user.AppUser;
import com.journal.journalbackend.user.AppUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ConfirmationTokenRepositoryTest {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @AfterEach
    void tearDown() {
        confirmationTokenRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    void findByAppUserIdShouldReturnConfirmationToken() {
        // given
        AppUser testAppUser = new AppUser(
                1L,
                "firstname",
                "lastname",
                "email@email.com",
                "username",
                "password",
                true
        );

        String testToken = UUID.randomUUID().toString();

        ConfirmationToken testConfirmationToken = new ConfirmationToken(
                1L,
                testToken,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                testAppUser
        );

        appUserRepository.save(testAppUser);
        confirmationTokenRepository.save(testConfirmationToken);

        // when
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByAppUserId(1L);

        // then
        assertThat(confirmationToken).usingRecursiveComparison().isEqualTo(testConfirmationToken);
    }

}