package com.journal.journalbackend.user;

import com.journal.journalbackend.exceptions.ElementNotFoundException;
import com.journal.journalbackend.exceptions.ElementTakenException;
import com.journal.journalbackend.exceptions.NoPermissionException;
import com.journal.journalbackend.registration.payload.RegistrationRequest;
import com.journal.journalbackend.user.payload.AppUserProfileResponse;
import com.journal.journalbackend.user.permissions.AppUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AppUserService appUserServiceUnderTest;

    @BeforeEach
    void setUp() {
        appUserServiceUnderTest = new AppUserService(appUserRepository, passwordEncoder);
    }

    @Test
    void loadUserByUsernameSuccessful() {
        // given
        String testUsername = "username";
        AppUser testUser = new AppUser(
                "firstuser",
                "lastuser",
                "test@test.com",
                testUsername,
                "password"
        );
        given(appUserRepository.findByUsername(testUsername)).willReturn(testUser);

        // when
        AppUser returnedAppUser = (AppUser) appUserServiceUnderTest.loadUserByUsername(testUsername);

        // then
        verify(appUserRepository).findByUsername(testUsername);
        assertThat(returnedAppUser).isEqualTo(testUser);
    }

    @Test
    void loadUserByUsernameWillThrowExceptionWhenUsernameIsNotFound() {
        // given
        String username = "WrongUsername";
        given(appUserRepository.findByUsername(username)).willReturn(null);

        // when
        // then
        assertThatThrownBy(() -> appUserServiceUnderTest.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Username not found");
        verify(appUserRepository).findByUsername(username);
    }

    @Test
    void registerUserForAdminSuccessful() {
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
        testUser.setAppUserRole(AppUserRole.ADMIN);

        given(appUserRepository.findByEmail(testRegistrationRequest.getEmail())).willReturn(null);
        given(appUserRepository.findByUsername(testRegistrationRequest.getUsername())).willReturn(null);
        given(passwordEncoder.encode(testRegistrationRequest.getPassword())).willReturn(testRegistrationRequest.getPassword());
        given(appUserRepository.count()).willReturn(0L);

        // when
        appUserServiceUnderTest.registerUser(testRegistrationRequest);

        // then
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(appUserArgumentCaptor.capture());

        AppUser capturedUser = appUserArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(testUser);
    }

    @Test
    void registerUserForNormalUserSuccessful() {
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
        testUser.setAppUserRole(AppUserRole.USER);

        given(appUserRepository.findByEmail(testRegistrationRequest.getEmail())).willReturn(null);
        given(appUserRepository.findByUsername(testRegistrationRequest.getUsername())).willReturn(null);
        given(passwordEncoder.encode(testRegistrationRequest.getPassword())).willReturn(testRegistrationRequest.getPassword());
        given(appUserRepository.count()).willReturn(1L);

        // when
        appUserServiceUnderTest.registerUser(testRegistrationRequest);

        // then
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(appUserArgumentCaptor.capture());

        AppUser capturedUser = appUserArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(testUser);
    }

    @Test
    void registerUserWillThrowExceptionWhenEmailIsTaken() {
        // given
        RegistrationRequest testRegistrationRequest = new RegistrationRequest();
        given(appUserRepository.findByEmail(testRegistrationRequest.getEmail())).willReturn(new AppUser());

        // when
        // then
        assertThatThrownBy(() -> appUserServiceUnderTest.registerUser(testRegistrationRequest))
                .isInstanceOf(ElementTakenException.class)
                .hasMessageContaining("Email taken");
        verify(appUserRepository).findByEmail(testRegistrationRequest.getEmail());
    }

    @Test
    void registerUserWillThrowExceptionWhenUsernameIsTaken() {
        // given
        RegistrationRequest testRegistrationRequest = new RegistrationRequest();
        given(appUserRepository.findByEmail(testRegistrationRequest.getEmail())).willReturn(null);
        given(appUserRepository.findByUsername(testRegistrationRequest.getUsername())).willReturn(new AppUser());

        // when
        // then
        assertThatThrownBy(() -> appUserServiceUnderTest.registerUser(testRegistrationRequest))
                .isInstanceOf(ElementTakenException.class)
                .hasMessageContaining("Username taken");
        verify(appUserRepository).findByEmail(testRegistrationRequest.getEmail());
        verify(appUserRepository).findByUsername(testRegistrationRequest.getUsername());
    }

    @Test
    void enableUserSuccessful() {
        // given
        AppUser testUser = new AppUser(
                "firstuser",
                "lastuser",
                "test@test.com",
                "username",
                "password"
        );

        // when
        appUserServiceUnderTest.enableUser(testUser);

        // then
        assertThat(testUser.getEnabled()).isTrue();
    }

    @Test
    void getUserByEmailSuccessful() {
        // given
        String testEmail = "test@test.com";
        AppUser testUser = new AppUser(
                "firstuser",
                "lastuser",
                testEmail,
                "username",
                "password"
        );
        given(appUserRepository.findByEmail(testEmail)).willReturn(testUser);

        // when
        AppUser appUser = appUserServiceUnderTest.getUserByEmail(testEmail);

        // then
        verify(appUserRepository).findByEmail(testEmail);
        assertThat(appUser).isEqualTo(testUser);
    }

    @Test
    void getUserByEmailWillThrowExceptionWhenUserIsNotFound() {
        // given
        String testEmail = "test@test.com";
        given(appUserRepository.findByEmail(testEmail)).willReturn(null);

        // when
        // then
        assertThatThrownBy(() -> appUserServiceUnderTest.getUserByEmail(testEmail))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(appUserRepository).findByEmail(testEmail);
    }

    @Test
    void getUserProfileSuccessful() {
        // given
        AppUser testUser = new AppUser(
                "firstuser",
                "lastuser",
                "test@test.com",
                "username",
                "password"
        );
        Long id = 1L;
        testUser.setId(id);
        testUser.setJournal(new ArrayList<>());

        AppUserProfileResponse testAppUserProfileResponse = new AppUserProfileResponse(
                testUser.getUsername(),
                testUser.getFirstName(),
                testUser.getLastName(),
                testUser.getJournal()
        );

        given(appUserRepository.findById(id)).willReturn(Optional.of(testUser));

        // when
        AppUserProfileResponse appUserProfileResponse = appUserServiceUnderTest.getUserProfile(id);

        // then
        verify(appUserRepository).findById(id);
        assertThat(appUserProfileResponse).isEqualTo(testAppUserProfileResponse);
    }

    @Test
    void getUserProfileWillThrowExceptionWhenUserIsNotFound() {
        // given
        Long id = 1L;
        given(appUserRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> appUserServiceUnderTest.getUserProfile(id))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(appUserRepository).findById(id);
    }

    @Test
    void changeUserRoleSuccessful() {
        // given
        String testRole = "USER";
        AppUserRole testRoleToUpdate = AppUserRole.valueOf(testRole);
        List<SimpleGrantedAuthority> testPermission = List.of(
                new SimpleGrantedAuthority("user:write")
        );

        Long testId = 1L;
        AppUser testUser = new AppUser(
                "firstuser",
                "lastuser",
                "test@test.com",
                "username",
                "password"
        );
        testUser.setAppUserRole(null);
        testUser.setId(testId);

        given(appUserRepository.findById(testId)).willReturn(Optional.of(testUser));

        // when
        appUserServiceUnderTest.changeUserRole(testId, testRole, testPermission);

        // then
        verify(appUserRepository).findById(testId);
        assertThat(testUser.getAppUserRole()).isEqualTo(testRoleToUpdate);
    }

    @Test
    void changeUserRoleWillThrowExceptionWithNoPermission() {
        // given
        Long testId = 1L;
        String testRole = "USER";
        List<SimpleGrantedAuthority> testPermission = new ArrayList<>();

        // when
        // then
        assertThatThrownBy(() -> appUserServiceUnderTest.changeUserRole(testId, testRole, testPermission))
                .isInstanceOf(NoPermissionException.class)
                .hasMessageContaining("You are not authorized to do this action");
    }

    @Test
    void changeUserRoleWillThrowExceptionWhenRoleDoesNotExist() {
        // given
        Long testId = 1L;
        String testRole = "norole";
        List<SimpleGrantedAuthority> testPermission = List.of(
                new SimpleGrantedAuthority("user:write")
        );

        // when
        // then
        assertThatThrownBy(() -> appUserServiceUnderTest.changeUserRole(testId, testRole, testPermission))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("Specified role not found");
    }

    @Test
    void changeUserRileWillThrowExceptionWhenUserIsNotFound() {
        // given
        Long testId = 1L;
        String testRole = "USER";
        List<SimpleGrantedAuthority> testPermission = List.of(
                new SimpleGrantedAuthority("user:write")
        );

        given(appUserRepository.findById(testId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> appUserServiceUnderTest.changeUserRole(testId, testRole, testPermission))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(appUserRepository).findById(testId);
    }

}






























