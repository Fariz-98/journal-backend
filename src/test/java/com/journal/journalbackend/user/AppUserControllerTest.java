package com.journal.journalbackend.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.journalbackend.exceptions.ElementNotFoundException;
import com.journal.journalbackend.jwt.configuration.JwtConfig;
import com.journal.journalbackend.jwt.configuration.JwtSecretKey;
import com.journal.journalbackend.registration.RegistrationService;
import com.journal.journalbackend.user.payload.AppUserProfileResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppUserController.class)
class AppUserControllerTest {

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
    @WithMockUser(authorities = "USER")
    void getUserProfile() throws Exception {
        // given
        AppUserProfileResponse testAppUserProfileResponse = new AppUserProfileResponse(
                "username",
                "firstname",
                "lastname",
                new ArrayList<>()
        );

        given(appUserService.getUserProfile(1L)).willReturn(testAppUserProfileResponse);

        // when
        // then
        mockMvc.perform(get("/api/user/{userId}", 1L))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.username").value(testAppUserProfileResponse.getUsername()))
                .andExpect(jsonPath("$.firstName").value(testAppUserProfileResponse.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testAppUserProfileResponse.getLastName()))
                .andExpect(jsonPath("$.userJournal").value(testAppUserProfileResponse.getUserJournal()));
    }

    @Test
    @WithMockUser(authorities = "user:write")
    void changeUserRole() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(patch("/api/user/changeRole/{userId}?role={role}", 1L, "MODERATOR"))
                .andExpect(status().isNoContent());
    }

}