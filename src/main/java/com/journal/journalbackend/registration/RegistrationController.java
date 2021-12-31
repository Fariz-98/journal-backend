package com.journal.journalbackend.registration;

import com.journal.journalbackend.registration.payload.IdAndTokenResponse;
import com.journal.journalbackend.registration.payload.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping(path = "register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest registrationRequest) {
        IdAndTokenResponse idAndTokenResponse = registrationService.register(registrationRequest);

        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/{id}").buildAndExpand(idAndTokenResponse.getId()).toUri();
        return ResponseEntity.created(uri).body(idAndTokenResponse.getToken());
    }

    @PatchMapping(path = "verifyToken")
    public ResponseEntity<?> verifyToken(@RequestParam("token") String token) {
        registrationService.confirmToken(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "resendToken")
    public String resendToken(@RequestParam("email") String email) {
        return registrationService.resendToken(email);
    }

}
