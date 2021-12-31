package com.journal.journalbackend.user;

import com.journal.journalbackend.exceptions.ElementNotFoundException;
import com.journal.journalbackend.exceptions.ElementTakenException;
import com.journal.journalbackend.exceptions.NoPermissionException;
import com.journal.journalbackend.registration.payload.RegistrationRequest;
import com.journal.journalbackend.user.payload.AppUserProfileResponse;
import com.journal.journalbackend.user.permissions.AppUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;


@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username);

        if (appUser != null) {
            return appUser;
        } else {
            throw new UsernameNotFoundException("Username not found");
        }
    }

    public AppUser registerUser(RegistrationRequest registrationRequest) {
        // User validation
        AppUser byEmail = appUserRepository.findByEmail(registrationRequest.getEmail());
        if (byEmail != null) {
            throw new ElementTakenException("Email taken");
        }

        AppUser byUsername = appUserRepository.findByUsername(registrationRequest.getUsername());
        if (byUsername != null) {
            throw new ElementTakenException("Username taken");
        }

        AppUser appUser = new AppUser(
                registrationRequest.getFirstName(),
                registrationRequest.getLastName(),
                registrationRequest.getEmail(),
                registrationRequest.getUsername(),
                passwordEncoder.encode(registrationRequest.getPassword())
        );


        if (appUserRepository.count() == 0) {
            appUser.setAppUserRole(AppUserRole.ADMIN);
        } else {
            appUser.setAppUserRole(AppUserRole.USER);
        }

        appUserRepository.save(appUser);
        return appUser;
    }

    @Transactional
    public void enableUser(AppUser appUser) {
        appUser.setEnabled(true);
    }

    public AppUser getUserByEmail(String email) {
        AppUser appUser = appUserRepository.findByEmail(email);

        if (appUser == null) {
            throw new ElementNotFoundException("User not found");
        }

        return appUser;
    }

    public AppUserProfileResponse getUserProfile(Long userId) {
        AppUser appUser = appUserRepository.findById(userId).orElse(null);

        if (appUser == null) {
            throw new ElementNotFoundException("User not found");
        }

        AppUserProfileResponse appUserProfileResponse = new AppUserProfileResponse(
                appUser.getUsername(),
                appUser.getFirstName(),
                appUser.getLastName(),
                appUser.getJournal()
        );

        return appUserProfileResponse;
    }

    @Transactional
    public void changeUserRole(Long userId, String role, Collection<? extends GrantedAuthority> authorities) {
        if (!authorities.contains(new SimpleGrantedAuthority("user:write"))) {
            throw new NoPermissionException("You are not authorized to do this action");
        }

        AppUserRole roleToUpdate;
        try {
            roleToUpdate = AppUserRole.valueOf(role);
        } catch (IllegalArgumentException ex) {
            throw new ElementNotFoundException("Specified role not found");
        }

        AppUser appUser = appUserRepository.findById(userId).orElse(null);
        if (appUser == null) {
            throw new ElementNotFoundException("User not found");
        }

        appUser.setAppUserRole(roleToUpdate);
    }

}
