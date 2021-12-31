package com.journal.journalbackend.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.journalbackend.jwt.configuration.JwtConfig;
import com.journal.journalbackend.jwt.configuration.JwtSecretKey;
import com.journal.journalbackend.registration.payload.IdAndTokenResponse;
import com.journal.journalbackend.registration.payload.RegistrationRequest;
import com.journal.journalbackend.user.AppUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private AppUserService appUserService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtConfig jwtConfig;
    @MockBean
    private JwtSecretKey jwtSecretKey;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void register() throws Exception {
        // given
        RegistrationRequest testRegistrationRequest = new RegistrationRequest(
                "username",
                "password",
                "email@email.com",
                "firstname",
                "lastname"
        );

        IdAndTokenResponse testResponse = new IdAndTokenResponse(1L, UUID.randomUUID().toString());
        given(registrationService.register(any(RegistrationRequest.class))).willReturn(testResponse);

        // when
        // then
        mockMvc.perform(post("/api/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/api/user/1"));
    }

    @Test
    void verifyToken() throws Exception {
        // given
        String token = UUID.randomUUID().toString();

        // when
        // then
        mockMvc.perform(patch("/api/registration/verifyToken?token={token}", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void resendToken() throws Exception {
        // given
        String testEmail = "test@test.com";
        String testToken = UUID.randomUUID().toString();
        given(registrationService.resendToken(testEmail)).willReturn(testToken);

        // when
        // then
        mockMvc.perform(get("/api/registration/resendToken?email={email}", testEmail))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").value(testToken));
    }

}