package com.journal.journalbackend.user;

import com.journal.journalbackend.user.payload.AppUserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping(path = "{userId}")
    public AppUserProfileResponse getUserProfile(@PathVariable Long userId) {
        return appUserService.getUserProfile(userId);
    }

    @PatchMapping(path = "changeRole/{userId}")
    public ResponseEntity<?> changeUserRole(@PathVariable Long userId, @RequestParam("role") String role, Authentication authentication) {
        appUserService.changeUserRole(userId, role, authentication.getAuthorities());
        return ResponseEntity.noContent().build();
    }

}
